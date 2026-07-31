package cn.icframework.dber;

import java.lang.reflect.Field;
import java.util.UUID;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import cn.icframework.mybatis.consts.IcParamsConsts;
import cn.icframework.mybatis.wrapper.SqlWrapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import cn.icframework.common.interfaces.IEnum;
import cn.icframework.core.common.helper.IndexResult;
import cn.icframework.core.common.helper.TableColumn;
import cn.icframework.core.utils.Assert;
import cn.icframework.mybatis.annotation.ForeignKey;
import cn.icframework.mybatis.annotation.ForeignKeyAction;
import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.Index;
import cn.icframework.mybatis.annotation.LogicDelete;
import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.annotation.TableField;
import cn.icframework.mybatis.annotation.Version;
import cn.icframework.mybatis.consts.IdType;
import cn.icframework.mybatis.consts.MysqlType;
import cn.icframework.mybatis.consts.MysqlTypeMap;
import cn.icframework.mybatis.utils.ModelClassUtils;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hzl
 * @since 2024/7/25
 */
@Component
@Slf4j
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class DDLHelper {

    @Resource
    private SqlSessionFactory sqlSessionFactory;
    /** 当前 DDL 批次使用的连接。连接由 sqlSession 持有，不能只关闭 Connection。 */
    private Connection conn;
    /** 通过 MyBatis 打开的会话，close 时必须和 Connection 一起释放。 */
    private SqlSession sqlSession;

    private List<String> sqlAfterRunList = new ArrayList<>();
    private List<Runnable> afterRunSuccessList = new ArrayList<>();

    public final static String SQL_CREATE_TEMPLATE = """
            CREATE TABLE IF NOT EXISTS `#TABLE_NAME` (
            #CONTENT
            #PRIMARY_KEY
            #INDEX
            #FOREIGN_KEY
            )  ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='#COMMENT';
            """;

    /**
     * 收尾工作。
     *
     * <p>外键和索引变更会延迟到所有实体扫描完成后执行。延迟 SQL 只要有一条失败，
     * 就不能执行成功回调（例如 DDL hash 写入），否则下一次启动会误以为数据库已经同步。</p>
     */
    public void close() {
        if (conn == null && sqlSession == null) {
            return;
        }

        RuntimeException failure = null;
        try {
            if (conn != null) {
                // 延迟 SQL 必须全部成功后，才能执行 afterRunSuccess 回调。
                try (Statement statement = conn.createStatement()) {
                    for (String sql : sqlAfterRunList) {
                        log.info(sql);
                        statement.execute(sql);
                    }
                }
                for (Runnable runnable : afterRunSuccessList) {
                    runnable.run();
                }
            }
        } catch (Exception e) {
            failure = new IllegalStateException("DDL 收尾阶段执行失败，应用启动已中止", e);
            log.error("DDL 收尾阶段执行失败", e);
        } finally {
            sqlAfterRunList.clear();
            afterRunSuccessList.clear();
            try {
                if (sqlSession != null) {
                    // SqlSession 负责管理 MyBatis 连接；只关闭 Connection 会泄漏会话资源。
                    sqlSession.close();
                } else if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                log.error("关闭 DDL 数据库资源失败", e);
                if (failure == null) {
                    failure = new IllegalStateException("关闭 DDL 数据库资源失败", e);
                } else {
                    failure.addSuppressed(e);
                }
            } finally {
                conn = null;
                sqlSession = null;
            }
        }
        if (failure != null) {
            throw failure;
        }
    }

    /**
     * 获取当前 DDL 批次的连接，并记录对应的 SqlSession，确保批次结束时完整释放资源。
     */
    private Connection getConnection() throws SQLException {
        if (conn == null) {
            sqlSession = sqlSessionFactory.openSession();
            conn = sqlSession.getConnection();
        }
        return conn;
    }

    public void afterRunSuccess(Runnable runnable) {
        afterRunSuccessList.add(runnable);
    }

    /**
     * 执行ddl
     *
     * @param entityType 实体类
     */
    public void runDDL(Class<?> entityType) throws SQLException {
        Table table = entityType.getAnnotation(Table.class);
        if (table == null || !StringUtils.hasText(table.value())) {
            throw new IllegalArgumentException(entityType.getName() + " 必须配置 @Table.value");
        }

        Connection connection = getConnection();
        String tableSchema = table.schema();
        String catalog = connection.getCatalog();
        try (Statement statement = connection.createStatement()) {
            try {
                if (StringUtils.hasLength(tableSchema)) {
                    // 切换到实体对应的数据库，保证 SHOW/ALTER 等未限定 schema 的语句作用于目标库。
                    statement.execute("USE " + quoteIdentifier(tableSchema));
                }
                try (ResultSet tableExist = statement.executeQuery(
                        "SHOW TABLES LIKE " + quoteSqlLiteral(table.value()))) {
                    if (tableExist.next()) {
                        updateTable(entityType, statement, table);
                    } else {
                        createTable(entityType, statement, table);
                    }
                }
            } finally {
                if (StringUtils.hasLength(tableSchema) && StringUtils.hasText(catalog)) {
                    // 一个 DDL 批次可能处理多个 schema，执行完当前实体后恢复原数据库。
                    statement.execute("USE " + quoteIdentifier(catalog));
                }
            }
        }
    }
    /**
     * 根据实体上的 {@link Table} 和查询 SQL 创建或更新视图。
     *
     * <p>{@code sqlWrapper} 只需要提供视图的查询部分，例如
     * {@code SELECT ... FROM ...}；视图名称和 {@code CREATE OR REPLACE VIEW}
     * 由本方法统一补齐。</p>
     *
     * @param entityType 实体类
     * @param sqlWrapper 视图查询 SQL
     */
    public void runViewDDL(Class<?> entityType, SqlWrapper sqlWrapper) throws SQLException {
        if (sqlWrapper == null) {
            throw new IllegalArgumentException("AutoView SQL 不能为空");
        }

        Table table = entityType.getAnnotation(Table.class);
        if (table == null || !StringUtils.hasText(table.value())) {
            throw new IllegalArgumentException(entityType.getName() + " 视图实体必须配置 @Table.value");
        }

        getConnection();
        String tableSchema = table.schema();
        String catalog = conn.getCatalog();
        try (Statement statement = conn.createStatement()) {
            if (StringUtils.hasLength(tableSchema)) {
                // 切换到视图对应的数据库，保证未带 schema 的 FROM/JOIN 仍然在目标库执行。
                statement.execute("USE " + quoteIdentifier(tableSchema));
            }

            String viewSql = renderViewSql(sqlWrapper);
            if (!StringUtils.hasText(viewSql)) {
                throw new IllegalArgumentException(entityType.getName() + " AutoView 返回的 SQL 不能为空");
            }
            viewSql = trimSqlTerminator(viewSql);

            String sql = "CREATE OR REPLACE VIEW "
                    + quoteIdentifier(table.value())
                    + " AS "
                    + viewSql;
            log.info(sql);
            statement.execute(sql);
        } finally {
            if (StringUtils.hasLength(tableSchema) && StringUtils.hasText(catalog)) {
                // 切换回默认数据库
                try (Statement statement = conn.createStatement()) {
                    statement.execute("USE " + quoteIdentifier(catalog));
                }
            }
        }
    }

    /**
     * 将 SqlWrapper 中的 MyBatis 参数替换成视图定义可以持久化的 SQL 字面量。
     *
     * <p>视图定义不能保留 {@code #{params.xxx}} 这类运行时绑定参数，
     * 因此 AutoView 中使用的参数必须在创建视图时固化到视图 SQL 中。</p>
     */
    private static String renderViewSql(SqlWrapper sqlWrapper) {
        String sql = sqlWrapper.sql();
        for (Map.Entry<String, Object> entry : sqlWrapper.getParams().entrySet()) {
            String placeholder = IcParamsConsts.GET_PARAM_S(entry.getKey());
            sql = sql.replace(placeholder, toSqlLiteral(entry.getValue()));
        }
        if (sql.contains("#{")) {
            throw new IllegalArgumentException("AutoView SQL 中存在未解析的 MyBatis 参数: " + sql);
        }
        return sql;
    }

    private static String toSqlLiteral(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof IEnum iEnum) {
            return Integer.toString(iEnum.code());
        }
        if (value instanceof Boolean bool) {
            return bool ? "1" : "0";
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof byte[] bytes) {
            StringBuilder hex = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return "X'" + hex + "'";
        }
        if (value instanceof Enum<?> enumValue) {
            return quoteSqlLiteral(enumValue.name());
        }
        if (value instanceof CharSequence
                || value instanceof Character
                || value instanceof UUID
                || value instanceof java.util.Date
                || value instanceof java.time.temporal.TemporalAccessor) {
            return quoteSqlLiteral(value.toString());
        }
        // 未知类型按字符串处理，避免把对象的 toString 直接当成 SQL 片段执行。
        return quoteSqlLiteral(value.toString());
    }

    private static String quoteSqlLiteral(String value) {
        return "'" + value.replace("\\", "\\\\").replace("'", "''") + "'";
    }

    private static String quoteIdentifier(String identifier) {
        return "`" + identifier.replace("`", "``") + "`";
    }

    private static String trimSqlTerminator(String sql) {
        String result = sql.trim();
        while (result.endsWith(";")) {
            result = result.substring(0, result.length() - 1).trim();
        }
        return result;
    }

    /**
     * 创建表
     *
     * @param entityType 实体类
     * @param table      表注解
     * @param statement  语句
     */
    private void createTable(Class<?> entityType, Statement statement, Table table) throws SQLException {
        Index index = entityType.getDeclaredAnnotation(Index.class);
        Index[] indexes = table.indexes();

        // 创建索引
        Set<String> indexSet = new HashSet<>();
        if (index != null) {
            setIndexSet(indexSet, index);
        }
        setIndexSet(indexSet, indexes);

        List<ModelClassUtils.FieldAndAnnotation<ForeignKey>> foreignKeys = ModelClassUtils.getFiledAnnotations(entityType, ForeignKey.class);
        // 创建外键
        Set<String> foreignKeySet = new HashSet<>();
        getForeignKeySet(entityType, table, foreignKeys, foreignKeySet);

        // 构建表字段
        Set<String> fields = new HashSet<>();
        String key = getTableFieldsContent(table, entityType, fields);
        // 生成建表语句
        String sql = SQL_CREATE_TEMPLATE
                // 使用 replace 而不是 replaceAll，避免表名、注释中的 $ 或反斜杠被当作正则替换语法。
                .replace("#TABLE_NAME", table.value())
                .replace("#CONTENT", String.join(",\n", fields))
                .replace("#COMMENT", table.comment().replace("\\", "\\\\").replace("'", "''"))
                .replace("#PRIMARY_KEY", StringUtils.hasLength(key) ? "," + key : "")
                .replace("#FOREIGN_KEY", "")
                .replace("#INDEX", indexSet.isEmpty() ? "" : "," + String.join(",\n", indexSet));
        log.info(sql);
        statement.execute(sql);
        for (String fkSql : foreignKeySet) {
            sqlAfterRunList.add(String.format("ALTER TABLE %s ADD %s;", table.value(), fkSql));
        }
    }

    private static void setIndexSet(Set<String> indexSet, Index... indices) {
        for (Index index : indices) {
            if (index.unique()) {
                indexSet.add(String.format("CONSTRAINT %s UNIQUE (%s)", index.name(), String.join(",", index.columns())));
            } else {
                indexSet.add(String.format("INDEX %s (%s)", index.name(), String.join(",", index.columns())));
            }
        }
    }

    /**
     * 根据字段上的 @ForeignKey 生成外键 SQL。
     *
     * <p>Table.foreignKeys() 目前没有描述本地字段的属性，因此无法独立生成外键；
     * DDLHelper 统一以字段上的 @ForeignKey 为准。</p>
     */
    private static Set<String> getForeignKeySet(Class<?> entityType, Table table,
                                                List<ModelClassUtils.FieldAndAnnotation<ForeignKey>> fieldAndAnnotations,
                                                Set<String> foreignKeySet) {
        Set<String> nameSet = new HashSet<>();
        for (ModelClassUtils.FieldAndAnnotation<ForeignKey> fa : fieldAndAnnotations) {
            Assert.isTrue(
                    fa.getField().isAnnotationPresent(TableField.class) || fa.getField().isAnnotationPresent(Id.class),
                    entityType.getName() + ":" + fa.getField().getName() + "非数据库字段，不可添加外建注解");

            Table referencedTable = fa.getAnnotation().references().getAnnotation(Table.class);
            Assert.isNotNull(referencedTable, "外键引用的实体类未添加 @Table 注解");

            String fkName = fa.getAnnotation().name();
            if (!StringUtils.hasLength(fkName)) {
                fkName = "FK_" + table.value() + "_" + ModelClassUtils.getTableColumnName(table, fa.getField());
            }
            nameSet.add(fkName);
            String referencesColumn;
            if (StringUtils.hasLength(fa.getAnnotation().referencesColumn())) {
                referencesColumn = fa.getAnnotation().referencesColumn();
            } else {
                // 引用列必须按被引用实体的 @Table 规则计算，不能误用当前表的命名规则。
                Field referencedId = ModelClassUtils.getIdField(fa.getAnnotation().references());
                Assert.isNotNull(referencedId, fa.getAnnotation().references().getName() + " 外键引用实体必须存在 @Id 字段");
                referencesColumn = ModelClassUtils.getTableColumnName(referencedTable, referencedId);
            }
            String referencedTableName = StringUtils.hasLength(referencedTable.schema())
                    ? quoteIdentifier(referencedTable.schema()) + "." + quoteIdentifier(referencedTable.value())
                    : quoteIdentifier(referencedTable.value());
            String onDelete = Objects.equals(fa.getAnnotation().onDelete(), ForeignKeyAction.NONE) ? "" : " ON DELETE " + fa.getAnnotation().onDelete();
            String onUpdate = Objects.equals(fa.getAnnotation().onUpdate(), ForeignKeyAction.NONE) ? "" : " ON UPDATE " + fa.getAnnotation().onUpdate();
            foreignKeySet.add(String.format("CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s (%s)%s%s",
                    quoteIdentifier(fkName),
                    quoteIdentifier(ModelClassUtils.getTableColumnName(table, fa.getField())),
                    referencedTableName,
                    quoteIdentifier(referencesColumn),
                    onDelete,
                    onUpdate));
        }
        return nameSet;
    }


    /**
     * 更新表
     *
     * @param entityType 实体类
     * @param statement  语句
     * @param table      表注解
     */
    private void updateTable(Class<?> entityType, Statement statement, Table table) throws SQLException {
        updateTableColumn(statement, entityType, table);
        Set<String> fkNameSet = updateTableForeignKey(statement, entityType, table);
        updateTableIndex(statement, entityType, table, fkNameSet);
    }

    /**
     * 更新表外键
     *
     * @param entityType 实体类
     * @param statement  语句
     * @param table      表注解
     */
    private Set<String> updateTableForeignKey(Statement statement, Class<?> entityType, Table table) throws SQLException {
        // 创建外键
        Map<String, String> foreignKeySortMap = new HashMap<>();
        List<ModelClassUtils.FieldAndAnnotation<ForeignKey>> fieldAndAnnotations = ModelClassUtils.getFiledAnnotations(entityType, ForeignKey.class);
        // 创建外键
        Set<String> foreignKeySet = new HashSet<>();
        Set<String> foreignKeyNameSet = getForeignKeySet(entityType, table, fieldAndAnnotations, foreignKeySet);
        String currentSchema = statement.getConnection().getCatalog();
        for (String foreignKeySql : foreignKeySet) {
            foreignKeySortMap.put(normalizeForeignKeySql(foreignKeySql, currentSchema), foreignKeySql);
        }

        // 获取已有外键
        List<String> dropSqls = new ArrayList<>();
        Map<String, String> existForeignKeyMap = getExistForeignKeyMap(statement, table.value());
        for (Map.Entry<String, String> entry : existForeignKeyMap.entrySet()) {
            String normalizedSql = normalizeForeignKeySql(entry.getValue(), currentSchema);
            if (foreignKeySortMap.containsKey(normalizedSql)) {
                foreignKeySortMap.remove(normalizedSql);
                continue;
            }
            String dropSql = String.format("ALTER TABLE %s DROP FOREIGN KEY %s;", table.value(), entry.getKey());
            dropSqls.add(dropSql);
        }
        sqlAfterRunList.addAll(dropSqls);
        for (Map.Entry<String, String> stringStringEntry : foreignKeySortMap.entrySet()) {
            String sql = String.format("ALTER TABLE %s ADD %s;", table.value(), stringStringEntry.getValue());
            sqlAfterRunList.add(sql);
        }
        return foreignKeyNameSet;
    }

    private Map<String, String> getExistForeignKeyMap(Statement statement, String tableName) throws SQLException {
        String sql = """
                SELECT
                  kcu.CONSTRAINT_NAME,
                  kcu.COLUMN_NAME,
                  kcu.REFERENCED_TABLE_SCHEMA,
                  kcu.REFERENCED_TABLE_NAME,
                  kcu.REFERENCED_COLUMN_NAME,
                  rc.DELETE_RULE,
                  rc.UPDATE_RULE
                FROM information_schema.KEY_COLUMN_USAGE kcu
                JOIN information_schema.REFERENTIAL_CONSTRAINTS rc
                  ON rc.CONSTRAINT_SCHEMA = kcu.CONSTRAINT_SCHEMA
                 AND rc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME
                 AND rc.TABLE_NAME = kcu.TABLE_NAME
                WHERE kcu.TABLE_SCHEMA = DATABASE()
                  AND kcu.TABLE_NAME = '%s'
                  AND kcu.REFERENCED_TABLE_NAME IS NOT NULL
                ORDER BY kcu.CONSTRAINT_NAME, kcu.ORDINAL_POSITION
                """.formatted(tableName);
        Map<String, List<ForeignKeyColumn>> columnMap = new HashMap<>();
        try (ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String fkName = resultSet.getString("CONSTRAINT_NAME");
                ForeignKeyColumn column = new ForeignKeyColumn();
                column.setColumnName(resultSet.getString("COLUMN_NAME"));
                column.setReferencedTableSchema(resultSet.getString("REFERENCED_TABLE_SCHEMA"));
                column.setReferencedTableName(resultSet.getString("REFERENCED_TABLE_NAME"));
                column.setReferencedColumnName(resultSet.getString("REFERENCED_COLUMN_NAME"));
                column.setDeleteRule(resultSet.getString("DELETE_RULE"));
                column.setUpdateRule(resultSet.getString("UPDATE_RULE"));
                columnMap.computeIfAbsent(fkName, k -> new ArrayList<>()).add(column);
            }
        }
        Map<String, String> foreignKeyMap = new HashMap<>();
        for (Map.Entry<String, List<ForeignKeyColumn>> entry : columnMap.entrySet()) {
            List<ForeignKeyColumn> columns = entry.getValue();
            ForeignKeyColumn first = columns.get(0);
            String columnNames = columns.stream()
                    .map(ForeignKeyColumn::getColumnName)
                    .map(name -> "`" + name + "`")
                    .collect(Collectors.joining(","));
            String referencedColumnNames = columns.stream()
                    .map(ForeignKeyColumn::getReferencedColumnName)
                    .map(name -> "`" + name + "`")
                    .collect(Collectors.joining(","));
            String onDelete = "NONE".equalsIgnoreCase(first.getDeleteRule()) ? "" : " ON DELETE " + first.getDeleteRule();
            String onUpdate = "NONE".equalsIgnoreCase(first.getUpdateRule()) ? "" : " ON UPDATE " + first.getUpdateRule();
            String referencedTable = StringUtils.hasLength(first.getReferencedTableSchema())
                    ? quoteIdentifier(first.getReferencedTableSchema()) + "." + quoteIdentifier(first.getReferencedTableName())
                    : quoteIdentifier(first.getReferencedTableName());
            foreignKeyMap.put(entry.getKey(), String.format("CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s (%s)%s%s",
                    quoteIdentifier(entry.getKey()),
                    columnNames,
                    referencedTable,
                    referencedColumnNames,
                    onDelete,
                    onUpdate));
        }
        return foreignKeyMap;
    }

    private String normalizeForeignKeySql(String sql, String currentSchema) {
        String normalized = sql.toLowerCase()
                .replaceAll("\\s+", "")
                .replace("`", "")
                .replace("ondeletenoaction", "")
                .replace("ondeleterestrict", "")
                .replace("onupdatenoaction", "")
                .replace("onupdaterestrict", "")
                .trim();
        // 当前库显式限定和未限定在 MySQL 中语义相同，统一后避免每次启动重复重建外键；
        // 其他 schema 则保留限定名，以便真正的跨库引用变化能够触发同步。
        if (StringUtils.hasText(currentSchema)) {
            normalized = normalized.replace(currentSchema.toLowerCase() + ".", "");
        }
        return normalized;
    }


    /**
     * 更新表索引
     *
     * @param statement statement
     */
    private void updateTableIndex(Statement statement, Class<?> entityType, Table table, Set<String> fkNameSet) throws SQLException {
        Index index = entityType.getDeclaredAnnotation(Index.class);
        Index[] indexes = table.indexes();

        Set<String> indexSet = new HashSet<>();
        if (index != null) {
            indexSet.add(String.format("CREATE%S INDEX %s USING BTREE ON %s (%s);", index.unique() ? " UNIQUE" : "", index.name(), table.value(), String.join(",", index.columns())));
        }
        for (Index i : indexes) {
            indexSet.add(String.format("CREATE%s INDEX %s USING BTREE ON %s (%s);", i.unique() ? " UNIQUE" : "", i.name(), table.value(), String.join(",", i.columns())));
        }
        // 获取已有索引
        List<IndexResult> indexResults = new ArrayList<>();
        try (ResultSet indexResultSet = statement.executeQuery("SHOW INDEX FROM " + table.value() + ";")) {
            while (indexResultSet.next()) {
                if (indexResultSet.getString("Key_name").equals("PRIMARY") || fkNameSet.contains(indexResultSet.getString("Key_name"))) {
                    continue;
                }
                IndexResult indexResult = new IndexResult();
                indexResult.setIndex(indexResultSet.getInt("Seq_in_index"));
                indexResult.setName(indexResultSet.getString("Key_name"));
                indexResult.setColumn(indexResultSet.getString("Column_name"));
                indexResult.setUnique(!indexResultSet.getBoolean("Non_unique"));
                indexResults.add(indexResult);
            }
        }

        Map<String, List<IndexResult>> indexNameGroup = indexResults.stream().collect(Collectors.groupingBy(IndexResult::getName));

        for (Map.Entry<String, List<IndexResult>> entry : indexNameGroup.entrySet()) {
            String name = entry.getKey();
            List<IndexResult> columns = entry.getValue();
            columns.sort(Comparator.comparingInt(IndexResult::getIndex));
            String indexAddSql = String.format("CREATE%s INDEX %s USING BTREE ON %s (%s);",
                    entry.getValue().get(0).isUnique() ? " UNIQUE" : "",
                    name,
                    table.value(),
                    columns.stream().map(IndexResult::getColumn).collect(Collectors.joining(",")));
            if (!indexSet.contains(indexAddSql)) {
                String dropIndexSql = String.format("ALTER TABLE %s DROP INDEX %s;", table.value(), name);
                sqlAfterRunList.add(dropIndexSql);
            } else {
                indexSet.remove(indexAddSql);
            }
        }

        if (!indexSet.isEmpty()) {
            sqlAfterRunList.addAll(indexSet);
        }
    }

    /**
     * 更新表字段
     *
     * @param entityType
     * @param statement
     * @param table
     * @throws SQLException
     */
    private void updateTableColumn(Statement statement, Class<?> entityType, Table table) throws SQLException {
        Map<String, TableColumn> existField = new HashMap<>();
        try (ResultSet resultSet = statement.executeQuery("show columns from " + table.value() + ";")) {
            while (resultSet.next()) {
                TableColumn tableColumn = new TableColumn();
                tableColumn.setType(resultSet.getString("Type"));
                tableColumn.setField(resultSet.getString("Field"));
                tableColumn.setDefaultValue(resultSet.getString("Default"));
                tableColumn.setNotNull("NO".equals(resultSet.getString("Null")));
                tableColumn.setAutoIncrement(resultSet.getString("Extra") != null
                        && resultSet.getString("Extra").toLowerCase().contains("auto_increment"));
                existField.put(tableColumn.getField(), tableColumn);
            }
        }
        Set<String> sqls = getTableAddFields(table, entityType, existField);
        for (String sql : sqls) {
            log.info(sql);
            statement.execute(sql);
        }
        // 字段新增与主键同步分开处理，避免复合主键被拆成多个 ADD PRIMARY KEY。
        reconcilePrimaryKey(statement, table, entityType);
    }

    /**
     * 将数据库主键调整为实体上声明的主键，主键字段一次性合并，支持复合主键。
     */
    private void reconcilePrimaryKey(Statement statement, Table table, Class<?> entityType) throws SQLException {
        List<String> desiredColumns = getPrimaryKeyColumns(table, entityType);
        List<String> existingColumns = new ArrayList<>();
        try (ResultSet resultSet = statement.executeQuery("SHOW INDEX FROM " + table.value() + ";")) {
            while (resultSet.next()) {
                if ("PRIMARY".equalsIgnoreCase(resultSet.getString("Key_name"))) {
                    existingColumns.add(resultSet.getString("Column_name"));
                }
            }
        }
        if (existingColumns.equals(desiredColumns)) {
            return;
        }
        if (!existingColumns.isEmpty()) {
            String sql = "ALTER TABLE " + table.value() + " DROP PRIMARY KEY";
            log.info(sql);
            statement.execute(sql);
        }
        if (!desiredColumns.isEmpty()) {
            String columns = desiredColumns.stream()
                    .map(DDLHelper::quoteIdentifier)
                    .collect(Collectors.joining(","));
            String sql = "ALTER TABLE " + table.value() + " ADD PRIMARY KEY (" + columns + ")";
            log.info(sql);
            statement.execute(sql);
        }
    }

    private List<String> getPrimaryKeyColumns(Table table, Class<?> entityClass) {
        List<String> columns = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        do {
            for (Field field : entityClass.getDeclaredFields()) {
                if (field.getDeclaredAnnotation(Id.class) == null) {
                    continue;
                }
                String column = ModelClassUtils.getTableColumnName(table, field);
                if (visited.add(column)) {
                    columns.add(column);
                }
            }
            entityClass = entityClass.getSuperclass();
        } while (entityClass != null && entityClass != Object.class);
        return columns;
    }


    /**
     * 遍历全部字段
     * 包括继承的
     *
     * @param entityClass
     * @param fields
     * @return
     */
    private String getTableFieldsContent(Table table, Class<?> entityClass, Set<String> fields) {
        List<String> sqlKeys = new ArrayList<>();
        Set<String> columnsSet = new HashSet<>();
        do {
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                TableField tableField = getTableField(declaredField, entityClass.getTypeName());
                Id id = declaredField.getDeclaredAnnotation(Id.class);
                Version version = declaredField.getDeclaredAnnotation(Version.class);
                LogicDelete logicDelete = declaredField.getDeclaredAnnotation(LogicDelete.class);
                if (version != null) {
                    // 获取字段的类型
                    Class<?> fieldType = declaredField.getType();
                    Assert.isTrue(fieldType == Long.class || fieldType == long.class,
                            entityClass.getName() + "." + declaredField.getName() + " @Version 字段必须是 Long 或 long 类型");
                }

                if (tableField == null && id == null && version == null && logicDelete == null) {
                    continue;
                }
                String fieldName = ModelClassUtils.getColumnName(table, tableField, declaredField.getName());
                if (columnsSet.contains(fieldName)) {
                    continue;
                }
                columnsSet.add(fieldName);
                String sqlType = getSqlType(declaredField, tableField);

                // 是否限制非空
                boolean notNull = tableField != null && tableField.notNull();
                String sql = getSql(tableField, id, version, logicDelete, declaredField, fieldName, sqlType, notNull);
                fields.add(sql);
                if (id != null) {
                    sqlKeys.add(fieldName);
                }
            }
            entityClass = entityClass.getSuperclass();
        } while (entityClass != null);
        if (!sqlKeys.isEmpty()) {
            return " PRIMARY KEY ( `" + String.join("`,`", sqlKeys) + "` )";
        }
        return null;
    }

    private static String getSql(
            TableField tableField,
            Id id,
            Version version,
            LogicDelete logicDelete,
            Field declaredField,
            String fieldName,
            String sqlType,
            boolean notNull) {
        if (logicDelete != null) {
            return " `" + fieldName + "` " + sqlType + " NOT NULL DEFAULT 0 COMMENT '逻辑删除'";
        }
        if (version != null) {
            return " `" + fieldName + "` " + sqlType + " NOT NULL DEFAULT 1 COMMENT '乐观锁'";
        }
        String nullAble = notNull ? " NOT NULL " : " NULL ";
        String defaultValue = "";
        if (tableField != null && StringUtils.hasLength(tableField.defaultValue())) {
            Class<?> fieldType = declaredField.getType();
            if (fieldType == String.class) {
                defaultValue = " DEFAULT '" + tableField.defaultValue() + "' ";
            } else {
                defaultValue = " DEFAULT " + tableField.defaultValue() + " ";
            }
        }
        String comment = tableField != null && StringUtils.hasLength(tableField.comment()) ? " COMMENT '" + tableField.comment() + "'" : "";
        if (id != null) {
            return " `" + fieldName + "` " + sqlType + " NOT NULL " + (id.idType() == IdType.AUTO ? "AUTO_INCREMENT" : "") + comment;
        } else {
            return " `" + fieldName + "` " + sqlType + nullAble + defaultValue + comment;
        }
    }

    /**
     * 遍历全部字段
     * 包括继承的
     *
     * @param entityClass
     * @return
     */
    /**
     * 计算字段新增/修改 SQL。
     *
     * <p>自增属性属于列定义的一部分，必须和类型、非空、默认值一起比较；
     * 主键则由 reconcilePrimaryKey 统一处理，避免复合主键被拆成多个语句。</p>
     */
    private Set<String> getTableAddFields(Table table, Class<?> entityClass, Map<String, TableColumn> existFields) {
        Set<String> sqls = new HashSet<>();
        String tableName = table.value();
        do {
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                TableField tableField = getTableField(declaredField, entityClass.getTypeName());
                Id id = declaredField.getDeclaredAnnotation(Id.class);
                Version version = declaredField.getDeclaredAnnotation(Version.class);
                LogicDelete logicDelete = declaredField.getDeclaredAnnotation(LogicDelete.class);
                if (version != null) {
                    // 获取字段的类型
                    Class<?> fieldType = declaredField.getType();
                    Assert.isTrue(fieldType == Long.class || fieldType == long.class,
                            entityClass.getName() + "." + declaredField.getName() + " @Version 字段必须是 Long 或 long 类型");
                }
                if (tableField == null && id == null && version == null && logicDelete == null) {
                    continue;
                }
                // 是否限制非空
                boolean notNull = (tableField != null && tableField.notNull()) || id != null;
                String sqlType = getSqlType(declaredField, tableField);
                String fieldName = ModelClassUtils.getColumnName(table, tableField, declaredField.getName());
                String sql = getSql(tableField, id, version, logicDelete, declaredField, fieldName, sqlType, notNull);
                if (existFields.containsKey(fieldName)) {
                    if (!existFields.get(fieldName).getType().startsWith(sqlType)
                            || existFields.get(fieldName).isNotNull() != notNull
                            || (tableField == null && existFields.get(fieldName).getDefaultValue() != null)
                            || !equalsDefaultValue(tableField, existFields.get(fieldName))
                            || existFields.get(fieldName).isAutoIncrement() != (id != null && id.idType() == IdType.AUTO)
                    ) {
                        sqls.add("ALTER TABLE " + tableName + " MODIFY COLUMN " + sql);
                    }
                    continue;
                }
                sqls.add("ALTER TABLE " + tableName + " ADD " + sql);
            }


            entityClass = entityClass.getSuperclass();
        } while (entityClass != null);
        return sqls;
    }

    private boolean equalsDefaultValue(TableField tableField, TableColumn tableColumn) {
        // 字段没有标注默认类型且数据库也没有，返回true
        if ((tableField == null || !StringUtils.hasLength(tableField.defaultValue())) && !StringUtils.hasLength(tableColumn.getDefaultValue())) {
            return true;
        }
        // 字段有标注默认类型，且数据库有默认类型，并且一致返回true
        if ((tableField != null && StringUtils.hasLength(tableField.defaultValue())) && StringUtils.hasLength(tableColumn.getDefaultValue())) {
            String defaultValue = tableField.defaultValue();
            String tableDefaultValue = tableColumn.getDefaultValue();
            return tableDefaultValue.equals(defaultValue)
                    || (tableDefaultValue.equals("b'0'") && (defaultValue.equals("0") || defaultValue.equals("false")))
                    || (tableDefaultValue.equals("b'1'") && (defaultValue.equals("1") || defaultValue.equals("true")));
        }
        return false;
    }

    @Nullable
    private static TableField getTableField(Field declaredField,
                                            String classTypeName) {
        TableField tableField = declaredField.getDeclaredAnnotation(TableField.class);
        LogicDelete logicDelete = declaredField.getDeclaredAnnotation(LogicDelete.class);
        if (logicDelete != null) {
            Assert.isNotNull(tableField, classTypeName + " 逻辑删除字段需要配合 @TableField 注解使用");
            Assert.isFalse(tableField.notNull() && !StringUtils.hasLength(tableField.defaultValue()), classTypeName + " 逻辑删除字段notNull时，默认 defaultValue 值必填");

            Class<?> fieldType = declaredField.getType();
            Assert.isTrue(fieldType == Boolean.class
                    || fieldType == boolean.class
                    || fieldType == Integer.class
                    || fieldType == int.class, classTypeName + " 逻辑删除字段只能是 boolean 或者 int 类型");

        }
        return tableField;
    }

    private static String getSqlType(Field declaredField, TableField tableField) {
        MysqlType type = null;
        if (tableField != null && StringUtils.hasLength(tableField.type())) {
            type = new MysqlType(tableField.type(), tableField.length(), tableField.fraction(), tableField.defaultValue());
        }
        if (type == null) {
            type = MysqlTypeMap.getType(declaredField.getType().toString().replace("class ", ""));
            if (type == null) {
                // 如果是枚举的活默用整数，IC框架限制
                if (IEnum.class.isAssignableFrom(declaredField.getType())) {
                    type = MysqlTypeMap.getType(short.class.toString());
                } else {
                    type = MysqlTypeMap.getType(String.class.toString());
                }
            }
        }
        if (type != null && tableField != null && type.getLength() != null && tableField.length() != -1) {
            type.setLength(tableField.length());
            if (type.getFraction() != null && tableField.fraction() != -1) {
                type.setFraction(tableField.fraction());
            }
        }
        assert type != null;
        return type.toString();
    }


    @Getter
    @Setter
    static class ForeignKeyColumn {
        private String columnName;
        private String referencedTableSchema;
        private String referencedTableName;
        private String referencedColumnName;
        private String deleteRule;
        private String updateRule;
    }

    @Getter
    @Setter
    static class CreateTableCache {
        private String ddl;
        private Set<String> needTable = new HashSet<>();
    }
}

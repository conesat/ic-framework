package cn.icframework.dber;

import cn.icframework.common.interfaces.IEnum;
import cn.icframework.core.common.helper.IndexResult;
import cn.icframework.core.common.helper.TableColumn;
import cn.icframework.core.utils.Assert;
import cn.icframework.mybatis.annotation.ForeignKey;
import cn.icframework.mybatis.annotation.ForeignKeyAction;
import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.Index;
import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.annotation.TableField;
import cn.icframework.mybatis.consts.IdType;
import cn.icframework.mybatis.consts.MysqlType;
import cn.icframework.mybatis.consts.MysqlTypeMap;
import cn.icframework.mybatis.utils.ModelClassUtils;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
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
    private Connection conn;

    private List<String> sqlAfterRunList = new ArrayList<>();

    public final static String SQL_CREATE_TEMPLATE = """
            CREATE TABLE IF NOT EXISTS `#TABLE_NAME` (
            #CONTENT
            #PRIMARY_KEY
            #INDEX
            #FOREIGN_KEY
            )  ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='#COMMENT';
            """;

    /**
     * 收尾工作
     */
    public void close() {
        if (conn == null) {
            return;
        }
        try (Statement statement = conn.createStatement()) {
            // 关闭前将未添加的 sqlAfterRunList 执行
            for (String sql : sqlAfterRunList) {
                log.info(sql);
                statement.execute(sql);
            }
            sqlAfterRunList = null;
        } catch (Exception e) {
            log.error("sql执行失败", e);
        }

        try {
            conn.close();
            conn = null;
        } catch (SQLException e) {
            log.error("关闭连接失败", e);
        }
    }

    /**
     * 执行ddl
     *
     * @param entityType 实体类
     */
    public void runDDL(Class<?> entityType) throws SQLException {
        if (conn == null) {
            SqlSession sqlSession = sqlSessionFactory.openSession();
            conn = sqlSession.getConnection();
        }
        Statement statement = conn.createStatement();
        Table table = entityType.getDeclaredAnnotation(Table.class);
        String tableName = table.value();
        String tableSchema = table.schema();
        String catalog = conn.getCatalog();
        try {
            if (StringUtils.hasLength(tableSchema)) {
                // 切换到指定数据库进行操作
                statement.execute("USE " + tableSchema + ";");
            }
            ResultSet tableExist = statement.executeQuery("SHOW TABLES like '" + tableName + "';");
            if (tableExist.next()) {
                updateTable(entityType, statement, table);
            } else {
                createTable(entityType, statement, table);
            }
        } finally {
            if (StringUtils.hasLength(tableSchema)) {
                // 切换回默认数据库
                statement.execute("USE " + catalog + ";");
            }
            statement.close();
        }
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
                .replaceAll("#TABLE_NAME", table.value())
                .replaceAll("#CONTENT", String.join(",\n", fields))
                .replaceAll("#COMMENT", table.comment())
                .replaceAll("#PRIMARY_KEY", StringUtils.hasLength(key) ? "," + key : "")
                .replaceAll("#FOREIGN_KEY", "")
                .replaceAll("#INDEX", indexSet.isEmpty() ? "" : "," + String.join(",\n", indexSet));
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

    private static Set<String> getForeignKeySet(Class<?> entityType, Table table,
                                                List<ModelClassUtils.FieldAndAnnotation<ForeignKey>> fieldAndAnnotations,
                                                Set<String> foreignKeySet) {
        Set<String> nameSet = new HashSet<>();
        for (ModelClassUtils.FieldAndAnnotation<ForeignKey> fa : fieldAndAnnotations) {
            Assert.isTrue(
                    fa.getField().isAnnotationPresent(TableField.class) || fa.getField().isAnnotationPresent(Id.class),
                    entityType.getName() + ":" + fa.getField().getName() + "非数据库字段，不可添加外建注解");

            Table annotation = fa.getAnnotation().references().getAnnotation(Table.class);
            Assert.isNotNull(annotation, "外键引用的实体类未添加 @Table 注解");

            String fkName = fa.getAnnotation().name();
            if (!StringUtils.hasLength(fkName)) {
                fkName = "FK_" + table.value() + "_" + ModelClassUtils.getTableColumnName(table, fa.getField());
            }
            nameSet.add(fkName);
            String referencesColumn;
            if (StringUtils.hasLength(fa.getAnnotation().referencesColumn())) {
                referencesColumn = fa.getAnnotation().referencesColumn();
            } else {
                referencesColumn = ModelClassUtils.getTableColumnName(table, ModelClassUtils.getIdField(fa.getAnnotation().references()));
            }
            String onDelete = Objects.equals(fa.getAnnotation().onDelete(), ForeignKeyAction.NONE) ? "" : " ON DELETE " + fa.getAnnotation().onDelete();
            String onUpdate = Objects.equals(fa.getAnnotation().onDelete(), ForeignKeyAction.NONE) ? "" : " ON UPDATE " + fa.getAnnotation().onDelete();
            foreignKeySet.add(String.format("CONSTRAINT `%s` FOREIGN KEY (`%s`) REFERENCES `%s` (`%s`)%s%s",
                    fkName,
                    ModelClassUtils.getTableColumnName(table, fa.getField()),
                    annotation.value(),
                    referencesColumn,
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
        for (String key : foreignKeySet) {
            foreignKeySortMap.put(key.toLowerCase().replaceAll(" ", "").trim(), key);
        }

        // 获取已有外键
        ResultSet resultSet = statement.executeQuery("show create table " + table.value() + ";");
        List<String> dropSqls = new ArrayList<>();
        while (resultSet.next()) {
            String ddl = resultSet.getString(2);
            String[] split = ddl.split("\n");
            for (String sql : split) {
                sql = sql.replaceAll(" ", "").trim();
                if (sql.endsWith(",")) {
                    sql = sql.substring(0, sql.length() - 1);
                }
                if (!sql.startsWith("CONSTRAINT")) {
                    continue;
                }
                String lowerCase = sql.toLowerCase();
                if (foreignKeySortMap.containsKey(lowerCase)) {
                    foreignKeySortMap.remove(lowerCase);
                } else {
                    sql = sql.substring("CONSTRAINT `".length() - 1);
                    String fkName = sql.split("`")[0];
                    foreignKeyNameSet.remove(fkName);
                    String dropSql = String.format("ALTER TABLE %s DROP FOREIGN KEY %s;", table.value(), fkName);
                    dropSqls.add(dropSql);
                }
            }
        }
        sqlAfterRunList.addAll(dropSqls);
        for (Map.Entry<String, String> stringStringEntry : foreignKeySortMap.entrySet()) {
            String sql = String.format("ALTER TABLE %s ADD %s;", table.value(), stringStringEntry.getValue());
            sqlAfterRunList.add(sql);
        }
        return foreignKeyNameSet;
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
        ResultSet indexResultSet = statement.executeQuery("SHOW INDEX FROM " + table.value() + ";");
        List<IndexResult> indexResults = new ArrayList<>();
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
        ResultSet resultSet = statement.executeQuery("show columns from " + table.value() + ";");
        Map<String, TableColumn> existField = new HashMap<>();
        while (resultSet.next()) {
            TableColumn tableColumn = new TableColumn();
            tableColumn.setType(resultSet.getString("Type"));
            tableColumn.setField(resultSet.getString("Field"));
            tableColumn.setDefaultValue(resultSet.getString("Default"));
            tableColumn.setNotNull(resultSet.getString("Null").equals("NO"));
            existField.put(tableColumn.getField(), tableColumn);
        }
        Set<String> sqls = getTableAddFields(table, entityType, existField);
        for (String sql : sqls) {
            log.info(sql);
            statement.execute(sql);
        }
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
                if (tableField == null && id == null) {
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
                String sql = getSql(tableField, id, fieldName, sqlType, notNull);
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
            String fieldName,
            String sqlType,
            boolean notNull) {
        String nullAble = notNull ? " NOT NULL " : " NULL ";
        String defaultValue = tableField != null && StringUtils.hasLength(tableField.defaultValue()) ? " DEFAULT " + tableField.defaultValue() + " " : "";
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
    private Set<String> getTableAddFields(Table table, Class<?> entityClass, Map<String, TableColumn> existFields) {
        Set<String> sqls = new HashSet<>();
        String tableName = table.value();
        do {
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                TableField tableField = getTableField(declaredField, entityClass.getTypeName());
                Id id = declaredField.getDeclaredAnnotation(Id.class);
                if (tableField == null && id == null) {
                    continue;
                }
                // 是否限制非空
                boolean notNull = (tableField != null && tableField.notNull()) || id != null;
                String sqlType = getSqlType(declaredField, tableField);
                String fieldName = ModelClassUtils.getColumnName(table, tableField, declaredField.getName());
                String sql = getSql(tableField, id, fieldName, sqlType, notNull);
                if (existFields.containsKey(fieldName)) {
                    if (!existFields.get(fieldName).getType().startsWith(sqlType)
                            || existFields.get(fieldName).isNotNull() != notNull
                            || (tableField == null && existFields.get(fieldName).getDefaultValue() != null)
                            || !equalsDefaultValue(tableField, existFields.get(fieldName))
                    ) {
                        sqls.add("ALTER TABLE " + tableName + " MODIFY COLUMN " + sql);
                    }
                    continue;
                }
                sqls.add("ALTER TABLE " + tableName + " ADD " + sql);
                if (id != null) {
                    sqls.add("ALTER TABLE " + tableName + " ADD CONSTRAINT " + tableName + "_pk PRIMARY KEY(" + fieldName + ")");
                }
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
            if (tableDefaultValue.equals(defaultValue)
                    || (tableDefaultValue.equals("b'0'") && (defaultValue.equals("0") || defaultValue.equals("false")))
                    || (tableDefaultValue.equals("b'1'") && (defaultValue.equals("1") || defaultValue.equals("true")))
            ) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static TableField getTableField(Field declaredField,
                                            String classTypeName) {
        TableField tableField = declaredField.getDeclaredAnnotation(TableField.class);
        if (tableField != null && tableField.isLogicDelete()) {
            Assert.isFalse(tableField.notNull() && !StringUtils.hasLength(tableField.defaultValue()), classTypeName + " 逻辑删除字段notNull时，默认 defaultValue 值必填");

            Assert.isTrue(declaredField.getType().isAssignableFrom(Boolean.class)
                    || declaredField.getType().isAssignableFrom(boolean.class)
                    || declaredField.getType().isAssignableFrom(Integer.class)
                    || declaredField.getType().isAssignableFrom(int.class), classTypeName + " 逻辑删除字段只能是 boolean 或者 int 类型");

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
    static class CreateTableCache {
        private String ddl;
        private Set<String> needTable = new HashSet<>();
    }
}

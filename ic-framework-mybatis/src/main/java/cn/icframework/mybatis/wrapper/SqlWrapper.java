package cn.icframework.mybatis.wrapper;

import cn.icframework.common.consts.IPage;
import cn.icframework.common.lambda.LambdaGetter;
import cn.icframework.common.lambda.LambdaUtils;
import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.consts.IcParamsConsts;
import cn.icframework.mybatis.consts.StatementType;
import cn.icframework.mybatis.consts.CompareEnum;
import cn.icframework.mybatis.query.Condition;
import cn.icframework.mybatis.query.DataSet;
import cn.icframework.mybatis.consts.Funcs;
import cn.icframework.mybatis.consts.JoinType;
import cn.icframework.mybatis.query.QueryField;
import cn.icframework.mybatis.query.QueryTable;
import cn.icframework.mybatis.consts.WhereJoinType;
import cn.icframework.mybatis.utils.ModelClassUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SqlWrapper 用于构建 SQL 语句的工具类。
 * <b>注意：本类非线程安全，不建议多线程共享同一实例。</b>
 */
@NoArgsConstructor
@Slf4j
public class SqlWrapper implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public SqlWrapper(StatementType statementType) {
        this.statementType = statementType;
    }

    /**
     * 参数
     */
    @Getter
    protected final Map<String, Object> params = new HashMap<>();
    protected List<String> sets = new ArrayList<>();
    protected List<String> select = new ArrayList<>();
    protected List<String> tables = new ArrayList<>();
    protected List<String> where = new ArrayList<>();

    protected List<String> columns = new ArrayList<>(); // 插入字段
    protected List<List<String>> valuesList = new ArrayList<>(); // 插入值
    protected Set<String> whereLogicDel = new HashSet<>(); // 逻辑删除
    protected List<String> groupBy = new ArrayList<>();
    protected List<String> having = new ArrayList<>();
    protected List<String> orderBy = new ArrayList<>();
    protected List<String> join = new ArrayList<>();
    protected List<String> innerJoin = new ArrayList<>();
    protected List<String> outerJoin = new ArrayList<>();
    protected List<String> leftOuterJoin = new ArrayList<>();
    protected List<String> rightOuterJoin = new ArrayList<>();
    private JoinType joinType; // 最后一个join的类型
    private String asName;

    @Getter
    @Setter
    protected StatementType statementType;
    private boolean distinct;
    @Setter
    @Getter
    private boolean coverXml; // 是否需要xml转义

    @Getter
    private PageWrapper pageWrapper; // 记录需要执行分页的sql
    private String offset;
    private String limit;
    private LimitingRowsStrategy limitingRowsStrategy = LimitingRowsStrategy.NOP;
    private SqlWrapper inserSqlWrapper; // 插入表嵌套添加

    /**
     * 将查询转为子查询
     *
     * @param as 别名
     * @return SqlWrapper
     */
    public SqlWrapper AS(String as) {
        this.asName = as;
        return this;
    }

    /**
     * 将查询转为子查询
     *
     * @param lambdaGetter 别名
     * @return SqlWrapper
     */
    public <T> SqlWrapper AS(LambdaGetter<T> lambdaGetter) {
        Field field = LambdaUtils.getField(lambdaGetter);
        this.asName = field.getName();
        return this;
    }

    /**
     * 将查询转为子查询
     *
     * @param queryTable 表
     * @return SqlWrapper
     */
    @SuppressWarnings("unchecked")
    public <T extends QueryTable<?>> T AS(Class<T> queryTable) {
        try {
            Method table = queryTable.getMethod("table");
            T t = (T) table.invoke(null);
            t.setSqlWrapper(this);
            t.setAsName(asName);
            return t;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 查询条件
     *
     * @param wheres 表条件参数
     * @return SqlWrapper
     */
    public SqlWrapper WHERE(String... wheres) {
        where.addAll(List.of(wheres));
        return this;
    }


    /**
     * 查询条件
     *
     * @param queryTables 表条件参数
     * @return SqlWrapper
     */
    public SqlWrapper WHERE_AO(WhereJoinType whereJoinType, QueryTable<?>... queryTables) {
        if (queryTables == null || queryTables.length == 0 || queryTables[0] == null) {
            return this;
        }
        List<String> wheres = new ArrayList<>();
        for (QueryTable<?> queryTable : queryTables) {
            if (queryTable.getFunc() != null) {
                // 这是一个嵌套函数
                String whereFunc = handleWhereFunc(queryTable);
                if (StringUtils.hasLength(whereFunc)) {
                    wheres.add(whereFunc);
                }
                continue;
            }
            if (!queryTable.getWheres().isEmpty()) {
                // 普通一级条件
                for (Condition condition : queryTable.getWheres()) {
                    wheres.add(getWhereParam(queryTable, condition));
                }
                queryTable.getWheres().clear();
            }
            if (queryTable.getChildrenQueryTables() != null) {
                if (!wheres.isEmpty()) {
                    wheres.add(queryTable.getWhereJoinType().key);
                }
                wheres.add(handelChileQuery(queryTable, false));
            }
        }
        if (!wheres.isEmpty()) {
            if (!where.isEmpty()) {
                where.add(whereJoinType.key);
            }
            SafeAppendable builder = new SafeAppendable(new StringBuilder());
            sqlClause(builder, "", wheres, "", "", " AND ");
            if (wheres.size() > 1) {
                where.add("(" + builder + ")");
            } else {
                where.add(builder.toString());
            }
        }
        return this;
    }

    public SqlWrapper WHERE(QueryTable<?>... queryTables) {
        return WHERE_AO(WhereJoinType.AND, queryTables);
    }

    public SqlWrapper AND(QueryTable<?>... queryTables) {
        return WHERE_AO(WhereJoinType.AND, queryTables);
    }

    public SqlWrapper OR(QueryTable<?>... queryTables) {
        return WHERE_AO(WhereJoinType.OR, queryTables);
    }

    private String handleWhereFunc(QueryTable<?> queryTable) {
        if (queryTable.getFunc().equals(Funcs.EXISTS) || queryTable.getFunc().equals(Funcs.NOT_EXISTS)) {
            String sql = queryTable.getSqlWrapper().sql();
            Map<String, Object> funParams = queryTable.getSqlWrapper().params;
            // 将 keySet 转换为 List 并排序
            List<String> sortedKeys = funParams.keySet().stream()
                    .sorted()
                    .toList();
            for (String key : sortedKeys) {
                String newKey = putParamSync(funParams.get(key));
                sql = sql.replace(IcParamsConsts.GET_PARAM_S(key), newKey);
            }
            return String.format("%s (%s)", queryTable.getFunc(), sql);
        }
        return null;
    }

    /**
     * 获取表属性中的排序
     *
     * @param queryTables 表条件参数
     * @return SqlWrapper
     */
    public SqlWrapper ORDER_BY(QueryTable<?>... queryTables) {
        for (QueryTable<?> queryTable : queryTables) {
            orderBy.addAll(queryTable.getOrders());
            queryTable.getOrders().clear();
        }
        return this;
    }

    /**
     * 正序排序
     *
     * @param queryFields 表条件参数
     * @return SqlWrapper
     */
    public SqlWrapper ORDER_BY_ASC(QueryField<?>... queryFields) {
        for (QueryField<?> queryField : queryFields) {
            if (queryField.getFunc() != null) {
                orderBy.add(queryField.getFunc());
            } else {
                orderBy.add(queryField.getAsNameOrName());
            }
        }
        return this;
    }

    /**
     * 倒序排序
     *
     * @param queryFields 表条件参数
     * @return SqlWrapper
     */
    public SqlWrapper ORDER_BY_DESC(QueryField<?>... queryFields) {
        for (QueryField<?> queryField : queryFields) {
            if (queryField.getFunc() != null) {
                orderBy.add(String.format("%s DESC", queryField.getFunc()));
            } else {
                orderBy.add(String.format("%s DESC", queryField.getAsNameOrName()));
            }
        }
        return this;
    }

    /**
     * 排序条件
     *
     * @param orderBys 表条件参数
     * @return SqlWrapper
     */
    public SqlWrapper ORDER_BY(String... orderBys) {
        orderBy.addAll(List.of(orderBys));
        return this;
    }

    /**
     * 设置limit参数，允许接收字符串
     *
     * @param offset   偏移
     * @param limitNum 获取数量
     * @return SqlWrapper
     */
    public SqlWrapper LIMIT(int offset, int limitNum) {
        limit = String.format("%d,%d", offset, limitNum);
        limitingRowsStrategy = LimitingRowsStrategy.OFFSET_LIMIT;
        return this;
    }

    /**
     * 设置limit参数，允许接收字符串
     *
     * @param limitNum limit获取num条
     * @return SqlWrapper
     */
    public SqlWrapper LIMIT(int limitNum) {
        limit = String.valueOf(limitNum);
        limitingRowsStrategy = LimitingRowsStrategy.OFFSET_LIMIT;
        return this;
    }


    /**
     * 开启分页，并返回到page中
     * 注意该接口只允许一个分页，如果重复赋值会抛出异常
     *
     * @param page 分页内容，分页结果会封装到page中
     * @return SqlWrapper
     */
    public <P extends IPage> SqlWrapper PAGE(P page) {
        page(new PageWrapper(page, this));
        return this;
    }

    public SqlWrapper OFFSET(String variable) {
        offset = variable;
        limitingRowsStrategy = LimitingRowsStrategy.OFFSET_LIMIT;
        return this;
    }

    public SqlWrapper OFFSET(long value) {
        return OFFSET(String.valueOf(value));
    }


    /**
     * 分组条件
     *
     * @param objects 条件参数
     * @return SqlWrapper
     */
    public SqlWrapper GROUP_BY(String... objects) {
        for (String str : objects) {
            if (isNotSafeSqlIdentifier(str)) {
                throw new IllegalArgumentException("不安全的SQL标识符: " + str);
            }
            groupBy.add(String.format("`%s`", str));
        }
        return this;
    }

    /**
     * 分组条件
     *
     * @param queryFields 条件参数
     * @return SqlWrapper
     */
    public SqlWrapper GROUP_BY(QueryField<?>... queryFields) {
        for (QueryField<?> queryField : queryFields) {
            groupBy.add(queryField.getNameWithTable());
        }
        return this;
    }


    /**
     * 全连接
     *
     * @param joinTable 需要链接的表
     * @return SqlQuery
     */
    public JoinWrapper JOIN(QueryTable<?> joinTable) {
        this.join(joinTable);
        return new JoinWrapper(this);
    }

    /**
     * 左连接
     *
     * @param joinTable 需要链接的表
     * @return SqlQuery
     */
    public JoinWrapper LEFT_JOIN(QueryTable<?> joinTable) {
        this.leftJoin(joinTable);
        return new JoinWrapper(this);
    }

    /**
     * 右连接
     *
     * @param joinTable 需要链接的表
     * @return SqlQuery
     */
    public JoinWrapper RIGHT_JOIN(QueryTable<?> joinTable) {
        this.rightJoin(joinTable);
        return new JoinWrapper(this);
    }

    /**
     * 内连接
     *
     * @param joinTable 需要链接的表
     * @return SqlQuery
     */
    public JoinWrapper INNER_JOIN(QueryTable<?> joinTable) {
        this.innerJoin(joinTable);
        return new JoinWrapper(this);
    }

    /**
     * 外连接
     *
     * @param joinTable 需要链接的表
     * @return SqlQuery
     */
    public JoinWrapper OUTER_JOIN(QueryTable<?> joinTable) {
        this.outerJoin(joinTable);
        return new JoinWrapper(this);
    }


    /**
     * 去重查询原始内容，输入的参数都将保留原格式拼接到select
     * 如
     * SELECT(1) =>  select 1
     * SELECT("1") => select `1`
     * PS: 该方法返回对象副本，请用新对象接收
     *
     * @param normalSelects 查询列表
     */
    public SqlWrapper SELECT_DISTINCT(Object... normalSelects) {
        SqlWrapper sqlWrapper = cloneSqlWrapper();
        if (select.size() == 1 && select.getFirst().equals("*")) {
            sqlWrapper.select = new ArrayList<>();
        }
        sqlWrapper.selectDistinct(normalSelects);
        return sqlWrapper;
    }

    /**
     * 拷贝构造函数，深拷贝所有字段，避免引用共享。
     */
    public SqlWrapper(SqlWrapper other) {
        this.statementType = other.statementType;
        this.coverXml = other.coverXml;
        this.distinct = other.distinct;
        this.asName = other.asName;
        this.joinType = other.joinType;
        this.offset = other.offset;
        this.limit = other.limit;
        this.limitingRowsStrategy = other.limitingRowsStrategy;
        this.inserSqlWrapper = other.inserSqlWrapper == null ? null : new SqlWrapper(other.inserSqlWrapper);

        // 深拷贝集合和Map
        this.params.putAll(other.params);
        this.sets = new ArrayList<>(other.sets);
        this.select = new ArrayList<>(other.select);
        this.tables = new ArrayList<>(other.tables);
        this.where = new ArrayList<>(other.where);
        this.columns = new ArrayList<>(other.columns);
        this.valuesList = new ArrayList<>();
        for (List<String> list : other.valuesList) {
            this.valuesList.add(new ArrayList<>(list));
        }
        this.whereLogicDel = new HashSet<>(other.whereLogicDel);
        this.groupBy = new ArrayList<>(other.groupBy);
        this.having = new ArrayList<>(other.having);
        this.orderBy = new ArrayList<>(other.orderBy);
        this.join = new ArrayList<>(other.join);
        this.innerJoin = new ArrayList<>(other.innerJoin);
        this.outerJoin = new ArrayList<>(other.outerJoin);
        this.leftOuterJoin = new ArrayList<>(other.leftOuterJoin);
        this.rightOuterJoin = new ArrayList<>(other.rightOuterJoin);

        // pageWrapper 只拷贝引用，pageWrapper.page 需要特殊回填
        if (other.pageWrapper != null) {
            this.pageWrapper = new PageWrapper(other.pageWrapper.page, this);
        }
    }

    /**
     * 克隆一个sqlWrapper对象，一般用于需要与原来条件进行分开处理的情况
     */
    private SqlWrapper cloneSqlWrapper() {
        return new SqlWrapper(this);
    }

    /**
     * 需要查询的字段
     * PS: 该方法返回对象副本，请用新对象接收
     *
     * @param queryTable 字段列表
     */
    public SqlWrapper SELECT(QueryTable<?> queryTable) {
        SqlWrapper sqlWrapper = cloneSqlWrapper();
        if (select.size() == 1 && select.getFirst().equals("*")) {
            sqlWrapper.select = new ArrayList<>();
        }
        sqlWrapper.select(queryTable);
        return sqlWrapper;
    }

    /**
     * 混合查询，查询原始内容，输入的参数都将保留原格式拼接到select
     * 如
     * SELECT(1) =>  select 1
     * SELECT("1") => select `1`
     * PS: 该方法返回对象副本，请用新对象接收
     *
     * @param normalSelects 查询列表
     */
    public SqlWrapper SELECT(Object... normalSelects) {
        SqlWrapper sqlWrapper = cloneSqlWrapper();
        if (select.size() == 1 && select.getFirst().equals("*")) {
            sqlWrapper.select = new ArrayList<>();
        }
        sqlWrapper.select(normalSelects);
        return sqlWrapper;
    }

    /**
     * 获取sql
     *
     * @return sql
     */
    public String sql() {
        SafeAppendable builder = new SafeAppendable(new StringBuilder());
        Assert.notNull(statementType, "sql未指定增删改查");
        if (pageWrapper != null && pageWrapper.getSqlWrapper() == this) {
            LIMIT(pageWrapper.getPage().getPageSize() * (pageWrapper.getPage().getPageIndex() - 1), pageWrapper.getPage().getPageSize());
        }
        switch (statementType) {
            case SELECT -> {
                return selectSQL(builder);
            }
            case UPDATE -> {
                return updateSQL(builder);
            }
            case DELETE -> {
                return deleteSQL(builder);
            }
            case INSERT -> {
                return insertSQL(builder);
            }
        }
        return null;
    }

    /**
     * 获取sql
     *
     * @return sql
     */
    public String countSql() {
        SafeAppendable builder = new SafeAppendable(new StringBuilder());
        statementType = StatementType.SELECT;
        return selectSQL(builder, true);
    }

    //++++++++++++受保护的方法，不对外开放+++++++++++++++++++++++++++++

    /**
     * 需要查询的字段
     *
     * @param queryTable 字段列表
     */
    protected final void select(QueryTable<?> queryTable) {
        Field[] fields = queryTable.getClass().getFields();
        for (Field field : fields) {
            try {
                QueryField<?> queryField = (QueryField<?>) field.get(queryTable);
                if (queryField.getTableColumn().equals("*")) {
                    // 跳过* 否则会死循环
                    continue;
                }
                select(queryField);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * 去重查询原始内容，输入的参数都将保留原格式拼接到select
     * 如
     * SELECT(1) =>  select 1
     * SELECT("1") => select `1`
     *
     * @param normalSelects 查询列表
     */
    protected final void selectDistinct(Object... normalSelects) {
        this.distinct = true;
        select(normalSelects);
    }

    /**
     * 不指定查询字段，字段全部从FROM的表获取
     */
    protected final void select() {
        statementType = StatementType.SELECT;
    }

    /**
     * 出重
     */
    protected final void selectDistinct() {
        distinct = true;
        this.select();
    }


    /**
     * 需要查询的字段
     *
     * @param queryField 字段列表
     */
    protected final void select(QueryField<?> queryField) {
        String col;
        if (StringUtils.hasLength(queryField.getFunc())) {
            col = StringUtils.hasLength(queryField.getAsName())
                    ? String.format("%s AS `%s`", queryField.getFunc(), queryField.getAsName())
                    : String.format("%s AS `%s`", queryField.getFunc(), queryField.getFunc().replaceAll("\\.", "_"));
        } else {
            if (queryField.getTableColumn().equals("*")) {
                select(queryField.getTable());
                return;
            }

            if (StringUtils.hasLength(queryField.getAsName())) {
                // 字段被授予了别名
                col = String.format("%s AS `%s`", queryField.getNameWithTable(), queryField.getAsName());
                if (select.contains(col)) {
                    // 名称重复，需要添加表名
                    col = String.format("%s AS `%s`", queryField.getNameWithTable(), queryField.getAsNameOrNameWithTable());
                }
            } else if (StringUtils.hasLength(queryField.getTable().getAsName())) {
                // 表被授予了别名
                col = String.format("%s AS `%s`", queryField.getNameWithTable(), queryField.getAsNameOrNameWithTable());
            } else {
                if (select.contains(queryField.getNameWithTable())) {
                    // 名称重复，需要添加表名
                    col = String.format("%s AS `%s`", queryField.getNameWithTable(), queryField.getAsNameOrNameWithTable());
                } else {
                    col = String.format("%s", queryField.getNameWithTable());
                }
            }
        }
        select.add(col);
    }

    /**
     * 需要查询的字段
     *
     * @param entity 实体类
     */
    protected final void selectFromEntity(Class<?> entity) {
        List<Field> declaredFields = ModelClassUtils.getDeclaredFields(entity);
        Table table = ModelClassUtils.getDeclaredAnnotation(entity, Table.class);
        Assert.notNull(table, entity.getName() + "表注解不能为空");
        for (Field declaredField : declaredFields) {
            String fieldName = ModelClassUtils.getTableColumnName(table, declaredField);
            if (fieldName == null) {
                continue;
            }
            select.add(table.value() + "." + fieldName);
        }
    }

    /**
     * 需要查询的字段
     *
     * @param sqlWrapper 子查询
     */
    protected final void select(SqlWrapper sqlWrapper) {
        Assert.hasLength(sqlWrapper.asName, "子查询必须指定 as ");
        select.add(getSubSql(sqlWrapper));
    }

    /**
     * 混合查询，查询原始内容，输入的参数都将保留原格式拼接到select
     * 如
     * SELECT(1) =>  select 1
     * SELECT("1") => select `1`
     *
     * @param normalSelects 查询列表
     */
    protected final void select(Object... normalSelects) {
        for (Object object : normalSelects) {
            switch (object) {
                case String str -> {
                    if (isNotSafeSqlIdentifier(str)) {
                        throw new IllegalArgumentException("不安全的SQL标识符: " + str);
                    }
                    select.add(String.format("`%s`", str));
                }
                case QueryTable<?> queryTable -> select(queryTable);
                case QueryField<?> queryField -> select(queryField);
                case SqlWrapper sqlWrapper -> select(sqlWrapper);
                case Class<?> h -> {
                    if (h.isAnnotationPresent(Table.class)) {
                        selectFromEntity(h);
                    } else {
                        select.add(h.toString());
                    }
                }
                default -> select.add(object.toString());
            }
        }
        statementType = StatementType.SELECT;
    }

    /**
     * 添加表
     *
     * @param queryTables 表
     */
    protected void from(QueryTable<?>... queryTables) {
        boolean handleSelect = statementType == StatementType.SELECT && select.isEmpty();
        if (handleSelect) {
            select.add("*");
        }
        for (QueryTable<?> queryTable : queryTables) {
            if (queryTable.getSqlWrapper() != null) {
                queryTable.getSqlWrapper().AS(queryTable.getAsNameOrName());
                from(queryTable.getSqlWrapper());
            } else {
                tables.add(queryTable.getAsNameWrap());
                logicDelNe(queryTable);
            }
        }
        WHERE(queryTables);
    }

    /**
     * 添加子查询
     *
     * @param sqlWrapper 子查询
     */
    protected void from(SqlWrapper sqlWrapper) {
        Assert.hasLength(sqlWrapper.asName, "子查询必须指定as名称");
        boolean handleSelect = statementType == StatementType.SELECT && select.isEmpty();
        if (handleSelect) {
            select.add("*");
        }
        tables.add(getSubSql(sqlWrapper));
        if (sqlWrapper.pageWrapper != null) {
            page(sqlWrapper.pageWrapper);
        }
    }

    /**
     * 更新表
     *
     * @param queryTable 需要操作的表
     */
    protected final void update(QueryTable<?> queryTable) {
        tables.add(queryTable.getAsNameWrap());
        WHERE(queryTable);
        set(queryTable);
        statementType = StatementType.UPDATE;
    }


    /**
     * 根据实体更新表
     *
     * @param entity 需要操作的实体
     */
    protected final void update(Class<?> entity) {
        String tableName = ModelClassUtils.getTableName(entity);
        tables.add(tableName);
        statementType = StatementType.UPDATE;
    }

    /**
     * 更新表
     *
     * @param tableName 表名
     */
    protected final void update(String tableName) {
        tables.add(tableName);
        statementType = StatementType.UPDATE;
    }

    /**
     * 设置表字段
     *
     * @param field 字段
     * @param val   值
     */
    protected final void set(String field, Object val) {
        if (val == null) {
            sets.add(String.format("`%s` = null", field));
            return;
        }
        String paramKey = putParamSync(val);
        sets.add(String.format("`%s` = %s", field, paramKey));
    }

    /**
     * 设置表字段
     *
     * @param field 字段
     * @param val   值
     */
    protected final void setSourceParam(String field, Object val) {
        sets.add(String.format("`%s` = %s", field, val));
    }

    /**
     * 设置表字段
     *
     * @param field 字段
     */
    protected final void setLogicDel(String field) {
        sets.add(String.format("`%s` = %s", field, IcParamsConsts.PARAMETER_LOGIC_DELETE_GET));
    }

    /**
     * 设置表字段
     *
     * @param queryTable 字段集合
     */
    protected final void set(QueryTable<?> queryTable) {
        for (DataSet set : queryTable.getSets()) {
            String paramKey = putParamSync(set.getValue());
            sets.add(String.format("`%s` = %s", set.getColumn(), paramKey));
        }
        queryTable.getSets().clear();
    }


    /**
     * 全连接
     *
     * @param joinTable 需要链接的表
     */
    protected final void join(QueryTable<?> joinTable) {
        wrapperJoin(joinTable, join);
        joinType = JoinType.JOIN;
    }

    /**
     * 左连接
     *
     * @param joinTable 需要链接的表
     */
    protected final void leftJoin(QueryTable<?> joinTable) {
        wrapperJoin(joinTable, leftOuterJoin);
        joinType = JoinType.LEFT_OUTER_JOIN;
    }

    /**
     * 右连接
     *
     * @param joinTable 需要链接的表
     */
    protected final void rightJoin(QueryTable<?> joinTable) {
        wrapperJoin(joinTable, rightOuterJoin);
        joinType = JoinType.RIGHT_OUTER_JOIN;
    }

    /**
     * 内连接
     *
     * @param joinTable 需要链接的表
     */
    protected final void innerJoin(QueryTable<?> joinTable) {
        wrapperJoin(joinTable, innerJoin);
        joinType = JoinType.INNER_JOIN;
    }

    /**
     * 外连接
     *
     * @param joinTable 需要链接的表
     */
    protected final void outerJoin(QueryTable<?> joinTable) {
        wrapperJoin(joinTable, outerJoin);
        joinType = JoinType.OUTER_JOIN;
    }

    /**
     * 处理连接
     *
     * @param joinTable 需要链接的表
     * @param join      连接
     */
    private void wrapperJoin(QueryTable<?> joinTable, List<String> join) {
        if (joinTable.getSqlWrapper() != null) {
            joinTable.getSqlWrapper().AS(joinTable.getAsNameOrName());
            join.add(getSubSql(joinTable.getSqlWrapper(), false));
            if (joinTable.getSqlWrapper().pageWrapper != null) {
                page(joinTable.getSqlWrapper().pageWrapper);
            }
        } else {
            join.add(joinTable.getAsNameWrap());
        }
    }

    /**
     * 添加逻辑删除标记 未删除的数据
     *
     * @param queryTable 表
     */
    protected final void logicDelNe(QueryTable<?> queryTable) {
        logicDelNe(queryTable.getTableClass(), queryTable.getAsNameOrName());
    }

    protected final void logicDelNe(Class<?> entity, String tableName) {
        Table table = entity.getAnnotation(Table.class);
        Field logicDeleteField = ModelClassUtils.getLogicDelete(entity);
        if (logicDeleteField != null) {
            whereLogicDel.add(String.format("%s.%s%s%s", tableName, ModelClassUtils.getTableColumnName(table, logicDeleteField), CompareEnum.NE.getKey(coverXml), IcParamsConsts.PARAMETER_LOGIC_DELETE_GET));
        }
    }

    /**
     * join条件
     *
     * @param objects join条件
     */
    protected final void joinOn(Object... objects) {
        SafeAppendable builder = new SafeAppendable(new StringBuilder());
        List<String> joinOns = new ArrayList<>();

        for (Object object : objects) {
            if (object instanceof QueryTable<?> queryTable) {
                for (Condition condition : queryTable.getWheres()) {
                    joinOns.add(getWhereParam(queryTable, condition));
                }
                Field logicDeleteField = ModelClassUtils.getLogicDelete(queryTable.getTableClass());
                Table table = queryTable.getTableClass().getAnnotation(Table.class);
                if (logicDeleteField != null) {
                    joinOns.add(String.format("%s.%s%s%s", queryTable.getAsNameOrName(), ModelClassUtils.getTableColumnName(table, logicDeleteField), CompareEnum.NE.getKey(coverXml), IcParamsConsts.PARAMETER_LOGIC_DELETE_GET));
                }
                queryTable.getWheres().clear();
            } else if (object instanceof String string) {
                joinOns.add(string);
            }
        }

        sqlClause(builder, "", joinOns, "(", ")", " AND ");
        switch (joinType) {
            case JOIN -> {
                String join = this.join.getLast();
                this.join.set(this.join.size() - 1, join + " ON " + builder);
            }
            case LEFT_OUTER_JOIN -> {
                String leftOuterJoin = this.leftOuterJoin.getLast();
                this.leftOuterJoin.set(this.leftOuterJoin.size() - 1, leftOuterJoin + " ON " + builder);
            }
            case RIGHT_OUTER_JOIN -> {
                String rightOuterJoin = this.rightOuterJoin.getLast();
                this.rightOuterJoin.set(this.rightOuterJoin.size() - 1, rightOuterJoin + " ON " + builder);
            }
            case INNER_JOIN -> {
                String innerJoin = this.innerJoin.getLast();
                this.innerJoin.set(this.innerJoin.size() - 1, innerJoin + " ON " + builder);
            }
            case OUTER_JOIN -> {
                String outerJoin = this.outerJoin.getLast();
                this.outerJoin.set(this.outerJoin.size() - 1, outerJoin + " ON " + builder);
            }
        }
    }


    /**
     * 插入数据 来源
     */
    protected final void insertSelect(SqlWrapper sqlWrapper) {
        inserSqlWrapper = sqlWrapper;
    }

    // 针对实体的操作 start


    /**
     * 添加表,从实体
     *
     * @param entities 实体
     */
    protected void fromEntity(Class<?>... entities) {
        SqlEntityWrapperHelper.fromEntity(this, entities);
    }
    // 针对实体的操作 end


    protected void copyFromAttributes(SqlWrapper sqlWrapper) {
        params.clear();
        params.putAll(sqlWrapper.params);
        sets = sqlWrapper.sets;
        select = sqlWrapper.select;
        tables = sqlWrapper.tables;
        where = sqlWrapper.where;
        columns = sqlWrapper.columns; // 插入字段
        valuesList = sqlWrapper.valuesList; // 插入值
        whereLogicDel = sqlWrapper.whereLogicDel; // 逻辑删除
        groupBy = sqlWrapper.groupBy;
        having = sqlWrapper.having;
        orderBy = sqlWrapper.orderBy;
        join = sqlWrapper.join;
        innerJoin = sqlWrapper.innerJoin;
        outerJoin = sqlWrapper.outerJoin;
        leftOuterJoin = sqlWrapper.leftOuterJoin;
        rightOuterJoin = sqlWrapper.rightOuterJoin;
        joinType = sqlWrapper.joinType;
        statementType = sqlWrapper.statementType;
        distinct = sqlWrapper.distinct;
        coverXml = sqlWrapper.coverXml;
        offset = sqlWrapper.offset;
        limit = sqlWrapper.limit;
        limitingRowsStrategy = sqlWrapper.limitingRowsStrategy;
        pageWrapper = sqlWrapper.pageWrapper;
    }

    //++++++++++++++++内部方法++++++++++++++++++++++++++++++++++

    /**
     * 生产子查询sql，并转移参数到外出wrapper
     *
     * @param sqlWrapper
     * @return
     */
    private String getSubSql(SqlWrapper sqlWrapper) {
        return getSubSql(sqlWrapper, true);
    }

    private String getSubSql(SqlWrapper sqlWrapper, boolean withAs) {
        String sql = getAndMixSql(sqlWrapper);
        return String.format("(%s) %s`%s`", sql, withAs ? "AS " : "", sqlWrapper.asName);
    }

    private String getAndMixSql(SqlWrapper sqlWrapper) {
        String sql = sqlWrapper.sql();
        Map<String, Object> funParams = sqlWrapper.params;
        // 先替换原有参数，防止重复替换
        for (String key : funParams.keySet()) {
            sql = sql.replace(IcParamsConsts.GET_PARAM_S(key), IcParamsConsts.GET_PARAM_S("new_key_" + key));
        }
        for (String key : funParams.keySet()) {
            String newKey = putParamSync(funParams.get(key));
            sql = sql.replace(IcParamsConsts.GET_PARAM_S("new_key_" + key), newKey);
        }
        return sql;
    }

    /**
     * 设置分页回填
     *
     * @param pageWrapper
     */
    private void page(PageWrapper pageWrapper) {
        Assert.isNull(this.pageWrapper, "只允许有一个分页对象，请勿重复赋值");
        this.pageWrapper = pageWrapper;
    }

    /**
     * 递归处理嵌套的子查询条件，将子查询条件拼接为SQL片段。
     *
     * @param queryTable 表，包含子查询条件的 QueryTable 对象
     * @param child      是否为递归子节点（true 表示递归调用）
     * @return 拼接后的 SQL 条件字符串
     */
    private String handelChileQuery(QueryTable<?> queryTable, boolean child) {
        List<String> resList = new ArrayList<>();
        // 嵌套条件
        for (QueryTable<?> childrenQueryTable : queryTable.getChildrenQueryTables()) {

            if (!childrenQueryTable.getWheres().isEmpty()) {
                List<String> childWheres = new ArrayList<>();
                // 普通一级条件
                for (Condition condition : childrenQueryTable.getWheres()) {
                    childWheres.add(getWhereParam(childrenQueryTable, condition));
                }
                SafeAppendable builder = new SafeAppendable(new StringBuilder());
                sqlClause(builder, "", childWheres, "", "", " AND ");
                resList.add(builder.toString());
            }

            if (childrenQueryTable.getChildrenQueryTables() != null) {
                resList.add(childrenQueryTable.getWhereJoinType().key);
                // 遍历下一个条件
                resList.add(handelChileQuery(childrenQueryTable, true));
            }
        }
        // 清空子查询
        queryTable.setChildrenQueryTables(null);
        SafeAppendable builder = new SafeAppendable(new StringBuilder());
        if (!child) {
            sqlClause(builder, "", resList, "(", ")", " AND ");
        } else {
            sqlClause(builder, "", resList, "", "", " AND ");
        }
        return builder.toString();
    }

    /**
     * 将当前 SqlWrapper 的参数合并到传入的 params Map 中。
     * 该方法不对外提供 get，防止外部修改内部参数。
     *
     * @param params 参数 Map，将当前 SqlWrapper 的参数合并到该 Map 中
     */
    public void mixParams(Map<String, Object> params) {
        params.put(IcParamsConsts.PARAMETER_S, this.params);
    }

    /**
     * 设置参数 次方法需要同步执行
     *
     * @param val 值
     * @return 参数key
     */
    protected String putParamSync(Object val) {
        synchronized (params) {
            String paramKey = IcParamsConsts.PARAM_KEY(String.valueOf(params.size()));
            params.put(paramKey, val);
            return IcParamsConsts.GET_PARAM_S(paramKey);
        }
    }


    /**
     * 添加where条件
     *
     * @param queryTable 表
     * @param condition  条件
     */
    private void where(QueryTable<?> queryTable, Condition condition) {
        where.add(getWhereParam(queryTable, condition));
    }

    /**
     * 获取where条件
     *
     * @param queryTable 表
     * @param condition  条件
     */
    private String getWhereParam(QueryTable<?> queryTable, Condition condition) {
        if (condition.getType() != null) {
            return condition.getType().key;
        }
        String column = queryTable.getAsNameOrName() + "." + condition.getTableFieldDef().getTableColumn();
        if (condition.getOtherTableFieldDef() != null) {
            String column2 = condition.getOtherTableFieldDef().getTable().getAsNameOrName() + "." + condition.getOtherTableFieldDef().getTableColumn();
            return column + condition.getCompare().getKey(coverXml) + column2;
        } else {
            if (condition.getCompare() == CompareEnum.IS_NULL || condition.getCompare() == CompareEnum.IS_NOT_NULL) {
                String key = condition.getCompare().getKey(coverXml);
                return column + key.substring(0, key.length() - 1);
            }
            if (condition.getValue() == null && condition.getValues() == null) {
                if (condition.getCompare() == CompareEnum.NE) {
                    String key = CompareEnum.IS_NOT_NULL.getKey(coverXml);
                    return column + key.substring(0, key.length() - 1);
                }
                if (condition.getCompare() == CompareEnum.EQ) {
                    String key = CompareEnum.IS_NULL.getKey(coverXml);
                    return column + key.substring(0, key.length() - 1);
                }
            }
            if (condition.getValues() == null && condition.getValue() == null) {
                // 条件是一个嵌套sql
                if (condition.getSqlWrapper() != null) {
                    return column + condition.getCompare().getKey(coverXml) + "(" + getAndMixSql(condition.getSqlWrapper()) + ")";
                } else if (condition.getSelectWrapper() != null) {
                    return column + condition.getCompare().getKey(coverXml) + "(" + condition.getSelectWrapper().sql() + ")";
                }
            }
            String paramKey = addParam(condition);
            return column + condition.getCompare().getKey(coverXml) + paramKey;
        }
    }

    /**
     * 添加参数 并返回索引
     *
     * @param condition 参数
     */
    private String addParam(Condition condition) {
        Object[] vals = condition.getValues() == null ? new Object[]{condition.getValue()} : condition.getValues().toArray();
        switch (condition.getCompare()) {
            case EQ, NE, GT, GE, LT, LE -> {
                return putParamSync(vals[0]);
            }
            case LIKE -> {
                return putParamSync("%" + vals[0] + "%");
            }
            case LEFT_LIKE -> {
                return putParamSync("%" + vals[0]);
            }
            case RIGHT_LIKE -> {
                return putParamSync(vals[0] + "%");
            }
            case IS_NULL, IS_NOT_NULL -> {
                return "";
            }
            case IN, NOT_IN, BETWEEN, NOT_BETWEEN -> {
                List<String> keys = new ArrayList<>();
                for (Object val : vals) {
                    keys.add(putParamSync(val));
                }
                return String.format("(%s)", String.join(",", keys));
            }
        }
        throw new RuntimeException("不支持的sql符号：" + condition.getCompare());
    }

    private String updateSQL(SafeAppendable builder) {
        sqlClause(builder, "UPDATE", tables, "", "", "");
        joins(builder);
        sqlClause(builder, "SET", sets, "", "", ", ");
        sqlClause(builder, "WHERE", where, "(", ")", " AND ");
        limitingRowsStrategy.appendClause(builder, null, limit);
        return builder.toString();
    }

    private String selectSQL(SafeAppendable builder) {
        return selectSQL(builder, false);
    }

    private String selectSQL(SafeAppendable builder, boolean isCount) {
        if (select.isEmpty()) {
            select.add("*");
        }
        if (distinct) {
            sqlClause(builder, "SELECT DISTINCT", select.stream().toList(), "", "", ", ");
        } else {
            sqlClause(builder, "SELECT", select.stream().toList(), "", "", ", ");
        }
        sqlClause(builder, "FROM", tables, "", "", ", ");
        joins(builder);
        if (whereLogicDel.isEmpty()) {
            sqlClause(builder, "WHERE", where, "(", ")", " AND ");
        } else {
            List<String> list = new ArrayList<>(whereLogicDel);
            if (!where.isEmpty()) {
                SafeAppendable whereBuilder = new SafeAppendable(new StringBuilder());
                sqlClause(whereBuilder, "", where, "(", ")", " AND ");
                if (where.size() > 1) {
                    list.addFirst(String.format("(%s)", whereBuilder));
                } else {
                    list.add(whereBuilder.toString());
                }
            }
            sqlClause(builder, "WHERE", list, "(", ")", " AND ");
        }
        sqlClause(builder, "GROUP BY", groupBy, "", "", ", ");
        sqlClause(builder, "HAVING", having, "(", ")", " AND ");
        if (!isCount || !groupBy.isEmpty()) {
            // 分页不需要order by 但是如果有group by 则需要order by
            sqlClause(builder, "ORDER BY", orderBy, "", "", ", ");
        }
        if (!isCount) {
            limitingRowsStrategy.appendClause(builder, offset, limit);
        }
        return builder.toString();
    }


    private String deleteSQL(SafeAppendable builder) {
        sqlClause(builder, "DELETE FROM", tables, "", "", "");
        sqlClause(builder, "WHERE", where, "(", ")", " AND ");
        limitingRowsStrategy.appendClause(builder, null, limit);
        return builder.toString();
    }

    private String insertSQL(SafeAppendable builder) {
        sqlClause(builder, "INSERT INTO", tables, "", "", "");
        sqlClause(builder, "", columns, "(", ")", ", ");
        for (int i = 0; i < valuesList.size(); i++) {
            sqlClause(builder, i > 0 ? "," : "VALUES", valuesList.get(i), "(", ")", ", ");
        }
        if (inserSqlWrapper != null) {
            if (inserSqlWrapper.select.size() != columns.size()) {
                throw new RuntimeException("insert into 字段(" + String.join(",", columns) + ") 与 select字段(" + String.join(",", inserSqlWrapper.select) + ") 不匹配");
            }
            // 处理 insert select 逻辑
            String sql = inserSqlWrapper.sql();
            params.putAll(inserSqlWrapper.params);
            return builder.append("\n").append(sql).toString();
        }
        return builder.toString();
    }

    private void joins(SafeAppendable builder) {
        sqlClause(builder, "JOIN", join, "", "", "\nJOIN ");
        sqlClause(builder, "INNER JOIN", innerJoin, "", "", "\nINNER JOIN ");
        sqlClause(builder, "OUTER JOIN", outerJoin, "", "", "\nOUTER JOIN ");
        sqlClause(builder, "LEFT OUTER JOIN", leftOuterJoin, "", "", "\nLEFT OUTER JOIN ");
        sqlClause(builder, "RIGHT OUTER JOIN", rightOuterJoin, "", "", "\nRIGHT OUTER JOIN ");
    }


    /**
     * 拼接 SQL 片段，将 parts 列表用指定关键字、分隔符、前后缀拼接到 builder 中。
     *
     * @param builder     用于拼接 SQL 的 SafeAppendable 对象
     * @param keyword     SQL 关键字（如 SELECT、WHERE 等），可为空
     * @param parts       需要拼接的 SQL 片段列表
     * @param open        前缀（如 "(", ""）
     * @param close       后缀（如 ")", ""）
     * @param conjunction 片段之间的分隔符（如 ", ", " AND "）
     */
    private void sqlClause(SafeAppendable builder, String keyword, List<String> parts, String open, String close,
                           String conjunction) {
        if (!parts.isEmpty()) {
            if (!builder.isEmpty()) {
                builder.append("\n");
            }
            if (StringUtils.hasLength(keyword)) {
                builder.append(keyword);
                builder.append(" ");
            }
            builder.append(open);
            String last = "________";
            for (int i = 0, n = parts.size(); i < n; i++) {
                String part = parts.get(i);
                if (i > 0 && StringUtils.hasLength(last) &&
                        !part.equals(WhereJoinType.AND.key)
                        && !part.equals(WhereJoinType.OR.key)
                        && !last.equals(WhereJoinType.AND.key)
                        && !last.equals(WhereJoinType.OR.key)) {
                    builder.append(conjunction);
                }
                builder.append(part);
                last = part;
            }
            builder.append(close);
        }
    }

    public boolean hasOrders() {
        return !orderBy.isEmpty();
    }

    public boolean hasTable() {
        return !tables.isEmpty();
    }


    private static class SafeAppendable {
        protected Appendable appendable;
        @Getter
        private boolean empty = true;

        public SafeAppendable(Appendable a) {
            super();
            this.appendable = a;
        }

        public SafeAppendable append(CharSequence s) {
            try {
                if (empty && !s.isEmpty()) {
                    empty = false;
                }
                appendable.append(s);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        @Override
        public String toString() {
            return appendable.toString();
        }
    }


    private enum LimitingRowsStrategy {
        NOP {
            @Override
            protected void appendClause(SafeAppendable builder, String offset, String limit) {
                // NOP
            }
        },
        ISO {
            @Override
            protected void appendClause(SafeAppendable builder, String offset, String limit) {
                if (offset != null) {
                    builder.append(" OFFSET ").append(offset).append(" ROWS");
                }
                if (limit != null) {
                    builder.append(" FETCH FIRST ").append(limit).append(" ROWS ONLY");
                }
            }
        },
        OFFSET_LIMIT {
            @Override
            protected void appendClause(SafeAppendable builder, String offset, String limit) {
                if (limit != null) {
                    builder.append(" LIMIT ").append(limit);
                }
                if (offset != null) {
                    builder.append(" OFFSET ").append(offset);
                }
            }
        };

        protected abstract void appendClause(SafeAppendable builder, String offset, String limit);
    }

    /**
     * 需要分页的sql包装器
     * 用于解决嵌套分页分体
     * 如：select * from (select * from table limit 10,10) a left join b on a.id=b.id
     * 实际需要计算分页的是table，并非整个sql
     */
    @Getter
    @Setter
    public static class PageWrapper {
        private IPage page;
        private SqlWrapper sqlWrapper;

        public PageWrapper(IPage page, SqlWrapper sqlWrapper) {
            this.page = page;
            this.sqlWrapper = sqlWrapper;
        }
    }

    /**
     * 校验 SQL 标识符是否安全（只允许字母、数字、下划线、点、星号）。
     *
     * @param str 待校验的 SQL 标识符
     * @return true 表示不安全，false 表示安全
     */
    private boolean isNotSafeSqlIdentifier(String str) {
        return str == null || !str.matches("[a-zA-Z0-9_.*]+(\\.[a-zA-Z0-9_.*]+)*");
    }
}

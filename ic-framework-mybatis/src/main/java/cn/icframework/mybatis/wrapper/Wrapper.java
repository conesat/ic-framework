package cn.icframework.mybatis.wrapper;

import cn.icframework.common.lambda.LambdaGetter;
import cn.icframework.common.lambda.LambdaUtils;
import cn.icframework.mybatis.query.QueryField;
import cn.icframework.mybatis.query.QueryTable;
import cn.icframework.mybatis.utils.ModelClassUtils;

/**
 * Wrapper 是 SQL 构造器的统一入口工具类。
 * <p>
 * 主要用于快速、类型安全地构建常用 SQL 语句片段（如 SELECT、UPDATE、DELETE、INSERT 等），
 * 通过静态工厂方法简化 SQL 构造流程，提升代码可读性和开发效率。
 * <br>
 * 该类只负责 SQL 片段的构造，不负责 SQL 的执行。
 * <br>
 * 复杂的 SQL 函数（如 MAX、MIN、COUNT、字符串处理等）请直接使用 FunctionWrapper。
 * 
 * <b>示例：</b>
 * <pre>
 *     // 构造 select 语句
 *     SelectWrapper select = Wrapper.SELECT(UserTable.ID, UserTable.NAME);
 *
 *     // 构造 update 语句
 *     UpdateWrapper update = Wrapper.UPDATE(UserTable.INSTANCE);
 *
 *     // 构造 delete 语句
 *     DeleteWrapper delete = Wrapper.DELETE();
 * </pre>
 */
public class Wrapper {

    /**
     * 构造 SELECT 查询，指定需要查询的字段。
     *
     * @param queryTables 需要查询的字段（通常为 Table.FIELD 形式）
     * @return SelectWrapper 用于进一步链式构造 SQL
     */
    public static SelectWrapper SELECT(QueryTable<?>... queryTables) {
        return SDIWrapper.SELECT(queryTables);
    }

    /**
     * 构造 SELECT 查询，支持原始 SQL 片段或嵌套子查询。
     * <p>
     * 传入的参数会原样拼接到 select 语句中。
     * 例如：SELECT(1) => select 1，SELECT("name") => select `name`
     * 
     * @param selects 查询内容（可为数字、字符串、子查询等）
     * @return SelectWrapper
     */
    public static SelectWrapper SELECT(Object... selects) {
        return SDIWrapper.SELECT(selects);
    }

    /**
     * 构造 SELECT ... FROM ... 查询，指定表对象。
     *
     * @param queryTables 需要查询的表
     * @return SqlWrapper
     */
    public static SqlWrapper SELECT_FROM(QueryTable<?>... queryTables) {
        return SDIWrapper.SELECT_FROM(queryTables);
    }

    /**
     * 构造 SELECT ... FROM ... 查询，指定实体类。
     *
     * @param entity 实体类（如 User.class）
     * @return SqlWrapper
     */
    public static SqlWrapper SELECT_FROM(Class<?>... entity) {
        return SDIWrapper.SELECT().FROM_ENTITY(entity);
    }

    /**
     * 构造 SELECT ... FROM ... 查询，指定实体类，并控制是否进行 XML 转义。
     *
     * @param coveXml 是否进行 XML 转义
     * @param entity 实体类
     * @return SqlWrapper
     */
    public static SqlWrapper SELECT_FROM(boolean coveXml, Class<?>... entity) {
        return SDIWrapper.SELECT().FROM_ENTITY(coveXml, entity);
    }

    /**
     * 构造 SELECT DISTINCT 查询，指定需要去重的字段。
     *
     * @param queryTables 需要去重的字段
     * @return SelectWrapper
     */
    public static SelectWrapper SELECT_DISTINCT(QueryTable<?>... queryTables) {
        return SDIWrapper.SELECT_DISTINCT(queryTables);
    }

    /**
     * 构造 SELECT DISTINCT 查询，支持原始 SQL 片段或嵌套子查询。
     *
     * @param selects 查询内容
     * @return SelectWrapper
     */
    public static SelectWrapper SELECT_DISTINCT(Object... selects) {
        return SDIWrapper.SELECT_DISTINCT(selects);
    }

    /**
     * 构造 SELECT * 查询，不指定字段，默认查询 FROM 表的所有字段。
     *
     * @return SelectWrapper
     */
    public static SelectWrapper SELECT() {
        return SDIWrapper.SELECT();
    }

    /**
     * 构造 SELECT DISTINCT * 查询，不指定字段，默认查询 FROM 表的所有字段并去重。
     *
     * @return SelectWrapper
     */
    public static SelectWrapper SELECT_DISTINCT() {
        return SDIWrapper.SELECT_DISTINCT();
    }

    /**
     * 构造 SELECT 查询，指定 QueryField 字段。
     *
     * @param queryFields 需要查询的字段
     * @return SelectWrapper
     */
    public static SelectWrapper SELECT(QueryField<?>... queryFields) {
        return SDIWrapper.SELECT(queryFields);
    }

    /**
     * 构造 UPDATE 语句，指定需要更新的表。
     *
     * @param queryTable 需要更新的表
     * @return UpdateWrapper
     */
    public static UpdateWrapper UPDATE(QueryTable<?> queryTable) {
        return SDIWrapper.UPDATE(queryTable);
    }

    /**
     * 构造 UPDATE 语句，指定需要更新的表名。
     *
     * @param tableName 表名
     * @return UpdateWrapper
     */
    public static UpdateWrapper UPDATE(String tableName) {
        return SDIWrapper.UPDATE(tableName);
    }

    /**
     * 构造 DELETE 语句。
     *
     * @return DeleteWrapper
     */
    public static DeleteWrapper DELETE() {
        return SDIWrapper.DELETE();
    }

    /**
     * 构造 DELETE FROM ... 语句，指定表对象。
     *
     * @param queryTable 需要删除的表
     * @return SqlWrapper
     */
    public static SqlWrapper DELETE_FROM(QueryTable<?> queryTable) {
        return SDIWrapper.DELETE_FROM(queryTable);
    }

    /**
     * 构造 DELETE FROM ... 语句，指定实体类。
     *
     * @param entity 实体类
     * @return SqlWrapper
     */
    public static SqlWrapper DELETE_FROM(Class<?> entity) {
        return SDIWrapper.DELETE_FROM(false, entity);
    }

    /**
     * 构造 DELETE FROM ... 语句，指定实体类，并控制是否进行 XML 转义。
     *
     * @param entity 实体类
     * @param coverXml 是否进行 XML 转义
     * @return SqlWrapper
     */
    public static SqlWrapper DELETE_FROM(boolean coverXml, Class<?> entity) {
        return SDIWrapper.DELETE_FROM(coverXml, entity);
    }

    /**
     * 构造 INSERT 语句。
     *
     * @return InsertWrapper
     */
    public static InsertWrapper INSERT() {
        return SDIWrapper.INSERT();
    }

    /**
     * 构造 INSERT INTO ... 语句，指定插入的表。
     *
     * @param queryTable 需要插入的表
     * @return InsertWrapper
     */
    public static InsertWrapper INSERT_INTO(QueryTable<?> queryTable) {
        return SDIWrapper.INSERT_INTO(queryTable);
    }

    /**
     * 构造 OR 条件，包装多个表条件参数。
     *
     * @param queryTables 条件参数
     * @return QueryTable
     */
    public static QueryTable<?> OR(QueryTable<?>... queryTables) {
        return AOWrapper.OR(queryTables);
    }

    /**
     * 构造 AND 条件，包装多个表条件参数。
     *
     * @param queryTables 条件参数
     * @return QueryTable
     */
    public static QueryTable<?> AND(QueryTable<?>... queryTables) {
        return AOWrapper.AND(queryTables);
    }

    /**
     * 字段重命名（AS），用于 select 查询结果重命名。
     *
     * @param object 字段对象
     * @param column 新的列名
     * @return QueryField
     */
    public static QueryField<?> AS(Object object, String column) {
        return QueryField.of(object).as(column);
    }

    /**
     * 字段重命名（AS），用于 select 查询结果重命名，支持 LambdaGetter。
     *
     * @param object 字段对象
     * @param lambdaGetter lambda 表达式获取字段
     * @return QueryField
     */
    public static <T> QueryField<?> AS(Object object, LambdaGetter<T> lambdaGetter) {
        return QueryField.of(object).as(ModelClassUtils.getTableColumnName(LambdaUtils.getField(lambdaGetter)));
    }

}

package cn.icframework.mybatis.wrapper;

import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.consts.StatementType;
import cn.icframework.mybatis.query.QueryField;
import cn.icframework.mybatis.query.QueryTable;
import cn.icframework.mybatis.utils.ModelClassUtils;

import java.lang.reflect.Field;

public class SDIWrapper {

    /**
     * 需要查询的字段
     *
     * @param queryTables 字段列表
     * @return SelectWrapper
     */
    protected static SelectWrapper SELECT(QueryTable<?>... queryTables) {
        return new SelectWrapper(false, queryTables);
    }

    /**
     * 查询原始内容，输入的参数都将保留原格式拼接到select
     * 如
     * SELECT(1) =>  select 1
     * SELECT("1") => select `1`
     * @param normalSelects 查询列表
     * @return SelectWrapper
     */
    protected static SelectWrapper SELECT(Object... normalSelects) {
        return new SelectWrapper(false, normalSelects);
    }


    /**
     * 需要查询的字段
     *
     * @param queryTables 表
     * @return SqlWrapper
     */
    protected static SqlWrapper SELECT_FROM(QueryTable<?>... queryTables) {
        SqlWrapper sqlWrapper = new SqlWrapper(StatementType.SELECT);
        sqlWrapper.from(queryTables);
        return sqlWrapper;
    }

    /**
     * 需要查询的字段
     * 去重
     *
     * @param queryTables 字段列表
     * @return SelectWrapper
     */
    protected static SelectWrapper SELECT_DISTINCT(QueryTable<?>... queryTables) {
        return new SelectWrapper(true, queryTables);
    }

    /**
     * 去重查询原始内容，输入的参数都将保留原格式拼接到select
     * 如
     * SELECT(1) =>  select distinct 1
     * SELECT("1") => select distinct  `1`
     * @param normalSelects 查询列表
     * @return SelectWrapper
     */
    protected static SelectWrapper SELECT_DISTINCT(Object... normalSelects) {
        return new SelectWrapper(true, normalSelects);
    }

    /**
     * 不指定查询字段，字段全部从FROM的表获取
     *
     * @return SelectWrapper
     */
    protected static SelectWrapper SELECT() {
        return new SelectWrapper(false);
    }


    /**
     * 不指定查询字段，字段全部从FROM的表获取
     * 去重
     *
     * @return SelectWrapper
     */
    protected static SelectWrapper SELECT_DISTINCT() {
        return new SelectWrapper(true);
    }

    /**
     * 需要查询的字段
     *
     * @param queryFields 字段列表
     * @return SelectWrapper
     */
    protected static SelectWrapper SELECT(QueryField<?>... queryFields) {
        return new SelectWrapper(false, queryFields);
    }


    /**
     * 需要更新字段的表
     *
     * @param queryTable 表
     * @return UpdateWrapper
     */
    protected static UpdateWrapper UPDATE(QueryTable<?> queryTable) {
        return new UpdateWrapper(queryTable);
    }

    /**
     * 需要更新字段的表
     *
     * @param tableName 表
     * @return UpdateWrapper
     */
    protected static UpdateWrapper UPDATE(String tableName) {
        return new UpdateWrapper(tableName);
    }

    /**
     * 需要更新字段的表
     *
     * @return DeleteWrapper
     */
    protected static DeleteWrapper DELETE() {
        return new DeleteWrapper();
    }

    /**
     * 需要更新字段的表
     *
     * @param queryTable 表
     * @return SqlWrapper
     */
    protected static SqlWrapper DELETE_FROM(QueryTable<?> queryTable) {
        Table table = queryTable.getTableClass().getAnnotation(Table.class);
        Field logicDeleteField = ModelClassUtils.getLogicDelete(queryTable.getTableClass());
        if (logicDeleteField != null) {
            UpdateWrapper updateWrapper = new UpdateWrapper(queryTable);
            updateWrapper.setLogicDel(ModelClassUtils.getTableColumnName(table, logicDeleteField));
            return updateWrapper;
        }
        DeleteWrapper deleteWrapper = new DeleteWrapper();
        return deleteWrapper.FROM(queryTable);
    }

    /**
     * 插入数据
     *
     * @return InsertWrapper
     */
    protected static InsertWrapper INSERT() {
        return new InsertWrapper();
    }

    /**
     * 插入数据
     * @param queryTable 需要插入的表数据
     * @return InsertWrapper
     */
    protected static InsertWrapper INSERT_INTO(QueryTable<?> queryTable) {
        InsertWrapper insertWrapper = new InsertWrapper();
        insertWrapper.INTO(queryTable);
        return insertWrapper;
    }

    public static SqlWrapper DELETE_FROM( boolean coverXml, Class<?> entity) {
        return new DeleteWrapper().FROM(coverXml, entity);
    }
}

package cn.icframework.mybatis.wrapper;

import cn.icframework.mybatis.consts.StatementType;
import cn.icframework.mybatis.query.QueryField;
import cn.icframework.mybatis.query.QueryTable;

public class UpdateWrapper extends SqlWrapper {

    public UpdateWrapper(QueryTable<?> queryTable) {
        super(StatementType.UPDATE);
        super.update(queryTable);
    }
    public UpdateWrapper(Class<?> entity) {
        super(StatementType.UPDATE);
        super.update(entity);
    }
    public UpdateWrapper(String tableName) {
        super(StatementType.UPDATE);
        super.update(tableName);
    }

    /**
     * 设置需要更新的字段
     *
     * @param queryField 字段
     * @param val        值
     * @return SqlQuery
     */
    public UpdateWrapper SET(QueryField<?> queryField, Object val) {
        super.set(queryField.getTableColumn(), val);
        return this;
    }

    /**
     * 设置需要更新的字段
     *
     * @param queryTable 表
     * @return SqlQuery
     */
    public UpdateWrapper SET(QueryTable<?> queryTable) {
        super.set(queryTable);
        return this;
    }


    /**
     * 设置需要更新的字段
     *
     * @param field 字段
     * @param val 值
     * @return SqlQuery
     */
    public UpdateWrapper SET(String field, Object val) {
        super.set(field, val);
        return this;
    }


    /**
     * 不转换参数 val是什么就set什么
     * ！！！注意sql注入
     * @param field 字段
     * @param val 值
     * @return SqlQuery
     */
    public UpdateWrapper SET_SOURCE_PARAM(String field, Object val) {
        super.setSourceParam(field, val);
        return this;
    }
}

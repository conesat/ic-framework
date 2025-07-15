package cn.icframework.mybatis.wrapper;

import cn.icframework.mybatis.consts.StatementType;
import cn.icframework.mybatis.query.QueryField;
import cn.icframework.mybatis.query.QueryTable;

public class SelectWrapper {

    SqlWrapper sqlWrapper = new SqlWrapper(StatementType.SELECT);

    public SelectWrapper(boolean distinct, QueryTable<?>... queryTables) {
        if (distinct) {
            sqlWrapper.selectDistinct((Object[]) queryTables);
        } else {
            sqlWrapper.select((Object[]) queryTables);
        }
    }
    public SelectWrapper(boolean distinct, Object... normalSelects) {
        if (distinct) {
            sqlWrapper.selectDistinct(normalSelects);
        } else {
            sqlWrapper.select(normalSelects);
        }
    }

    public SelectWrapper(boolean distinct, QueryField<?>... queryFields) {
        if (distinct) {
            sqlWrapper.selectDistinct((Object[]) queryFields);
        } else {
            sqlWrapper.select((Object[]) queryFields);
        }
    }

    public SelectWrapper(boolean distinct) {
        if (distinct) {
            sqlWrapper.selectDistinct();
        } else {
            sqlWrapper.select();
        }
    }

    /**
     * 需要查询的表
     *
     * @param queryTables 表
     * @return SqlWrapper
     */
    public FromWrapper FROM(QueryTable<?>... queryTables) {
        sqlWrapper.from(queryTables);
        return new FromWrapper(sqlWrapper);
    }

    /**
     * 需要查询的表
     *
     * @param sqlWrapper 子查询
     * @return SqlWrapper
     */
    public FromWrapper FROM(SqlWrapper sqlWrapper) {
        sqlWrapper.from(sqlWrapper);
        return new FromWrapper(sqlWrapper);
    }


    /**
     * 需要查询的实体
     *
     * @param entities 实体
     * @return SqlWrapper
     */
    public FromWrapper FROM_ENTITY(Class<?>... entities) {
        sqlWrapper.fromEntity(entities);
        return new FromWrapper(sqlWrapper);
    }

    /**
     * 需要查询的实体
     *
     * @param entities 实体
     * @return SqlWrapper
     */
    public FromWrapper FROM_ENTITY(boolean coverXml, Class<?>... entities) {
        sqlWrapper.setCoverXml(coverXml);
        sqlWrapper.fromEntity(entities);
        return new FromWrapper(sqlWrapper);
    }

    /**
     * 获取sql
     * @return
     */
    public String sql() {
        return sqlWrapper.sql();
    }
}

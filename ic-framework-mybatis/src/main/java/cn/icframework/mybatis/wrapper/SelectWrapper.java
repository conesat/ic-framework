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
        FromWrapper fromWrapper = new FromWrapper(sqlWrapper);
        this.sqlWrapper = fromWrapper;
        return fromWrapper;
    }

    /**
     * 需要查询的表
     *
     * @param sqlWrapper 子查询
     * @return SqlWrapper
     */
    public FromWrapper FROM(SqlWrapper subQuery) {
        this.sqlWrapper.from(subQuery);
        FromWrapper fromWrapper = new FromWrapper(this.sqlWrapper);
        this.sqlWrapper = fromWrapper;
        return fromWrapper;
    }


    /**
     * 需要查询的实体
     *
     * @param entities 实体
     * @return SqlWrapper
     */
    public FromWrapper FROM_ENTITY(Class<?>... entities) {
        sqlWrapper.fromEntity(entities);
        FromWrapper fromWrapper = new FromWrapper(sqlWrapper);
        this.sqlWrapper = fromWrapper;
        return fromWrapper;
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
        FromWrapper fromWrapper = new FromWrapper(sqlWrapper);
        this.sqlWrapper = fromWrapper;
        return fromWrapper;
    }

    /**
     * 将查询转为子查询
     *
     * @param as 别名
     * @return SqlWrapper
     */
    public SqlWrapper AS(String as) {
        return this.sqlWrapper.AS(as);
    }

    /**
     * 获取sql
     *
     * @return
     */
    public String sql() {
        return sqlWrapper.sql();
    }
}

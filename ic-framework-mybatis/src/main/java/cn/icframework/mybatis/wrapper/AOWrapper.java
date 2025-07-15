package cn.icframework.mybatis.wrapper;

import cn.icframework.mybatis.query.QueryTable;
import cn.icframework.mybatis.consts.WhereJoinType;

public class AOWrapper {

    /**
     * 包装Or条件
     *
     * @param queryTables 表条件参数
     * @return SqlQuery
     */
    protected static QueryTable<?> OR(QueryTable<?>... queryTables) {
        return new QueryTable<>(WhereJoinType.OR, queryTables);
    }

    /**
     * 包装AND条件
     *
     * @param queryTables 表条件参数
     * @return SqlQuery
     */
    protected static QueryTable<?> AND(QueryTable<?>... queryTables) {
        return new QueryTable<>(WhereJoinType.AND, queryTables);
    }

}

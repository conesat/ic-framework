package cn.icframework.mybatis.wrapper;

import cn.icframework.mybatis.query.QueryTable;

public class JoinWrapper {
    SqlWrapper root;

    public JoinWrapper(SqlWrapper sqlQuery) {
        this.root = sqlQuery;
    }
    /**
     * 连接条件
     *
     * @param joinTable 需要连接的表
     * @return SqlQuery
     */
    public FromWrapper ON(QueryTable<?> joinTable) {
        root.joinOn(joinTable);
        return new FromWrapper(root);
    }
    /**
     * 连接条件
     *
     * @param objects 需要连接的表
     * @return SqlQuery
     */
    public FromWrapper ON(Object... objects) {
        root.joinOn(objects);
        return new FromWrapper(root);
    }
}

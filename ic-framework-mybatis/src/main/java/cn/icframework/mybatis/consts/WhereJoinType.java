package cn.icframework.mybatis.consts;

/**
 * 条件连接符
 */
public enum WhereJoinType {
    OR(") OR ("),
    AND(") AND (");

    public final String key;

    WhereJoinType(String key) {
        this.key = key;
    }
}

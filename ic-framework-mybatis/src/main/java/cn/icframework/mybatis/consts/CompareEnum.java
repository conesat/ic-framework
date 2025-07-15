package cn.icframework.mybatis.consts;

/**
 * 比较符号
 */
public enum CompareEnum {
    LIKE(" LIKE "),
    LEFT_LIKE(" LIKE "),
    RIGHT_LIKE(" LIKE "),
    GT(" > "),
    GT_XML(" &gt; "),
    GE(" >= "),
    GE_XML(" &gt;= "),
    LT(" < "),
    LT_XML(" &lt; "),
    LE(" <= "),
    LE_XML(" &lt;= "),
    EQ(" = "),
    NE(" <> "),
    NE_XML(" &lt;&gt; "),
    IS_NULL(" IS NULL "),
    IS_NOT_NULL(" IS NOT NULL "),
    IN(" IN "),
    NOT_IN(" NOT IN "),
    BETWEEN(" BETWEEN "),
    NOT_BETWEEN(" NOT BETWEEN ");

    private final String key;
    public String getKey(boolean coverXml) {
        if (coverXml) {
            switch (this){
                case GT:
                    return GT_XML.key;
                case GE:
                    return GE_XML.key;
                case LT:
                    return LT_XML.key;
                case LE:
                    return LE_XML.key;
                case NE:
                    return NE_XML.key;
            }
        }
        return key;
    }

    CompareEnum(String key) {
        this.key = key;
    }
}

package cn.icframework.common;


/**
 * @author hzl
 * @since 2023/6/14
 */
public class MysqlType {
    private Integer length;
    private Integer fraction;
    private String type;
    private String defaultVueValue;

    public MysqlType(String type, Integer length, Integer fraction, String defaultVueValue) {
        this.length = length;
        this.fraction = fraction;
        this.type = type;
        this.defaultVueValue = defaultVueValue;
    }

    public MysqlType(String type, String defaultVueValue) {
        this.type = type;
        this.defaultVueValue = defaultVueValue;
    }

    @Override
    public String toString() {
        String l = "";
        if (length != null && length >= 0) {
            l += "(" + length;
            if (fraction != null) {
                l += "," + fraction;
            }
            l += ")";
        }
        return type + l;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getFraction() {
        return fraction;
    }

    public void setFraction(Integer fraction) {
        this.fraction = fraction;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultVueValue() {
        return defaultVueValue;
    }

    public void setDefaultVueValue(String defaultVueValue) {
        this.defaultVueValue = defaultVueValue;
    }
}

package cn.icframework.mybatis.consts;

import lombok.Getter;
import lombok.Setter;


/**
 * MySQL数据类型枚举。
 * 定义MySQL中常用的数据类型。
 * @author hzl
 */
@Getter
@Setter
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
}

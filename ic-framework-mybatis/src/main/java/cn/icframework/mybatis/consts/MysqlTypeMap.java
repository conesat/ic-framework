package cn.icframework.mybatis.consts;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Objects;

/**
 * MySQL数据类型映射工具类。
 * 提供Java类型与MySQL类型的映射关系。
 * @author hzl
 */
public class MysqlTypeMap {
    public static MysqlType getType(String type) {
        if (Objects.equals(type, Boolean.class.toString().replace("class ", ""))) {
            return new MysqlType("bit", 1, null, "0");
        }
        if (Objects.equals(type, boolean.class.toString().replace("class ", ""))) {
            return new MysqlType("bit", 1, null, "0");
        }
        if (Objects.equals(type, Byte.class.toString().replace("class ", ""))) {
            return new MysqlType("bit", 1, null, "0");
        }
        if (Objects.equals(type, byte.class.toString().replace("class ", ""))) {
            return new MysqlType("bit", 1, null, "0");
        }
        if (Objects.equals(type, Short.class.toString().replace("class ", ""))) {
            return new MysqlType("int",  "0");
        }
        if (Objects.equals(type, short.class.toString().replace("class ", ""))) {
            return new MysqlType("int",  "0");
        }
        if (Objects.equals(type, Integer.class.toString().replace("class ", ""))) {
            return new MysqlType("int", "0");
        }
        if (Objects.equals(type, int.class.toString().replace("class ", ""))) {
            return new MysqlType("int", "0");
        }
        if (Objects.equals(type, char.class.toString().replace("class ", ""))) {
            return new MysqlType("varchar", 1, null, "''");
        }
        if (Objects.equals(type, Character.class.toString().replace("class ", ""))) {
            return new MysqlType("varchar", 1, null, "''");
        }
        if (Objects.equals(type, Long.class.toString().replace("class ", ""))) {
            return new MysqlType("bigint", "0");
        }
        if (Objects.equals(type, long.class.toString().replace("class ", ""))) {
            return new MysqlType("bigint", "0");
        }
        if (Objects.equals(type, Float.class.toString().replace("class ", ""))) {
            return new MysqlType("float", 11, 4, "0");
        }
        if (Objects.equals(type, float.class.toString().replace("class ", ""))) {
            return new MysqlType("float", 11, 4, "0");
        }
        if (Objects.equals(type, Double.class.toString().replace("class ", ""))) {
            return new MysqlType("double", 20, 6, "0");
        }
        if (Objects.equals(type, double.class.toString().replace("class ", ""))) {
            return new MysqlType("double", 20, 6, "0");
        }
        if (Objects.equals(type, String.class.toString().replace("class ", ""))) {
            return new MysqlType("varchar", 255, null, "''");
        }
        if (Objects.equals(type, BigInteger.class.toString().replace("class ", ""))) {
            return new MysqlType("bigint", "0");
        }
        if (Objects.equals(type, BigDecimal.class.toString().replace("class ", ""))) {
            return new MysqlType("decimal", 10, 3, "0");
        }
        if (Objects.equals(type, Byte[].class.toString().replace("class ", ""))) {
            return new MysqlType("binary", null, null, "''");
        }
        if (Objects.equals(type, byte[].class.toString().replace("class ", ""))) {
            return new MysqlType("binary", null, null, "''");
        }
        if (Objects.equals(type, Date[].class.toString().replace("class ", ""))) {
            return new MysqlType("datetime", "''");
        }
        if (Objects.equals(type, java.sql.Date.class.toString().replace("class ", ""))) {
            return new MysqlType("datetime", "''");
        }
        if (Objects.equals(type, LocalDateTime.class.toString().replace("class ", ""))) {
            return new MysqlType("datetime","''");
        }
        if (Objects.equals(type, LocalDate.class.toString().replace("class ", ""))) {
            return new MysqlType("date", "''");
        }
        if (Objects.equals(type, LocalTime.class.toString().replace("class ", ""))) {
            return new MysqlType("time", "''");
        }
        return null;
    }
}

package cn.icframework.mybatis.utils;

import cn.icframework.common.interfaces.IEnum;
import cn.icframework.common.utils.ConvertUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 类型转换工具，支持常见类型、枚举、时间等自动转换
 */
public class TypeConvertUtils {
    public static Object coverType(Class<?> type, Object v) {
        if (v == null) {
            return null;
        }
        if (type.isInstance(v)) {
            return v;
        }
        if (type == String.class) {
            return v.toString();
        }
        if (type == boolean.class || type == Boolean.class) {
            return ConvertUtils.toBoolean(v, false);
        }
        if ((v instanceof Integer || v instanceof Long) && type.isEnum() && IEnum.class.isAssignableFrom(type)) {
            Object[] enumConstants = type.getEnumConstants();
            for (Object ev : enumConstants) {
                if (((IEnum) ev).code() == (int) v) {
                    return ev;
                }
            }
        }
        if ((v instanceof Timestamp) && type.equals(LocalDateTime.class)) {
            return ((Timestamp) v).toLocalDateTime();
        }
        if ((v instanceof Time) && type.equals(LocalTime.class)) {
            return ((Time) v).toLocalTime();
        }
        if ((v instanceof java.sql.Date) && type.equals(LocalDate.class)) {
            return ((java.sql.Date) v).toLocalDate();
        }
        try {
            Method valueOf = type.getMethod("valueOf", String.class);
            return valueOf.invoke(type, v.toString());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
            return v;
        }
    }
} 
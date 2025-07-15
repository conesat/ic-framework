package cn.icframework.mybatis.utils;

import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;

/**
 * 反射工具类，封装set方法调用并自动类型转换
 */
@Slf4j
public class ReflectUtils {
    public static void setValue(Object o, Method setMethod, Object v) {
        Class<?> type = setMethod.getParameterTypes()[0];
        try {
            setMethod.invoke(o, TypeConvertUtils.coverType(type, v));
        } catch (Exception e) {
            log.error("参数转换异常 method={} valueType={} targetType={} value={}", setMethod.getName(), v != null ? v.getClass().getSimpleName() : "null", type.getSimpleName(), v, e);
            throw new RuntimeException("参数转换异常 " + setMethod.getName() + " " + (v != null ? v.getClass().getSimpleName() : "null") + " >> " + type.getSimpleName(), e);
        }
    }
} 
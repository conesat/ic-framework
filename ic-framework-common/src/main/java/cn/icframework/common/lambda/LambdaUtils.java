package cn.icframework.common.lambda;

import cn.icframework.common.utils.cache.CacheUtil;
import cn.icframework.common.utils.cache.LFUCache;

import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Lambda表达式工具类
 *
 * 提供通过LambdaGetter获取字段名、字段对象、字段注解等能力。
 *
 * @author hzl
 * @since 2023/5/20
 */
public class LambdaUtils {
    /**
     * 缓存解析的字段名
     */
    private final static LFUCache<String, String> fieldNameCache = CacheUtil.newLFUCache(100);
    /**
     * 缓存字段
     */
    private final static LFUCache<String, Field> fieldCache = CacheUtil.newLFUCache(100);

    /**
     * 获取字段上的指定注解
     * @param lambdaGetter LambdaGetter
     * @param annotation 注解类型
     * @return 字段上的注解
     */
    public static <T> Annotation getFieldAnnotation(LambdaGetter<T> lambdaGetter, Class<? extends Annotation> annotation) {
        try {
            return getField(lambdaGetter).getDeclaredAnnotation(annotation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取字段对象
     * @param lambdaGetter LambdaGetter
     * @return 字段对象
     */
    public static <T> Field getField(LambdaGetter<T> lambdaGetter) {
        try {
            Field field = fieldCache.get(lambdaGetter.toString());
            if (field == null) {
                // 获取 Lambda 表达式的 SerializedLambda 对象
                SerializedLambda serializedLambda = getSerializedLambda(lambdaGetter);
                // 解析出方法名
                String fieldName = getFieldName(serializedLambda);
                Class<?> aClass = Class.forName(serializedLambda.getImplClass().replaceAll("/", "."));
                // 使用反射获取字段
                field = aClass.getDeclaredField(fieldName);
                fieldCache.put(lambdaGetter.toString(), field);
            }
            return field;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取字段名称
     * @param lambdaGetter LambdaGetter
     * @return 字段名称
     */
    public static <T> String getFieldName(LambdaGetter<T> lambdaGetter) {
        try {
            String name = fieldNameCache.get(lambdaGetter.toString());
            if (name == null) {
                // 获取 Lambda 表达式的 SerializedLambda 对象
                SerializedLambda serializedLambda = getSerializedLambda(lambdaGetter);
                // 解析出方法名
                name = getFieldName(serializedLambda);
                fieldNameCache.put(lambdaGetter.toString(), name);
            }
            return name;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取LambdaGetter对应的SerializedLambda对象
     * @param LambdaGetter LambdaGetter
     * @return SerializedLambda
     * @throws NoSuchMethodException 反射异常
     * @throws IllegalAccessException 反射异常
     * @throws InvocationTargetException 反射异常
     */
    private static <T> SerializedLambda getSerializedLambda(LambdaGetter<T> LambdaGetter) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method writeReplaceMethod = LambdaGetter.getClass().getDeclaredMethod("writeReplace");
        writeReplaceMethod.setAccessible(true);
        return (SerializedLambda) writeReplaceMethod.invoke(LambdaGetter);
    }

    /**
     * 通过SerializedLambda解析字段名
     * @param serializedLambda SerializedLambda
     * @return 字段名
     */
    private static String getFieldName(SerializedLambda serializedLambda) {
        String methodName = serializedLambda.getImplMethodName();
        String prefix = methodName.startsWith("get") ? "get" : methodName.startsWith("is") ? "is" : "set";
        String fieldName = methodName.substring(prefix.length());
        fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
        return fieldName;
    }

}

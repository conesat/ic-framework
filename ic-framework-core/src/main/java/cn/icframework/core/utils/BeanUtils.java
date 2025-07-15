package cn.icframework.core.utils;

import cn.icframework.common.lambda.LambdaGetter;
import cn.icframework.common.lambda.LambdaUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessorFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean工具类
 *
 * 扩展Spring的BeanUtils。
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {
    /**
     * 默认构造方法
     */
    public BeanUtils() {}

    /**
     * 拷贝指定属性字段的值，从源对象到目标对象。
     * @param src 源对象
     * @param trg 目标对象
     * @param fields 需要拷贝的属性字段（Lambda表达式）
     * @param <T> 属性类型
     */
    @SafeVarargs
    public static <T> void copyWithProps(Object src, Object trg, LambdaGetter<T>... fields) {
        BeanWrapper srcWrap = PropertyAccessorFactory.forBeanPropertyAccess(src);
        BeanWrapper trgWrap = PropertyAccessorFactory.forBeanPropertyAccess(trg);
        for (LambdaGetter<T> field : fields) {
            try {
                String fieldName = LambdaUtils.getFieldName(field);
                trgWrap.setPropertyValue(fieldName, srcWrap.getPropertyValue(fieldName));
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 拷贝除指定属性外的所有属性，从源对象到目标对象。
     * @param source 源对象
     * @param target 目标对象
     * @param fields 需要排除的属性字段（Lambda表达式）
     * @param <T> 属性类型
     */
    @SafeVarargs
    public static <T> void copyExcludeProps(Object source, Object target, LambdaGetter<T>... fields) {
        List<String> fieldNames = new ArrayList<>();
        for (LambdaGetter<T> field : fields) {
            fieldNames.add(LambdaUtils.getFieldName(field));
        }
        copyProperties(source, target, fieldNames.toArray(new String[0]));
    }

    /**
     * 拷贝源对象的所有属性到目标对象。
     * @param source 源对象
     * @param target 目标对象
     * @throws BeansException 拷贝过程中出现异常时抛出
     */
    public static void copyProperties(Object source, Object target) throws BeansException {
        if(source == null){
            return;
        }
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }

}

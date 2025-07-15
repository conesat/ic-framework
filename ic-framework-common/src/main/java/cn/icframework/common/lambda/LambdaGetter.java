package cn.icframework.common.lambda;

import java.io.Serializable;

/**
 * 获取属性的函数式接口
 *
 * @author hzl
 * @since 2023/5/19 0019
 */
@FunctionalInterface
public interface LambdaGetter<T> extends Serializable {
    /**
     * 获取指定对象的属性值
     * @param source 源对象
     * @return 属性值
     */
    Object get(T source);
}
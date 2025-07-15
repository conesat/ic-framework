package cn.icframework.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表映射
 * @author hzl
 * @since 2023/5/18
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Joins {
    /**
     * 连接关系
     */
    Join[] joins();

    /**
     * 跳过该对象的关联查询
     * 默认为false，但是存在部分情况，出现循环引用，导致一直死循环，可以设置为true，将不会处理该类的内部关联
     */
    boolean skipRelation() default false;
}

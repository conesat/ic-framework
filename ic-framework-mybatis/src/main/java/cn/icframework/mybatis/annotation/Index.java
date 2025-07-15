package cn.icframework.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表索引
 * @author hzl
 * @since 2023/5/18
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Index {
    /**
     * 索引名称
     */
    String name();

    /**
     * 是否唯一索引
     */
    boolean unique() default false;

    /**
     * 索引字段
     */
    String[] columns();
}

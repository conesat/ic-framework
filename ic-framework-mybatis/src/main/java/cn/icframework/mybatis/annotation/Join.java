package cn.icframework.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 关联表映射 关联
 * @author hzl
 * @since 2023/5/18
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Join {
    /**
     * 需要连接的表，如果不填就是当前注解标注的对象
     */
    Class<?> joinTable() default Void.class;

    /**
     * 指定关联实体的表，默认为当前实体，如果多级嵌套默认为上一个joinTable
     */
    Class<?> selfTable() default Void.class;

    /**
     * 关联表字段，必填
     * 注意是填写java字段
     */
    String joinTableField();

    /**
     * 用当前实体的该字段数据去和关联表column进行查询
     * 注意是填写java字段
     */
    String selfField();

    /**
     * 扩展条件
     */
    String where() default "";

    /**
     * 查询字段 不填就是所有
     */
    String[] select() default {};

    /**
     * 排序条件
     */
    String orderBy() default "";

    /**
     * 条件比较方式
     */
    String compare() default "=";

    /**
     * 跳过该对象的关联查询
     * 默认为false，但是存在部分情况，出现循环引用，导致一直死循环，可以设置为true，将不会处理该类的内部关联
     */
    boolean skipRelation() default false;
}

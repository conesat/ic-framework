package cn.icframework.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库表
 * @author hzl
 * @since 2023/5/18
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TableField {
    /**
     * 表名称
     */
    String value() default "";

    /**
     * insert 的时候默认值，这个值会直接被拼接到 sql 而不通过参数设置
     */
    String onInsertValue() default "";

    /**
     * update 的时候自动赋值，这个值会直接被拼接到 sql 而不通过参数设置
     */
    String onUpdateValue() default "";

    /**
     * 配置的 type如：DECIMAL(10,2)
     */
    String type() default "";

    /**
     * 注释
     */
    String comment() default "";

    /**
     * 是否是逻辑删除字段
     */
    boolean isLogicDelete() default false;

    /**
     * 是否禁止为空
     */
    boolean notNull() default false;

    /**
     * 长度 -1采用默认值
     */
    int length() default -1;

    /**
     * 小数长度 -1采用默认值
     */
    int fraction() default -1;

    /**
     * 默认值，仅用于生成sql，并不会在插入时生效
     */
    String defaultValue() default "";
}

package cn.icframework.mybatis.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 外键注解。
 *
 * @author ic-framework
 * @since 2024/06/09
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ForeignKey {

    /**
     * 外键名称。
     *
     * @return 外键名称
     */
    String name() default "";

    /**
     * 关联表。
     *
     * @return 关联表类型
     */
    Class<?> references();

    /**
     * 关联字段。
     *
     * @return 关联字段名
     */
    String referencesColumn() default "";

    /**
     * 删除时的操作。
     *
     * @return 删除操作类型
     */
    String onDelete() default ForeignKeyAction.NONE;

    /**
     * 更新时的操作。
     *
     * @return 更新操作类型
     */
    String onUpdate() default ForeignKeyAction.NONE;
}

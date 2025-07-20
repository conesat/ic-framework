package cn.icframework.mybatis.annotation;


import java.lang.annotation.*;

/**
 * 逻辑删除字段，只能用于布尔型
 * @since 1.0
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface LogicDelete {
}
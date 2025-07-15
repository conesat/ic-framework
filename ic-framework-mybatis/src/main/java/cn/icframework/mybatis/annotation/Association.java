package cn.icframework.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询结果映射到对象的注解。
 * 默认会自动映射对象，但如果对象与前缀不一致时，可以通过该注解指定前缀。
 *
 * @author ic-framework
 * @since 2024/06/09
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Association {
    /**
     * 需要封装到该列表的前缀，不指定的话用的是字段名。
     *
     * @return 前缀
     */
    String prefix() default "";
}

package cn.icframework.auth.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记不校验系统可用性的注解。
 *
 * @author ic-framework
 * @since 2024/06/09
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface NotValidateSystem {
    /**
     * 说明信息，默认为空字符串。
     * @return 说明
     */
    String value() default "";
}

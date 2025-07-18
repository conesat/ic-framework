package cn.icframework.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 异常信息注解
 *
 * 用于标记方法的异常描述信息。
 *
 * @author hzl
 * @since 2020/04/22
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ExceptionInfo {
    /**
     * 异常描述信息
     * @return 异常信息
     */
    String value() default "";
}

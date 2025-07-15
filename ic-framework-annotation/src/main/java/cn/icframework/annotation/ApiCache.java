package cn.icframework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于标记接口缓存相关信息的注解。
 *
 * @author ic-framework
 * @since 2024/06/09
 */
@Retention(RetentionPolicy.RUNTIME)
@Author(value = "何志龙", date = "2020/5/8")
public @interface ApiCache {
    /**
     * 缓存的唯一标识值。
     * @return 缓存标识
     */
    String value();
    /**
     * 缓存日期，默认为空字符串。
     * @return 缓存日期
     */
    String date() default "";
}

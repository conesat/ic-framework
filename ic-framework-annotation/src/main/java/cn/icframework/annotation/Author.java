package cn.icframework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 标注接口开发人员，接口异常可以通过异常记录快速找到负责人。
 *
 * @author ic-framework
 * @since 2024/06/09
 */
@Retention(RetentionPolicy.RUNTIME)
@Author(value = "何志龙", date = "2020/5/8")
public @interface Author {
    /**
     * 开发人员姓名。
     * @return 姓名
     */
    String value();
    /**
     * 日期，默认为空字符串。
     * @return 日期
     */
    String date() default "";
}

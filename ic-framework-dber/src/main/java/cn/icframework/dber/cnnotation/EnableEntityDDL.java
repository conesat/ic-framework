package cn.icframework.dber.cnnotation;

import java.lang.annotation.*;

/**
 * 标注该类为启用DDL（数据定义语言）功能的注解。
 *
 * @author ic-framework
 * @since 2024/06/09
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface EnableEntityDDL {
    /**
     * 是否开启DDL脚本，默认开启。
     * 没有该注解时也默认开启，如不需要DDL，将其修改为false。
     *
     * @return 是否启用DDL
     */
    boolean enable() default true;
}

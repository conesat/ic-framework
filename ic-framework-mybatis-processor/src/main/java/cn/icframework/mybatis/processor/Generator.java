package cn.icframework.mybatis.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Generator {
    /**
     * 是否生成
     */
    boolean gen() default true;

    /**
     * 是否生成 dto 等
     */
    boolean o() default true;

    /**
     * 是否生成对应controller
     */
    boolean api() default true;

    /**
     * 是否生成service
     */
    boolean service() default true;

    /**
     * 是否生成dao
     */
    boolean dao() default true;

    /**
     * 是否生成table def
     */
    boolean def() default true;
    /**
     * 是否生成 sqlWrapper
     */
    boolean sqlWrapper() default true;

    /**
     * 是否生成 vue
     */
    boolean vue() default true;
}

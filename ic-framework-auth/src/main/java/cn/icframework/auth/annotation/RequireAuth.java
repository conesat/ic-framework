package cn.icframework.auth.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类上使用这个注解，代表整个接口都要进行权限验证
 * 接口上添加只限制这个接口的权限
 * 必须鉴权才能访问
 * @author hzl
 * @since 2021-07-09  10:17:00
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Inherited
public @interface RequireAuth {
    /**
     * 允许的用户类型
     * 这个由业务控制，指定这类用户才有权限访问
     * 在拥有这个的基础上，还需要判断权限
     */
    String userType() default "";

    /**
     * 限制角色访问，这里填写角色Role的sign
     */
    String[] role() default {};

    /**
     * 混合橘色与权限判断，只要具备权限或者具备角色都可以访问，
     * 为false时需要同时具备，对于的角色与对于的接口权限
     */
    boolean mixRP() default true;

    /**
     * 是否仅判断token有效性
     * 只要token有效就放行，不管是否有对应权限
     */
    boolean onlyToken() default false;
}

package cn.icframework.mybatis.annotation;


import java.lang.annotation.*;

/**
 * 版本注解，用于标识实体类中的版本字段。
 * 该注解通常用于乐观锁实现，标识版本号字段。
 * 在更新操作时，MyBatis 会自动处理版本号的递增和校验。
 * 使用了@Version 注解的字段，可以不用@TableField 注解。
 * PS: 只能用于long类型的字段。
 * @since 1.0
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Version {
}
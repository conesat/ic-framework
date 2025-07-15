package cn.icframework.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库表
 * @author hzl
 * @since 2023/5/18
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {
    /**
     * 表名称
     */
    String value();
    /**
     * 数据库的 schema
     */
    String schema() default "";
    /**
     * 默认为 驼峰属性 转换为 下划线字段
     */
    boolean camelToUnderline() default true;
    /**
     * 默认使用哪个数据源，若系统找不到该指定的数据源时，默认使用第一个数据源
     */
    String dataSource() default "";
    /**
     * 注释
     */
    String comment() default "";

    /**
     * 开启自动建表默认true
     */
    boolean autoDDL() default true;

    /**
     * 表索引
     */
    Index[] indexes() default {};
    /**
     * 表索引
     */
    ForeignKey[] foreignKeys() default {};
}

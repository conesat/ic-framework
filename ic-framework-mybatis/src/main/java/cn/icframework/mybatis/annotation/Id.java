package cn.icframework.mybatis.annotation;


import cn.icframework.mybatis.consts.IdType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Id {

    /**
     * ID 生成策略，默认为 Input 外部控制输入
     *
     * @return 生成策略
     */
    IdType idType() default IdType.INPUT;
}
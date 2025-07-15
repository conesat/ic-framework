package cn.icframework.core.utils;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Spring上下文工具类
 *
 * 提供静态方式获取Spring容器中的Bean。
 *
 * @author hzl
 * @since 2024/8/23
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
    @Getter
    private static ApplicationContext applicationContext;

    /**
     * 设置Spring应用上下文
     * @param applicationContext Spring应用上下文
     * @throws BeansException 异常
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 通过Bean名称获取Bean实例
     * @param name Bean名称
     * @return Bean实例
     * @throws BeansException 异常
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    /**
     * 通过类型获取Bean实例
     * @param cls Bean类型
     * @return Bean实例
     * @throws BeansException 异常
     */
    public static <T> T getBeanByClass(Class<T> cls) throws BeansException {
        return applicationContext.getBean(cls);
    }


}
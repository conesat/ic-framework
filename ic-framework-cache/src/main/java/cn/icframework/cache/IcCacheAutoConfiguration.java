package cn.icframework.cache;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * IC Framework Cache 自动配置类。
 * 引入本模块依赖时自动扫描并注册缓存相关组件。
 */
@Configuration
@ComponentScan(basePackages = "cn.icframework.cache")
public class IcCacheAutoConfiguration {
}
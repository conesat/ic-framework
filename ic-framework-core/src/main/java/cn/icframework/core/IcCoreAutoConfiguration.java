package cn.icframework.core;

import cn.icframework.core.common.config.IcLogConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * IC Framework Core 自动配置类。
 * <p>
 * 引入 ic-framework-core 依赖时自动扫描并注册核心组件。
 * </p>
 * @author hzl
 * @since 2024/12/19
 */
@Configuration
@EnableConfigurationProperties(IcLogConfig.class)
@ComponentScan(basePackages = "cn.icframework.core")
public class IcCoreAutoConfiguration {
}
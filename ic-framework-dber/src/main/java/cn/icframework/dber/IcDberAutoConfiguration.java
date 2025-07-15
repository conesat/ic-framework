package cn.icframework.dber;

import cn.icframework.dber.config.IcSqlConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * IC Framework Dber 自动配置类
 * 
 * @author hzl
 * @since 2025/7/5
 */
@Configuration
@ComponentScan(basePackages = "cn.icframework.dber")
@EnableConfigurationProperties(IcSqlConfig.class)
public class IcDberAutoConfiguration {
}

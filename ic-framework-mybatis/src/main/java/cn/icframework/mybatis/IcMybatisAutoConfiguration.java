package cn.icframework.mybatis;

import cn.icframework.mybatis.config.SnowflakeConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * IC MyBatis 自动配置类。
 * 提供MyBatis相关的自动配置功能。
 * @author hzl
 */
@Configuration
@ComponentScan(basePackages = "cn.icframework.mybatis")
@EnableConfigurationProperties(SnowflakeConfig.class)
public class IcMybatisAutoConfiguration {
}

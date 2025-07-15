package cn.icframework.springboot;

import cn.icframework.auth.IcAuthAutoConfiguration;
import cn.icframework.cache.IcCacheAutoConfiguration;
import cn.icframework.core.common.config.IcLogConfig;
import cn.icframework.core.IcCoreAutoConfiguration;
import cn.icframework.dber.IcDberAutoConfiguration;
import cn.icframework.mybatis.IcMybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * IC Framework Spring Boot Starter 主自动配置类
 * <p>
 * 整合了以下模块的自动配置：
 * - ic-framework-auth: 认证授权模块
 * - ic-framework-common: 通用工具模块
 * - ic-framework-core: 核心功能模块
 *
 * @author hzl
 * @since 2024/12/19
 */
@Configuration
@ConditionalOnProperty(name = "ic.framework.enabled", havingValue = "true", matchIfMissing = true)
@Import({
        IcAuthAutoConfiguration.class,
        IcCacheAutoConfiguration.class,
        IcCoreAutoConfiguration.class,
        IcMybatisAutoConfiguration.class,
        IcDberAutoConfiguration.class
})
public class IcFrameworkAutoConfiguration {


    /**
     * 确保IcConfig被正确初始化
     */
    @Bean
    @ConditionalOnMissingBean
    public IcLogConfig icConfig() {
        return new IcLogConfig();
    }


} 
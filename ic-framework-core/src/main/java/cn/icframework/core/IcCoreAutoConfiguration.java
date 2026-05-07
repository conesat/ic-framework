package cn.icframework.core;

import cn.icframework.core.common.config.CoreBaseAutoConfiguration;
import cn.icframework.core.common.config.CoreWebAutoConfiguration;
import cn.icframework.core.common.config.GlobalConfig;
import cn.icframework.core.common.config.IcLogConfig;
import cn.icframework.core.common.config.LocalDateTimeSerializerConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
@Import({
        CoreBaseAutoConfiguration.class,
        GlobalConfig.class,
        LocalDateTimeSerializerConfig.class,
        CoreWebAutoConfiguration.class
})
public class IcCoreAutoConfiguration {
}

package cn.icframework.core.common.config;

import cn.icframework.core.IcFrameworkStarter;
import cn.icframework.core.utils.SpringContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreBaseAutoConfiguration {

    @Bean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }

    @Bean
    public IcFrameworkStarter icFrameworkStarter() {
        return new IcFrameworkStarter();
    }

    @Bean
    public EnumConvertFactory enumConvertFactory() {
        return new EnumConvertFactory();
    }

    @Bean
    public PropertySourcesPlaceholderConfig propertySourcesPlaceholderConfig() {
        return new PropertySourcesPlaceholderConfig();
    }
}

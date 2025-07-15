package cn.icframework.core.common.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * YML 配置文件加载配置类。
 * <p>
 * 用于加载 application-dev.yml 等自定义 YML 配置文件到 Spring 环境。
 * </p>
 */
@Component
public class PropertySourcesPlaceholderConfig {
    /**
     * 加载YML格式自定义配置文件。
     *
     * @return PropertySourcesPlaceholderConfigurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        ClassPathResource classPathResource = new ClassPathResource("application-dev.yml");
        if(classPathResource.exists()){
            yaml.setResources(classPathResource);//File引入
        }
        configurer.setProperties(Objects.requireNonNull(yaml.getObject()));
        return configurer;
    }
}

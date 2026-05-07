package cn.icframework.core.common.config;

import cn.icframework.core.common.exception.GlobalExceptionHandler;
import cn.icframework.core.common.helper.ResponseResultHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CoreWebAutoConfiguration {

    @Bean
    public CoreWebConfig coreWebConfig() {
        return new CoreWebConfig();
    }

    @Bean
    public ResponseResultHandler responseResultHandler() {
        return new ResponseResultHandler();
    }

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}

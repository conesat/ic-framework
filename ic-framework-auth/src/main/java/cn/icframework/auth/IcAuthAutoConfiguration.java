package cn.icframework.auth;

import cn.icframework.auth.config.IcJwtConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * IC Framework Auth 自动配置类
 * <p>
 * 当项目中引入ic-framework-auth依赖时，会自动扫描并注册以下组件：
 * - 所有带有@Component、@Service、@Repository、@Configuration注解的类
 * - 认证相关组件（JWTUtils、AuthPermissionInitRunner）
 * - Web配置（AuthWebConfig）
 * - 后置处理器（IcBeanPostProcessor）
 *
 * @author hzl
 * @since 2024/12/19
 */
@Configuration
@ComponentScan(basePackages = "cn.icframework.auth")
@EnableConfigurationProperties(IcJwtConfig.class)
public class IcAuthAutoConfiguration {
} 
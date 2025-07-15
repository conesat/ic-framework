package cn.icframework.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 获取JWT配置，若未设置则返回默认配置。
 * @since 2023/6/17
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ic.jwt")
public class IcJwtConfig {
    /**
     * JWT密钥
     */
    private String secret = "ic-framework-default-secret-key";
    /**
     * JWT过期时间
     */
    private int timeout = 7200;
    /**
     * 不需要过滤的url
     */
    private String[] noFilterUrls = new String[0];
}

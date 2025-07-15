package cn.icframework.core.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ic基础配置
 * @since 2023/6/17
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ic.log")
public class IcLogConfig {
    /**
     * 是否打印异常堆栈，默认true。
     */
    private Boolean printStackTrace = true;
    /**
     * 是否打印错误日志，默认false。
     */
    private Boolean printError = false;
}

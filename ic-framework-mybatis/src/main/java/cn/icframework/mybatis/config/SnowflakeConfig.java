package cn.icframework.mybatis.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ic雪花算法配置
 *
 * @since 2023/6/17
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ic.snowflake")
public class SnowflakeConfig {
    /**
     * 数据中心ID
     */
    private int dataCenterId = 1;
    /**
     * 工作节点ID
     */
    private int workerId = 1;
}

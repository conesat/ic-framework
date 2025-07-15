package cn.icframework.dber.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * sql脚本配置
 *
 * @since 2023/6/17
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ic.sql")
public class IcSqlConfig {
    /**
     * sql升级脚本路径，默认：classpath:/sqls 下所有sql文件
     */
    private String updatePath = "classpath:/sqls";
    /**
     * sql初始化脚本路径，默认：classpath*:/sqls/init.sql
     */
    private String initPath = "classpath*:/sqls/init.sql";
}

package cn.icframework.monitor;

import cn.icframework.monitor.config.IcMonitorProperties;
import cn.icframework.monitor.service.IcMonitorCollector;
import cn.icframework.monitor.service.IcMonitorService;
import cn.icframework.monitor.service.IcMonitorStorage;
import cn.icframework.monitor.service.NetworkStatsReader;
import cn.icframework.monitor.web.IcMonitorController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IcMonitorProperties.class)
@ConditionalOnProperty(prefix = "ic.monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
public class IcMonitorAutoConfiguration {

    @Bean
    public NetworkStatsReader networkStatsReader() {
        return new NetworkStatsReader();
    }

    @Bean
    public IcMonitorStorage icMonitorStorage(IcMonitorProperties properties) {
        return new IcMonitorStorage(properties);
    }

    @Bean
    public IcMonitorService icMonitorService(IcMonitorStorage storage, IcMonitorProperties properties) {
        return new IcMonitorService(storage, properties);
    }

    @Bean
    public IcMonitorCollector icMonitorCollector(IcMonitorStorage storage,
                                                 IcMonitorProperties properties,
                                                 NetworkStatsReader networkStatsReader) {
        return new IcMonitorCollector(storage, properties, networkStatsReader);
    }

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public IcMonitorController icMonitorController(IcMonitorService monitorService, IcMonitorProperties properties) {
        return new IcMonitorController(monitorService, properties);
    }
}

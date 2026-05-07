package cn.icframework.monitor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "ic.monitor")
public class IcMonitorProperties {
    private boolean enabled = true;
    private String basePath = "/ic/monitor";
    private int sampleIntervalSeconds = 5;
    private int retentionMinutes = 60;
    private boolean diskDetailEnabled = true;
    private List<String> diskPaths = new ArrayList<>();

    public long getSampleIntervalMillis() {
        return Math.max(sampleIntervalSeconds, 1) * 1000L;
    }

    public int getMaxSamples() {
        int retentionSeconds = Math.max(retentionMinutes, 1) * 60;
        return Math.max((retentionSeconds / Math.max(sampleIntervalSeconds, 1)) + 4, 16);
    }
}

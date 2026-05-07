package cn.icframework.monitor.service;

import cn.icframework.monitor.config.IcMonitorProperties;
import cn.icframework.monitor.model.MonitorTimelineResponse;
import org.springframework.util.Assert;

public class IcMonitorService {
    private final IcMonitorStorage storage;
    private final IcMonitorProperties properties;

    public IcMonitorService(IcMonitorStorage storage, IcMonitorProperties properties) {
        this.storage = storage;
        this.properties = properties;
    }

    public MonitorTimelineResponse timeline(int minutes) {
        Assert.isTrue(minutes > 0, "minutes must be greater than 0");
        int safeMinutes = Math.min(minutes, properties.getRetentionMinutes());
        long now = System.currentTimeMillis();
        long minTimestamp = now - safeMinutes * 60_000L;
        return MonitorTimelineResponse.builder()
                .generatedAt(now)
                .minutes(safeMinutes)
                .retentionMinutes(properties.getRetentionMinutes())
                .sampleIntervalSeconds(properties.getSampleIntervalSeconds())
                .latest(storage.latest())
                .samples(storage.range(minTimestamp))
                .build();
    }
}

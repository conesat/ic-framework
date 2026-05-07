package cn.icframework.monitor.service;

import cn.icframework.monitor.config.IcMonitorProperties;
import cn.icframework.monitor.model.MonitorSnapshot;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class IcMonitorStorage {
    private final Deque<MonitorSnapshot> snapshots = new ArrayDeque<>();
    private final int maxSamples;

    public IcMonitorStorage(IcMonitorProperties properties) {
        this.maxSamples = properties.getMaxSamples();
    }

    public synchronized void append(MonitorSnapshot snapshot) {
        snapshots.addLast(snapshot);
        while (snapshots.size() > maxSamples) {
            snapshots.removeFirst();
        }
    }

    public synchronized MonitorSnapshot latest() {
        return snapshots.peekLast();
    }

    public synchronized List<MonitorSnapshot> range(long minTimestamp) {
        List<MonitorSnapshot> result = new ArrayList<>();
        for (MonitorSnapshot snapshot : snapshots) {
            if (snapshot.getTimestamp() >= minTimestamp) {
                result.add(snapshot);
            }
        }
        return result;
    }
}

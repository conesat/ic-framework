package cn.icframework.monitor.service;

import cn.icframework.monitor.config.IcMonitorProperties;
import cn.icframework.monitor.model.CpuMetrics;
import cn.icframework.monitor.model.DiskMetrics;
import cn.icframework.monitor.model.DiskPathMetrics;
import cn.icframework.monitor.model.JvmMemoryMetrics;
import cn.icframework.monitor.model.JvmMetrics;
import cn.icframework.monitor.model.MonitorSnapshot;
import cn.icframework.monitor.model.NetworkMetrics;
import cn.icframework.monitor.model.SystemMemoryMetrics;
import com.sun.management.OperatingSystemMXBean;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class IcMonitorCollector {
    private final IcMonitorStorage storage;
    private final IcMonitorProperties properties;
    private final NetworkStatsReader networkStatsReader;
    private final OperatingSystemMXBean osBean;
    private final MemoryMXBean memoryMXBean;
    private final ThreadMXBean threadMXBean;
    private final ClassLoadingMXBean classLoadingMXBean;
    private final RuntimeMXBean runtimeMXBean;
    private volatile NetworkSample previousNetworkSample;

    public IcMonitorCollector(IcMonitorStorage storage,
                              IcMonitorProperties properties,
                              NetworkStatsReader networkStatsReader) {
        this.storage = storage;
        this.properties = properties;
        this.networkStatsReader = networkStatsReader;
        this.osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        this.threadMXBean = ManagementFactory.getThreadMXBean();
        this.classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        this.runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    }

    @PostConstruct
    public void initialize() {
        collect();
    }

    @Scheduled(
            fixedDelayString = "#{${ic.monitor.sample-interval-seconds:5} * 1000}",
            initialDelayString = "#{${ic.monitor.sample-interval-seconds:5} * 1000}"
    )
    public void collect() {
        try {
            storage.append(capture());
        } catch (Exception e) {
            log.warn("collect monitor snapshot failed: {}", e.getMessage());
        }
    }

    private MonitorSnapshot capture() {
        long now = System.currentTimeMillis();
        return MonitorSnapshot.builder()
                .timestamp(now)
                .cpu(captureCpu())
                .systemMemory(captureSystemMemory())
                .jvmMemory(captureJvmMemory())
                .disk(captureDisk())
                .network(captureNetwork(now))
                .jvm(captureJvm())
                .build();
    }

    private CpuMetrics captureCpu() {
        return CpuMetrics.builder()
                .systemUsage(normalizeRate(osBean.getCpuLoad()))
                .processUsage(normalizeRate(osBean.getProcessCpuLoad()))
                .systemLoadAverage(nonNegative(osBean.getSystemLoadAverage()))
                .availableProcessors(osBean.getAvailableProcessors())
                .build();
    }

    private SystemMemoryMetrics captureSystemMemory() {
        long total = Math.max(osBean.getTotalMemorySize(), 0L);
        long free = Math.max(osBean.getFreeMemorySize(), 0L);
        long used = Math.max(total - free, 0L);
        return SystemMemoryMetrics.builder()
                .totalBytes(total)
                .freeBytes(free)
                .usedBytes(used)
                .usage(ratio(used, total))
                .build();
    }

    private JvmMemoryMetrics captureJvmMemory() {
        MemoryUsage heap = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memoryMXBean.getNonHeapMemoryUsage();
        return JvmMemoryMetrics.builder()
                .heapUsedBytes(heap.getUsed())
                .heapCommittedBytes(heap.getCommitted())
                .heapMaxBytes(heap.getMax())
                .heapUsage(ratio(heap.getUsed(), heap.getMax() > 0 ? heap.getMax() : heap.getCommitted()))
                .nonHeapUsedBytes(nonHeap.getUsed())
                .nonHeapCommittedBytes(nonHeap.getCommitted())
                .nonHeapMaxBytes(nonHeap.getMax())
                .build();
    }

    private DiskMetrics captureDisk() {
        List<DiskPathMetrics> items = new ArrayList<>();
        long total = 0L;
        long free = 0L;
        long usable = 0L;
        for (String path : resolveDiskPaths()) {
            File file = new File(path);
            long pathTotal = Math.max(file.getTotalSpace(), 0L);
            long pathFree = Math.max(file.getFreeSpace(), 0L);
            long pathUsable = Math.max(file.getUsableSpace(), 0L);
            long pathUsed = Math.max(pathTotal - pathFree, 0L);
            total += pathTotal;
            free += pathFree;
            usable += pathUsable;
            if (properties.isDiskDetailEnabled()) {
                items.add(DiskPathMetrics.builder()
                        .path(file.getAbsolutePath())
                        .totalBytes(pathTotal)
                        .freeBytes(pathFree)
                        .usableBytes(pathUsable)
                        .usedBytes(pathUsed)
                        .usage(ratio(pathUsed, pathTotal))
                        .build());
            }
        }
        long used = Math.max(total - free, 0L);
        return DiskMetrics.builder()
                .totalBytes(total)
                .freeBytes(free)
                .usableBytes(usable)
                .usedBytes(used)
                .usage(ratio(used, total))
                .items(items)
                .build();
    }

    private NetworkMetrics captureNetwork(long now) {
        NetworkStatsReader.NetworkCounters counters = networkStatsReader.read();
        double rxPerSecond = 0D;
        double txPerSecond = 0D;
        if (counters.isAvailable()) {
            NetworkSample previous = previousNetworkSample;
            if (previous != null && now > previous.timestamp) {
                double seconds = (now - previous.timestamp) / 1000D;
                rxPerSecond = Math.max((counters.getRxBytes() - previous.rxBytes) / seconds, 0D);
                txPerSecond = Math.max((counters.getTxBytes() - previous.txBytes) / seconds, 0D);
            }
            previousNetworkSample = new NetworkSample(now, counters.getRxBytes(), counters.getTxBytes());
        }
        return NetworkMetrics.builder()
                .available(counters.isAvailable())
                .rxBytes(counters.getRxBytes())
                .txBytes(counters.getTxBytes())
                .rxBytesPerSecond(rxPerSecond)
                .txBytesPerSecond(txPerSecond)
                .source(counters.getSource())
                .build();
    }

    private JvmMetrics captureJvm() {
        long gcCount = 0L;
        long gcTimeMillis = 0L;
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            long count = gcBean.getCollectionCount();
            long time = gcBean.getCollectionTime();
            if (count > 0) {
                gcCount += count;
            }
            if (time > 0) {
                gcTimeMillis += time;
            }
        }
        return JvmMetrics.builder()
                .threadCount(threadMXBean.getThreadCount())
                .daemonThreadCount(threadMXBean.getDaemonThreadCount())
                .peakThreadCount(threadMXBean.getPeakThreadCount())
                .loadedClassCount(classLoadingMXBean.getLoadedClassCount())
                .totalLoadedClassCount(classLoadingMXBean.getTotalLoadedClassCount())
                .unloadedClassCount(classLoadingMXBean.getUnloadedClassCount())
                .gcCount(gcCount)
                .gcTimeMillis(gcTimeMillis)
                .uptimeMillis(runtimeMXBean.getUptime())
                .build();
    }

    private Set<String> resolveDiskPaths() {
        Set<String> paths = new LinkedHashSet<>();
        if (properties.getDiskPaths() != null && !properties.getDiskPaths().isEmpty()) {
            paths.addAll(properties.getDiskPaths());
        } else {
            File[] roots = File.listRoots();
            if (roots != null) {
                for (File root : roots) {
                    paths.add(root.getAbsolutePath());
                }
            }
            paths.add(new File(".").getAbsolutePath());
        }
        return paths;
    }

    private double ratio(long numerator, long denominator) {
        if (denominator <= 0) {
            return 0D;
        }
        return numerator * 100D / denominator;
    }

    private double normalizeRate(double value) {
        if (Double.isNaN(value) || value < 0) {
            return 0D;
        }
        return Math.min(value * 100D, 100D);
    }

    private double nonNegative(double value) {
        if (Double.isNaN(value) || value < 0) {
            return 0D;
        }
        return value;
    }

    private record NetworkSample(long timestamp, long rxBytes, long txBytes) {
    }
}

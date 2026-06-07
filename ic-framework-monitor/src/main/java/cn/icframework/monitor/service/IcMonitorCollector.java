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

/**
 * 系统监控指标采集器
 * <p>
 * 定期采集系统的各项监控指标，包括：
 * <ul>
 *   <li>CPU使用率（系统级别和进程级别）</li>
 *   <li>系统内存使用情况</li>
 *   <li>JVM内存使用情况（堆内存和非堆内存）</li>
 *   <li>磁盘空间使用情况</li>
 *   <li>网络流量统计</li>
 *   <li>JVM运行时信息（线程、类加载、GC等）</li>
 * </ul>
 * </p>
 * <p>
 * 采集的数据会通过 {@link IcMonitorStorage} 进行持久化存储，
 * 采集频率由配置项 {@code ic.monitor.sample-interval-seconds} 控制，默认为5秒。
 * </p>
 *
 * @author ic-framework
 * @see IcMonitorStorage
 * @see IcMonitorProperties
 */
@Slf4j
public class IcMonitorCollector {
    /** 监控数据存储组件 */
    private final IcMonitorStorage storage;
    
    /** 监控配置属性 */
    private final IcMonitorProperties properties;
    
    /** 网络统计数据读取器 */
    private final NetworkStatsReader networkStatsReader;
    
    /** 操作系统MXBean，用于获取系统级别的监控数据 */
    private final OperatingSystemMXBean osBean;
    
    /** JVM内存MXBean，用于获取JVM内存使用情况 */
    private final MemoryMXBean memoryMXBean;
    
    /** JVM线程MXBean，用于获取线程相关信息 */
    private final ThreadMXBean threadMXBean;
    
    /** JVM类加载MXBean，用于获取类加载相关信息 */
    private final ClassLoadingMXBean classLoadingMXBean;
    
    /** JVM运行时MXBean，用于获取JVM运行时信息 */
    private final RuntimeMXBean runtimeMXBean;
    
    /** 上一次网络采样数据，用于计算网络流量速率 */
    private volatile NetworkSample previousNetworkSample;

    /**
     * 构造监控指标采集器
     *
     * @param storage           监控数据存储组件
     * @param properties        监控配置属性
     * @param networkStatsReader 网络统计数据读取器
     */
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

    /**
     * 初始化方法，在Bean创建后立即执行首次采集
     */
    @PostConstruct
    public void initialize() {
        collect();
    }

    /**
     * 定时采集监控指标
     * <p>
     * 按照配置的采样间隔定期执行，默认每5秒采集一次。
     * 采集失败时会记录警告日志，但不会影响后续采集任务。
     * </p>
     */
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

    /**
     * 捕获当前时刻的完整监控快照
     *
     * @return 包含所有监控指标的快照对象
     */
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

    /**
     * 采集CPU使用率指标
     * <p>
     * 包括系统整体CPU使用率和当前进程的CPU使用率。
     * 使用率值已转换为百分比形式（0-100）。
     * </p>
     *
     * @return CPU监控指标
     */
    private CpuMetrics captureCpu() {
        return CpuMetrics.builder()
                .systemUsage(normalizeRate(osBean.getCpuLoad()))
                .processUsage(normalizeRate(osBean.getProcessCpuLoad()))
                .systemLoadAverage(nonNegative(osBean.getSystemLoadAverage()))
                .availableProcessors(osBean.getAvailableProcessors())
                .build();
    }

    /**
     * 采集系统内存使用情况
     * <p>
     * 获取操作系统的总内存、已用内存、空闲内存及使用率。
     * 所有内存值均以字节为单位。
     * </p>
     *
     * @return 系统内存监控指标
     */
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

    /**
     * 采集JVM内存使用情况
     * <p>
     * 包括堆内存和非堆内存的使用情况：
     * <ul>
     *   <li>堆内存：已用、已提交、最大值及使用率</li>
     *   <li>非堆内存：已用、已提交、最大值</li>
     * </ul>
     * </p>
     *
     * @return JVM内存监控指标
     */
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

    /**
     * 采集磁盘空间使用情况
     * <p>
     * 根据配置的路径列表或系统根目录，统计各磁盘分区的空间使用情况。
     * 如果启用了磁盘详情模式，还会记录每个路径的详细信息。
     * </p>
     *
     * @return 磁盘监控指标
     */
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

    /**
     * 采集网络流量指标
     * <p>
     * 通过对比前后两次采样的网络字节数，计算出每秒的接收和发送速率。
     * 如果是首次采样或数据不可用，则速率为0。
     * </p>
     *
     * @param now 当前时间戳（毫秒）
     * @return 网络监控指标
     */
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

    /**
     * 采集JVM运行时指标
     * <p>
     * 包括：
     * <ul>
     *   <li>线程信息：当前线程数、守护线程数、峰值线程数</li>
     *   <li>类加载信息：已加载类数、总加载类数、卸载类数</li>
     *   <li>GC信息：GC次数、GC耗时</li>
     *   <li>JVM运行时长</li>
     * </ul>
     * </p>
     *
     * @return JVM运行时监控指标
     */
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

    /**
     * 解析需要监控的磁盘路径
     * <p>
     * 优先使用配置文件中指定的路径列表，如果未配置则使用：
     * <ul>
     *   <li>系统所有根目录（Windows下的盘符，Linux下的/）</li>
     *   <li>当前工作目录</li>
     * </ul>
     * </p>
     *
     * @return 需要监控的磁盘路径集合
     */
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

    /**
     * 计算百分比
     *
     * @param numerator   分子
     * @param denominator 分母
     * @return 百分比值（0-100），如果分母小于等于0则返回0
     */
    private double ratio(long numerator, long denominator) {
        if (denominator <= 0) {
            return 0D;
        }
        return numerator * 100D / denominator;
    }

    /**
     * 标准化比率值为百分比形式
     * <p>
     * 将0-1范围的比率值转换为0-100的百分比，并处理无效值（NaN或负数）。
     * </p>
     *
     * @param value 原始比率值（0-1范围）
     * @return 百分比值（0-100）
     */
    private double normalizeRate(double value) {
        if (Double.isNaN(value) || value < 0) {
            return 0D;
        }
        return Math.min(value * 100D, 100D);
    }

    /**
     * 确保数值为非负数
     * <p>
     * 如果值为NaN或负数，则返回0；否则返回原值。
     * </p>
     *
     * @param value 待检查的数值
     * @return 非负数值
     */
    private double nonNegative(double value) {
        if (Double.isNaN(value) || value < 0) {
            return 0D;
        }
        return value;
    }

    /**
     * 网络采样数据记录
     * <p>
     * 用于存储某一时刻的网络累计字节数，以便计算流量速率。
     * </p>
     *
     * @param timestamp 采样时间戳（毫秒）
     * @param rxBytes   累计接收字节数
     * @param txBytes   累计发送字节数
     */
    private record NetworkSample(long timestamp, long rxBytes, long txBytes) {
    }
}

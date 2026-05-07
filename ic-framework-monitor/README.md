# IC Framework Monitor

`ic-framework-monitor` 为 `ic-framework` 提供轻量级资源监控能力，默认采集以下指标：

- 系统 CPU 使用率、进程 CPU 使用率、系统负载
- 系统内存总量、可用量、使用率
- JVM 堆内存、非堆内存、线程数、类加载、GC 次数与耗时
- 磁盘总量、可用量、使用率，并支持按路径输出明细
- 网络总入站/出站字节，以及每秒吞吐速率

## 默认能力

- 采样周期：`5` 秒
- 默认保留：最近 `60` 分钟
- 接口前缀：`/ic/monitor`

## 配置示例

```yaml
ic:
  monitor:
    enabled: true
    base-path: /ic/monitor
    sample-interval-seconds: 5
    retention-minutes: 60
    disk-detail-enabled: true
    disk-paths:
      - /
      - /data
```

## 接口

- `GET /ic/monitor/summary`
- `GET /ic/monitor/timeline?minutes=15`
- `GET /ic/monitor/meta`

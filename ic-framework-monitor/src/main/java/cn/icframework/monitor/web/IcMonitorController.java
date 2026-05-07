package cn.icframework.monitor.web;

import cn.icframework.monitor.config.IcMonitorProperties;
import cn.icframework.monitor.model.MonitorTimelineResponse;
import cn.icframework.monitor.service.IcMonitorService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("${ic.monitor.base-path:/ic/monitor}")
public class IcMonitorController {
    private final IcMonitorService monitorService;
    private final IcMonitorProperties properties;

    public IcMonitorController(IcMonitorService monitorService, IcMonitorProperties properties) {
        this.monitorService = monitorService;
        this.properties = properties;
    }

    @GetMapping("/summary")
    public MonitorTimelineResponse summary() {
        return monitorService.timeline(Math.min(5, properties.getRetentionMinutes()));
    }

    @GetMapping("/timeline")
    public MonitorTimelineResponse timeline(@RequestParam(defaultValue = "15") @Min(1) @Max(1440) int minutes) {
        return monitorService.timeline(minutes);
    }

    @GetMapping("/meta")
    public IcMonitorProperties meta() {
        return properties;
    }
}

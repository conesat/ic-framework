package cn.icframework.monitor.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class NetworkStatsReader {
    private static final Path LINUX_NET_DEV = Path.of("/proc/net/dev");

    public NetworkCounters read() {
        if (Files.exists(LINUX_NET_DEV)) {
            return readLinuxCounters();
        }
        return new NetworkCounters(false, 0L, 0L, "unsupported");
    }

    private NetworkCounters readLinuxCounters() {
        try {
            List<String> lines = Files.readAllLines(LINUX_NET_DEV);
            long rxBytes = 0L;
            long txBytes = 0L;
            for (String line : lines) {
                String trimmed = line.trim();
                if (!trimmed.contains(":")) {
                    continue;
                }
                String[] pair = trimmed.split(":", 2);
                String name = pair[0].trim();
                if ("lo".equals(name)) {
                    continue;
                }
                String[] values = pair[1].trim().split("\\s+");
                if (values.length < 16) {
                    continue;
                }
                rxBytes += parseLong(values[0]);
                txBytes += parseLong(values[8]);
            }
            return new NetworkCounters(true, rxBytes, txBytes, "proc-net-dev");
        } catch (IOException ignored) {
            return new NetworkCounters(false, 0L, 0L, "unavailable");
        }
    }

    private long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            return 0L;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class NetworkCounters {
        private final boolean available;
        private final long rxBytes;
        private final long txBytes;
        private final String source;
    }
}

package cn.icframework.core.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * IP 工具类，提供 IP 地址相关的常用操作方法。
 * 所有方法均为静态方法。
 *
 * @author hzl
 * @since 2024/9/11
 */
@Slf4j
public class IpUtils {
    private static final Searcher searcher;


    static {
        try {
            InputStream ris = WebUtils.class.getResourceAsStream("/ip2region.xdb");
            byte[] dbBinStr = FileCopyUtils.copyToByteArray(ris);
            searcher = Searcher.newWithBuffer(dbBinStr);
        } catch (IOException e) {
            log.error("解析ip地址失败,无法创建搜索器: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static String getIpRegion(String ip) {
        if (searcher == null || !StringUtils.hasLength(ip)) {
            return null;
        }
        try {
            return searcher.search(ip);
        } catch (Exception e) {
            return null;
        }
    }


    public static String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress != null && !ipAddress.isEmpty()) {
            ipAddress = ipAddress.split(",")[0];
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}

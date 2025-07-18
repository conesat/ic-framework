package cn.icframework.dber.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * DDL结构hash本地缓存工具类
 * 用于存储和读取每个实体类的结构hash，提升启动效率
 *
 * @author icframework
 * @since 2025/07/11
 */
public class DDLHashCacheUtils {
    /**
     * 缓存文件名，存储在项目根目录下
     */
    private static final String CACHE_FILE = "ddl_hash_cache.properties";
    /**
     * 内存缓存，key为类名，value为结构hash
     */
    private static final Map<String, String> cache = new HashMap<>();

    static {
        // 启动时加载本地缓存到内存
        try (FileInputStream fis = new FileInputStream(CACHE_FILE)) {
            java.util.Properties props = new java.util.Properties();
            props.load(fis);
            for (String name : props.stringPropertyNames()) {
                cache.put(name, props.getProperty(name));
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * 获取指定类名的结构hash
     *
     * @param className 实体类全名
     * @return hash值
     */
    public static String getHash(String className) {
        return cache.get(className);
    }

    /**
     * 设置并持久化指定类名的结构hash
     *
     * @param className 实体类全名
     * @param hash      hash值
     */
    public static void setHash(String className, String hash) {
        cache.put(className, hash);
        save();
    }

    /**
     * 持久化所有缓存到本地文件
     */
    private static void save() {
        try (FileOutputStream fos = new FileOutputStream(CACHE_FILE)) {
            java.util.Properties props = new java.util.Properties();
            props.putAll(cache);
            props.store(fos, "IC DDL hash cache");
        } catch (IOException ignored) {
        }
    }
} 
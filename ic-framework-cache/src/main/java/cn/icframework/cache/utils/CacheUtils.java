package cn.icframework.cache.utils;

import cn.icframework.cache.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 缓存工具类
 * <p>
 * 提供便捷的静态方法进行缓存操作
 * 自动根据Redis配置选择使用Redis还是本地缓存
 *
 * @author hzl
 * @since 2024/12/19
 */
@Slf4j
@Component
public class CacheUtils {

    private static ICacheService cacheService;

    @Autowired
    public void setCacheService(ICacheService cacheService) {
        CacheUtils.cacheService = cacheService;
    }

    /**
     * 获取缓存值
     *
     * @param key 缓存键
     * @return 缓存值，如果不存在返回null
     */
    public static Object get(String key) {
        if (cacheService == null) {
            log.warn("CacheService not initialized");
            return null;
        }
        return cacheService.get(key);
    }

    /**
     * 获取缓存值，如果不存在则通过加载函数获取
     *
     * @param key      缓存键
     * @param loadData 加载数据的函数
     * @return 缓存值
     */
    public static Object get(String key, Function<String, Object> loadData) {
        if (cacheService == null) {
            log.warn("CacheService not initialized");
            return null;
        }
        return cacheService.get(key, loadData);
    }

    /**
     * 设置缓存值
     *
     * @param key  缓存键
     * @param data 缓存值
     */
    public static void set(String key, Object data) {
        if (cacheService == null) {
            log.warn("CacheService not initialized");
            return;
        }
        cacheService.set(key, data);
    }

    /**
     * 设置缓存值并指定过期时间
     *
     * @param key       缓存键
     * @param data      缓存值
     * @param expireTime 过期时间（秒）
     */
    public static void set(String key, Object data, long expireTime) {
        if (cacheService == null) {
            log.warn("CacheService not initialized");
            return;
        }
        cacheService.set(key, data, expireTime);
    }

    /**
     * 设置缓存值并指定过期时间和时间单位
     *
     * @param key       缓存键
     * @param data      缓存值
     * @param expireTime 过期时间
     * @param unit      时间单位
     */
    public static void set(String key, Object data, long expireTime, TimeUnit unit) {
        if (cacheService == null) {
            log.warn("CacheService not initialized");
            return;
        }
        cacheService.set(key, data, expireTime, unit);
    }

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    public static void remove(String key) {
        if (cacheService == null) {
            log.warn("CacheService not initialized");
            return;
        }
        cacheService.remove(key);
    }

    /**
     * 设置缓存过期时间
     *
     * @param key       缓存键
     * @param expireTime 过期时间（秒）
     * @return 是否设置成功
     */
    public static boolean expire(String key, long expireTime) {
        if (cacheService == null) {
            log.warn("CacheService not initialized");
            return false;
        }
        return cacheService.expire(key, expireTime);
    }

    /**
     * 设置缓存过期时间
     *
     * @param key       缓存键
     * @param expireTime 过期时间
     * @param unit      时间单位
     * @return 是否设置成功
     */
    public static boolean expire(String key, long expireTime, TimeUnit unit) {
        if (cacheService == null) {
            log.warn("CacheService not initialized");
            return false;
        }
        return cacheService.expire(key, expireTime, unit);
    }

    /**
     * 检查缓存是否存在
     *
     * @param key 缓存键
     * @return 是否存在
     */
    public static boolean exists(String key) {
        if (cacheService == null) {
            log.warn("CacheService not initialized");
            return false;
        }
        return cacheService.exists(key);
    }

    /**
     * 获取缓存剩余过期时间
     *
     * @param key 缓存键
     * @return 剩余过期时间（秒），-1表示永不过期，-2表示不存在
     */
    public static long getExpire(String key) {
        if (cacheService == null) {
            log.warn("CacheService not initialized");
            return -2;
        }
        return cacheService.getExpire(key);
    }

    /**
     * 清空所有缓存
     */
    public static void clear() {
        if (cacheService == null) {
            log.warn("CacheService not initialized");
            return;
        }
        cacheService.clear();
    }

    /**
     * 检查是否使用Redis
     *
     * @return 是否使用Redis
     */
    public static boolean useRedis() {
        if (cacheService == null) {
            log.warn("CacheService not initialized");
            return false;
        }
        return cacheService.useRedis();
    }
} 
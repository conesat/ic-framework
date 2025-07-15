package cn.icframework.cache.impl;

import cn.icframework.cache.ICacheService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 本地缓存服务实现
 * <p>
 * 基于Caffeine实现的本地缓存服务
 *
 * @author hzl
 * @since 2024/12/19
 */
@Slf4j
@Service("localCacheService")
public class LocalCacheServiceImpl implements ICacheService {

    private final Cache<String, Object> cache;

    public LocalCacheServiceImpl(
            @Value("${ic.cache.local.max-size:10000}") int maxSize,
            @Value("${ic.cache.local.expire-after-write:600}") long expireAfterWrite) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireAfterWrite, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public Object get(String key) {
        try {
            return cache.getIfPresent(key);
        } catch (Exception e) {
            log.error("Local cache get operation failed for key: {}", key, e);
            return null;
        }
    }

    @Override
    public Object get(String key, java.util.function.Function<String, Object> loadData) {
        try {
            return cache.get(key, loadData);
        } catch (Exception e) {
            log.error("Local cache get operation failed for key: {}", key, e);
            return null;
        }
    }

    @Override
    public void set(String key, Object data) {
        if (data != null) {
            try {
                cache.put(key, data);
            } catch (Exception e) {
                log.error("Local cache set operation failed for key: {}", key, e);
            }
        }
    }

    @Override
    public void set(String key, Object data, long expireTime) {
        set(key, data, expireTime, TimeUnit.SECONDS);
    }

    @Override
    public void set(String key, Object data, long expireTime, TimeUnit unit) {
        if (data != null) {
            try {
                // 注意：Caffeine不支持动态过期时间，这里只是简单设置
                // 如果需要动态过期时间，需要使用Guava Cache或其他方案
                cache.put(key, data);
            } catch (Exception e) {
                log.error("Local cache set operation failed for key: {} with expire time: {}", key, expireTime, e);
            }
        }
    }

    @Override
    public void remove(String key) {
        try {
            cache.invalidate(key);
        } catch (Exception e) {
            log.error("Local cache remove operation failed for key: {}", key, e);
        }
    }

    @Override
    public boolean expire(String key, long expireTime) {
        // Caffeine不支持动态设置过期时间，这里返回false
        log.warn("Local cache does not support dynamic expiration, key: {}", key);
        return false;
    }

    @Override
    public boolean expire(String key, long expireTime, TimeUnit unit) {
        // Caffeine不支持动态设置过期时间，这里返回false
        log.warn("Local cache does not support dynamic expiration, key: {}", key);
        return false;
    }

    @Override
    public boolean exists(String key) {
        try {
            return cache.getIfPresent(key) != null;
        } catch (Exception e) {
            log.error("Local cache exists operation failed for key: {}", key, e);
            return false;
        }
    }

    @Override
    public long getExpire(String key) {
        // Caffeine不支持获取剩余过期时间，这里返回-1表示永不过期
        log.warn("Local cache does not support getting expiration time, key: {}", key);
        return -1;
    }

    @Override
    public void clear() {
        try {
            cache.invalidateAll();
        } catch (Exception e) {
            log.error("Local cache clear operation failed", e);
        }
    }

    @Override
    public boolean useRedis() {
        return false;
    }
} 
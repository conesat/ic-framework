package cn.icframework.cache.impl;

import cn.icframework.cache.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 统一缓存服务实现
 * <p>
 * 根据Redis配置自动选择使用Redis还是本地缓存
 * 当有Redis配置时默认使用Redis，当没有Redis时使用本地缓存
 *
 * @author hzl
 * @since 2024/12/19
 */
@Slf4j
@Service
@Primary
public class UnifiedCacheServiceImpl implements ICacheService {

    private final RedisCacheServiceImpl redisCacheService;
    private final LocalCacheServiceImpl localCacheService;
    private boolean hasRedis = false;

    public UnifiedCacheServiceImpl(
            @Autowired(required = false) RedisCacheServiceImpl redisCacheService,
            LocalCacheServiceImpl localCacheService,
            @Value("${spring.redis.host:}") String redisHost) {
        this.redisCacheService = redisCacheService;
        this.localCacheService = localCacheService;
        this.hasRedis = StringUtils.isNotEmpty(redisHost) && redisCacheService != null;
        
        log.info("Cache service initialized - Redis available: {}", hasRedis);
    }

    /**
     * 获取缓存数据。
     * @param key 缓存键
     * @return 缓存数据
     */
    @Override
    public Object get(String key) {
        if (hasRedis) {
            try {
                return redisCacheService.get(key);
            } catch (Exception e) {
                log.error("Redis get operation failed, falling back to local cache for key: {}", key, e);
                return localCacheService.get(key);
            }
        } else {
            return localCacheService.get(key);
        }
    }

    /**
     * 获取缓存数据，若不存在则通过loadData加载。
     * @param key 缓存键
     * @param loadData 加载数据的函数
     * @return 缓存数据
     */
    @Override
    public Object get(String key, java.util.function.Function<String, Object> loadData) {
        if (hasRedis) {
            try {
                return redisCacheService.get(key, loadData);
            } catch (Exception e) {
                log.error("Redis get operation failed, falling back to local cache for key: {}", key, e);
                return localCacheService.get(key, loadData);
            }
        } else {
            return localCacheService.get(key, loadData);
        }
    }

    /**
     * 设置缓存数据。
     * @param key 缓存键
     * @param data 缓存数据
     */
    @Override
    public void set(String key, Object data) {
        if (hasRedis) {
            try {
                redisCacheService.set(key, data);
            } catch (Exception e) {
                log.error("Redis set operation failed, falling back to local cache for key: {}", key, e);
                localCacheService.set(key, data);
            }
        } else {
            localCacheService.set(key, data);
        }
    }

    /**
     * 设置缓存数据并指定过期时间（秒）。
     * @param key 缓存键
     * @param data 缓存数据
     * @param expireTime 过期时间（秒）
     */
    @Override
    public void set(String key, Object data, long expireTime) {
        if (hasRedis) {
            try {
                redisCacheService.set(key, data, expireTime);
            } catch (Exception e) {
                log.error("Redis set operation failed, falling back to local cache for key: {}", key, e);
                localCacheService.set(key, data, expireTime);
            }
        } else {
            localCacheService.set(key, data, expireTime);
        }
    }

    /**
     * 设置缓存数据并指定过期时间和时间单位。
     * @param key 缓存键
     * @param data 缓存数据
     * @param expireTime 过期时间
     * @param unit 时间单位
     */
    @Override
    public void set(String key, Object data, long expireTime, TimeUnit unit) {
        if (hasRedis) {
            try {
                redisCacheService.set(key, data, expireTime, unit);
            } catch (Exception e) {
                log.error("Redis set operation failed, falling back to local cache for key: {}", key, e);
                localCacheService.set(key, data, expireTime, unit);
            }
        } else {
            localCacheService.set(key, data, expireTime, unit);
        }
    }

    /**
     * 移除缓存。
     * @param key 缓存键
     */
    @Override
    public void remove(String key) {
        if (hasRedis) {
            try {
                redisCacheService.remove(key);
            } catch (Exception e) {
                log.error("Redis remove operation failed, falling back to local cache for key: {}", key, e);
                localCacheService.remove(key);
            }
        } else {
            localCacheService.remove(key);
        }
    }

    /**
     * 设置缓存过期时间（秒）。
     * @param key 缓存键
     * @param expireTime 过期时间（秒）
     * @return 是否设置成功
     */
    @Override
    public boolean expire(String key, long expireTime) {
        if (hasRedis) {
            try {
                return redisCacheService.expire(key, expireTime);
            } catch (Exception e) {
                log.error("Redis expire operation failed for key: {}", key, e);
                return false;
            }
        } else {
            return localCacheService.expire(key, expireTime);
        }
    }

    /**
     * 设置缓存过期时间。
     * @param key 缓存键
     * @param expireTime 过期时间
     * @param unit 时间单位
     * @return 是否设置成功
     */
    @Override
    public boolean expire(String key, long expireTime, TimeUnit unit) {
        if (hasRedis) {
            try {
                return redisCacheService.expire(key, expireTime, unit);
            } catch (Exception e) {
                log.error("Redis expire operation failed for key: {}", key, e);
                return false;
            }
        } else {
            return localCacheService.expire(key, expireTime, unit);
        }
    }

    /**
     * 判断缓存是否存在。
     * @param key 缓存键
     * @return 是否存在
     */
    @Override
    public boolean exists(String key) {
        if (hasRedis) {
            try {
                return redisCacheService.exists(key);
            } catch (Exception e) {
                log.error("Redis exists operation failed, falling back to local cache for key: {}", key, e);
                return localCacheService.exists(key);
            }
        } else {
            return localCacheService.exists(key);
        }
    }

    /**
     * 获取缓存剩余过期时间（秒）。
     * @param key 缓存键
     * @return 剩余过期时间，-2表示不存在
     */
    @Override
    public long getExpire(String key) {
        if (hasRedis) {
            try {
                return redisCacheService.getExpire(key);
            } catch (Exception e) {
                log.error("Redis getExpire operation failed for key: {}", key, e);
                return -2;
            }
        } else {
            return localCacheService.getExpire(key);
        }
    }

    /**
     * 清空所有缓存。
     */
    @Override
    public void clear() {
        if (hasRedis) {
            try {
                redisCacheService.clear();
            } catch (Exception e) {
                log.error("Redis clear operation failed, falling back to local cache", e);
                localCacheService.clear();
            }
        } else {
            localCacheService.clear();
        }
    }

    /**
     * 是否使用Redis作为缓存。
     * @return true表示使用Redis
     */
    @Override
    public boolean useRedis() {
        return hasRedis;
    }
} 
package cn.icframework.cache.impl;

import cn.icframework.cache.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis缓存服务实现
 * <p>
 * 基于Spring Data Redis实现的缓存服务
 *
 * @author hzl
 * @since 2024/12/19
 */
@Slf4j
@Service("redisCacheService")
public class RedisCacheServiceImpl implements ICacheService {

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Object get(String key) {
        if (redisTemplate == null) {
            log.debug("RedisTemplate not available, returning null for key: {}", key);
            return null;
        }
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis get operation failed for key: {}", key, e);
            return null;
        }
    }

    @Override
    public Object get(String key, java.util.function.Function<String, Object> loadData) {
        Object value = get(key);
        if (value == null && loadData != null) {
            value = loadData.apply(key);
            if (value != null) {
                set(key, value);
            }
        }
        return value;
    }

    @Override
    public void set(String key, Object data) {
        if (redisTemplate == null) {
            log.debug("RedisTemplate not available, skipping set operation for key: {}", key);
            return;
        }
        try {
            redisTemplate.opsForValue().set(key, data);
        } catch (Exception e) {
            log.error("Redis set operation failed for key: {}", key, e);
        }
    }

    @Override
    public void set(String key, Object data, long expireTime) {
        set(key, data, expireTime, TimeUnit.SECONDS);
    }

    @Override
    public void set(String key, Object data, long expireTime, TimeUnit unit) {
        if (redisTemplate == null) {
            log.debug("RedisTemplate not available, skipping set operation for key: {}", key);
            return;
        }
        try {
            redisTemplate.opsForValue().set(key, data, expireTime, unit);
        } catch (Exception e) {
            log.error("Redis set operation failed for key: {} with expire time: {}", key, expireTime, e);
        }
    }

    @Override
    public void remove(String key) {
        if (redisTemplate == null) {
            log.debug("RedisTemplate not available, skipping remove operation for key: {}", key);
            return;
        }
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis remove operation failed for key: {}", key, e);
        }
    }

    @Override
    public boolean expire(String key, long expireTime) {
        return expire(key, expireTime, TimeUnit.SECONDS);
    }

    @Override
    public boolean expire(String key, long expireTime, TimeUnit unit) {
        if (redisTemplate == null) {
            log.debug("RedisTemplate not available, skipping expire operation for key: {}", key);
            return false;
        }
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, expireTime, unit));
        } catch (Exception e) {
            log.error("Redis expire operation failed for key: {}", key, e);
            return false;
        }
    }

    @Override
    public boolean exists(String key) {
        if (redisTemplate == null) {
            log.debug("RedisTemplate not available, returning false for key: {}", key);
            return false;
        }
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Redis exists operation failed for key: {}", key, e);
            return false;
        }
    }

    @Override
    public long getExpire(String key) {
        if (redisTemplate == null) {
            log.debug("RedisTemplate not available, returning -2 for key: {}", key);
            return -2;
        }
        try {
            Long expire = redisTemplate.getExpire(key);
            return expire != null ? expire : -2;
        } catch (Exception e) {
            log.error("Redis getExpire operation failed for key: {}", key, e);
            return -2;
        }
    }

    @Override
    public void clear() {
        if (redisTemplate == null) {
            log.debug("RedisTemplate not available, skipping clear operation");
            return;
        }
        try {
            redisTemplate.getConnectionFactory().getConnection().flushDb();
        } catch (Exception e) {
            log.error("Redis clear operation failed", e);
        }
    }

    @Override
    public boolean useRedis() {
        return redisTemplate != null;
    }
} 
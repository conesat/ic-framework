package cn.icframework.cache;

import java.util.concurrent.TimeUnit;

/**
 * IC Framework 统一缓存服务接口
 * <p>
 * 提供统一的缓存操作接口，支持Redis和本地缓存的双重实现
 * 当有Redis配置时默认使用Redis，当没有Redis时使用本地缓存
 *
 * @author hzl
 * @since 2024/12/19
 */
public interface ICacheService {

    /**
     * 获取缓存值
     *
     * @param key 缓存键
     * @return 缓存值，如果不存在返回null
     */
    Object get(String key);

    /**
     * 获取缓存值，如果不存在则通过加载函数获取
     *
     * @param key      缓存键
     * @param loadData 加载数据的函数
     * @return 缓存值
     */
    Object get(String key, java.util.function.Function<String, Object> loadData);

    /**
     * 设置缓存值
     *
     * @param key  缓存键
     * @param data 缓存值
     */
    void set(String key, Object data);

    /**
     * 设置缓存值并指定过期时间
     *
     * @param key       缓存键
     * @param data      缓存值
     * @param expireTime 过期时间
     */
    void set(String key, Object data, long expireTime);

    /**
     * 设置缓存值并指定过期时间和时间单位
     *
     * @param key       缓存键
     * @param data      缓存值
     * @param expireTime 过期时间
     * @param unit      时间单位
     */
    void set(String key, Object data, long expireTime, TimeUnit unit);

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    void remove(String key);

    /**
     * 设置缓存过期时间
     *
     * @param key       缓存键
     * @param expireTime 过期时间（秒）
     * @return 是否设置成功
     */
    boolean expire(String key, long expireTime);

    /**
     * 设置缓存过期时间
     *
     * @param key       缓存键
     * @param expireTime 过期时间
     * @param unit      时间单位
     * @return 是否设置成功
     */
    boolean expire(String key, long expireTime, TimeUnit unit);

    /**
     * 检查缓存是否存在
     *
     * @param key 缓存键
     * @return 是否存在
     */
    boolean exists(String key);

    /**
     * 获取缓存剩余过期时间
     *
     * @param key 缓存键
     * @return 剩余过期时间（秒），-1表示永不过期，-2表示不存在
     */
    long getExpire(String key);

    /**
     * 清空所有缓存
     */
    void clear();

    /**
     * 检查是否使用Redis
     *
     * @return 是否使用Redis
     */
    boolean useRedis();
}
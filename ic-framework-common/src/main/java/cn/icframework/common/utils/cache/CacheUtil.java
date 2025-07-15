package cn.icframework.common.utils.cache;

/**
 * 缓存工具类
 *
 * 提供便捷方法创建LRU和LFU缓存实例。
 */
public class CacheUtil {
    /**
     * 创建LRU缓存实例
     * @param maxSize 最大容量
     * @return LRUCache实例
     */
    public static <K, V> LRUCache<K, V> newLRUCache(int maxSize) {
        return new LRUCache<K, V>(maxSize);
    }

    /**
     * 创建LFU缓存实例
     * @param maxSize 最大容量
     * @return LFUCache实例
     */
    public static <K, V> LFUCache<K, V> newLFUCache(int maxSize) {
        return new LFUCache<>(maxSize);
    }
}
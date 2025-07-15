package cn.icframework.common.utils.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU（最近最少使用）缓存实现。
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    /**
     * 缓存最大容量
     */
    private final int maxSize;

    /**
     * 构造方法
     * @param maxSize 最大容量
     */
    public LRUCache(int maxSize) {
        super(maxSize, 0.75f, true);
        this.maxSize = maxSize;
    }

    /**
     * 判断是否移除最老的元素
     * @param eldest 最老的元素
     * @return 是否移除
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}
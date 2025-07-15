package cn.icframework.common.utils.cache;


import java.util.*;

/**
 * LFU（最不经常使用）缓存实现。
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class LFUCache<K, V> {
    /**
     * 缓存容量
     */
    private final int capacity;
    /**
     * 当前最小访问频率
     */
    private int minFreq = 0;
    /**
     * 键值映射
     */
    private final Map<K, V> valueMap = new HashMap<>();
    /**
     * 键访问频率映射
     */
    private final Map<K, Integer> freqMap = new HashMap<>();
    /**
     * 频率到键集合的映射
     */
    private final Map<Integer, LinkedHashSet<K>> freqListMap = new HashMap<>();

    /**
     * 构造方法
     * @param capacity 缓存容量
     */
    public LFUCache(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 获取缓存值，并更新访问频率。
     * @param key 键
     * @return 值，若不存在返回null
     */
    public synchronized V get(K key) {
        if (!valueMap.containsKey(key)) return null;
        int freq = freqMap.get(key);
        freqMap.put(key, freq + 1);
        freqListMap.get(freq).remove(key);
        if (freqListMap.get(freq).isEmpty()) {
            freqListMap.remove(freq);
            if (minFreq == freq) minFreq++;
        }
        freqListMap.computeIfAbsent(freq + 1, ignore -> new LinkedHashSet<>()).add(key);
        return valueMap.get(key);
    }

    /**
     * 添加或更新缓存值。
     * @param key 键
     * @param value 值
     */
    public synchronized void put(K key, V value) {
        if (capacity <= 0) return;
        if (valueMap.containsKey(key)) {
            valueMap.put(key, value);
            get(key); // 更新频率
            return;
        }
        if (valueMap.size() >= capacity) {
            K evict = freqListMap.get(minFreq).iterator().next();
            freqListMap.get(minFreq).remove(evict);
            if (freqListMap.get(minFreq).isEmpty()) freqListMap.remove(minFreq);
            valueMap.remove(evict);
            freqMap.remove(evict);
        }
        valueMap.put(key, value);
        freqMap.put(key, 1);
        freqListMap.computeIfAbsent(1, ignore -> new LinkedHashSet<>()).add(key);
        minFreq = 1;
    }
}
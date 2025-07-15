package cn.icframework.core.basic.wrapperbuilder;

import cn.icframework.common.consts.IPage;
import cn.icframework.common.consts.RequestValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * 查询参数封装类
 *
 * 用于存储和操作请求参数，支持分页。
 */
public class QueryParams {
    /**
     * 参数Map
     */
    private final Map<String, RequestValue> params = new HashMap<>();
    /**
     * 分页参数
     */
    public IPage page;

    /**
     * 获取指定key的参数值
     * @param key 参数名
     * @return 参数值
     */
    public RequestValue get(String key) {
        return params.get(key);
    }

    /**
     * 添加参数
     * @param key 参数名
     * @param value 参数值
     */
    public void put(String key, Object value) {
        if (value == null) {
            return;
        }
        params.put(key, new RequestValue(new Object[]{value}));
    }

    /**
     * 添加参数（数组）
     * @param key 参数名
     * @param value 参数值数组
     */
    public void put(String key, Object[] value) {
        if (value == null) {
            return;
        }
        params.put(key, new RequestValue(value));
    }

    /**
     * 遍历所有参数
     * @param action BiConsumer操作
     */
    public void forEach(BiConsumer<String, RequestValue> action) {
        params.forEach(action);
    }

    /**
     * 获取所有参数名
     * @return 参数名集合
     */
    public Set<String> keys() {
        return params.keySet();
    }
}

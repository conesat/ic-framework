package cn.icframework.common.consts;

import lombok.Getter;
import lombok.Setter;

/**
 * 请求参数
 *
 * @author IceFire
 * @since 2023/8/13
 */
@Getter
@Setter
public class RequestValue {
    /**
     * 参数值数组
     */
    private Object[] values;

    /**
     * 构造方法
     * @param values 参数值数组
     */
    public RequestValue(Object[] values) {
        this.values = values;
    }

    /**
     * 判断是否有值
     * @return 是否有值
     */
    public boolean hasValue() {
        return values != null && values.length > 0;
    }

    /**
     * 获取第一个参数值
     * @return 第一个参数值，若无则返回null
     */
    public Object getValue() {
        if (!hasValue()) {
            return null;
        }
        return values[0];
    }
}

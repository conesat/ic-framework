package cn.icframework.common.enums;

import cn.icframework.common.interfaces.IEnum;

/**
 * 通用状态枚举
 *
 * @since 2023/6/21
 */
public enum Status implements IEnum {
    /**
     * 可用
     */
    ENABLE(1, "可用"),
    /**
     * 禁用
     */
    DISABLE(0, "禁用");

    /**
     * 状态编码
     */
    private final int code;
    /**
     * 状态文本
     */
    private final String text;

    /**
     * 构造方法
     * @param code 编码
     * @param text 文本
     */
    Status(int code, String text) {
        this.code = code;
        this.text = text;
    }

    /**
     * 获取编码
     * @return 编码
     */
    @Override
    public int code() {
        return code;
    }

    /**
     * 获取文本
     * @return 文本
     */
    @Override
    public String text() {
        return text;
    }

    /**
     * 根据编码获取枚举实例
     * @param code 编码
     * @return Status枚举
     */
    public static Status instanceOf(Integer code) {
        if (code == null) {
            return DISABLE;
        }
        for (Status status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return DISABLE;
    }
}

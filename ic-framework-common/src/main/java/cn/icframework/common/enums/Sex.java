package cn.icframework.common.enums;

import cn.icframework.common.interfaces.IEnum;

/**
 * 性别枚举
 *
 * @since 2023/6/21
 */
public enum Sex implements IEnum {
    /**
     * 未知/保密
     */
    UNKNOWN(0, "保密"),
    /**
     * 男
     */
    MEN(1, "男"),
    /**
     * 女
     */
    WOMEN(2, "女");

    /**
     * 性别编码
     */
    private final int code;
    /**
     * 性别文本
     */
    private final String text;

    /**
     * 构造方法
     * @param code 编码
     * @param text 文本
     */
    Sex(int code, String text) {
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
     * @return Sex枚举
     */
    public static Sex instanceOf(Integer code) {
        if (code == null) {
            return UNKNOWN;
        }
        for (Sex fileType : values()) {
            if (fileType.code == code) {
                return fileType;
            }
        }
        return UNKNOWN;
    }
}

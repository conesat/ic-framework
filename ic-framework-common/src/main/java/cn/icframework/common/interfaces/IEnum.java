package cn.icframework.common.interfaces;


import java.io.Serializable;

/**
 * 枚举接口
 *
 * @since 2023/5/27
 */
public interface IEnum extends Serializable {
    /**
     * 获取枚举编码
     * @return 编码
     */
    int code();

    /**
     * 获取枚举文本
     * @return 文本
     */
    String text();

}

package cn.icframework.core.common.bean.enums;


import cn.icframework.common.interfaces.IEnum;
import cn.icframework.core.common.helper.I18N;

/**
 * 返回代码枚举
 *
 * 定义通用的API响应状态码及含义。
 *
 * @author hzl
 */
public enum ResponseEnum implements IEnum {
    /**
     * 请求成功
     */
    SUCCESS(0, "SUCCESS"),
    /**
     * 请求失败
     */
    FAIL(-1, "FAIL"),
    /**
     * 未登录，需要登录
     */
    NEED_LOGIN(4001, "NEED_LOGIN"),
    /**
     * token过期
     */
    TOKEN_OUT_TIME(4002, "TOKEN_OUT_TIME"),
    /**
     * 其他地方登录，被挤下线
     */
    OTHER_LOGIN(4003, "OTHER_LOGIN"),
    /**
     * 无权限
     */
    UNAUTHORIZED(4000, "UNAUTHORIZED"),
    /**
     * 系统未初始化
     */
    SYSTEM_UNINITIALIZED(5000, "SYSTEM_UNINITIALIZED"),
    /**
     * 未激活
     */
    SYSTEM_NOT_ACTIVE(5001, "SYSTEM_NOT_ACTIVE"),
    /**
     * 已过期
     */
    SYSTEM_OUT_DATE(5002, "SYSTEM_OUT_DATE");

    /**
     * 响应码
     */
    private final int code;
    /**
     * 响应文本
     */
    private final String text;

    /**
     * 构造方法
     * @param code 响应码
     * @param text 响应文本
     */
    ResponseEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    /**
     * 获取响应码
     * @return 响应码
     */
    @Override
    public int code() {
        return code;
    }

    /**
     * 获取响应文本（支持国际化）
     * @return 响应文本
     */
    @Override
    public String text() {
        return I18N.g(this.text, ResponseEnum.class);
    }
}

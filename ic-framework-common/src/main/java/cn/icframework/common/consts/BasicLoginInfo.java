package cn.icframework.common.consts;

import lombok.Getter;
import lombok.Setter;

/**
 * 登录信息
 *
 * @author iceFire
 */
@Getter
@Setter
public class BasicLoginInfo {
    /**
     * 访问令牌
     */
    private String token;
    /**
     * 刷新令牌
     */
    private String refreshToken;
    /**
     * 用户ID
     */
    private Object id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 姓名
     */
    private String name;
    /**
     * 用户类型
     */
    private int userType;
}

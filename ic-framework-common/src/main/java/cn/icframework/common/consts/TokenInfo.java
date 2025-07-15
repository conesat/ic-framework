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
public class TokenInfo {
    /**
     * 访问令牌
     */
    private String token;
    /**
     * 刷新令牌
     */
    private String refreshToken;
}

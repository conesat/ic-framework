package cn.icframework.auth.threadlocal;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * JWT上下文工具类，基于Spring RequestAttributes存储和获取当前请求的JWT。
 */
public class JwtContext {

    private static final String HG_JWT = "HG_JWT";

    /**
     * 设置当前请求的JWT对象。
     * @param decodedJWT 解码后的JWT
     */
    public static void set(DecodedJWT decodedJWT) {
        RequestContextHolder.currentRequestAttributes().setAttribute(HG_JWT, decodedJWT, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * 获取当前请求的JWT对象。
     * @return 解码后的JWT
     */
    public static DecodedJWT get() {
        return (DecodedJWT)RequestContextHolder.currentRequestAttributes().getAttribute(HG_JWT, RequestAttributes.SCOPE_REQUEST);
    }
}

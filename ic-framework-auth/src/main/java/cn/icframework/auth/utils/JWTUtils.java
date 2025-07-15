package cn.icframework.auth.utils;

import cn.icframework.auth.config.IcJwtConfig;
import cn.icframework.auth.entity.RP;
import cn.icframework.auth.entity.UserProps;
import cn.icframework.auth.standard.IOnlineUserService;
import cn.icframework.auth.standard.IUserRPService;
import cn.icframework.auth.threadlocal.JwtContext;
import cn.icframework.cache.utils.CacheUtils;
import cn.icframework.common.consts.TokenInfo;
import cn.icframework.core.common.exception.TokenOutTimeException;
import cn.icframework.core.utils.IpUtils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * jwt工具类
 * @author hzl
 * @since 2021-05-24  14:09:00
 */
@Component
@Slf4j
public class JWTUtils {
    private static IOnlineUserService onlineUserService;
    private static IcJwtConfig icJwtConfig;
    private static final String TOKEN_CACHE_SESSION_ID_PREFIX = "IC:TOKEN:SESSION_ID:";
    private static final String TOKEN_CACHE_RP_PREFIX = "IC:TOKEN:RP:";

    public static final String ISSUER = "IceFire";
    /**
     * 用户类型
     */
    public static final String USER_TYPE = "user_type";
    /**
     * 是否超管
     */
    public static final String SU = "su";
    /**
     * token唯一标识，用于标记当前有多少地方使用该账号
     */
    public static final String TOKEN_SESSION_ID = "token_session_id";
    /**
     * 用户名
     */
    public static final String USER_NAME = "username";

    /**
     * 是否刷新仅用于token
     */
    public static final String REFRESH_TOKEN = "refresh";

    private static IUserRPService userRPService;

    public static void recoveryUserOnlineStatus(Long sessionId) {
        if (icJwtConfig == null) {
            log.warn("IcConfig not initialized, skipping recoveryUserOnlineStatus");
            return;
        }
        CacheUtils.set(TOKEN_CACHE_SESSION_ID_PREFIX + sessionId, 1, icJwtConfig.getTimeout() * 60L);
    }

    @Autowired(required = false)
    public void setIUserRPService(IUserRPService userRPService) {
        JWTUtils.userRPService = userRPService;
    }

    @Autowired
    public void setAppConfig(IcJwtConfig icJwtConfig) {
        JWTUtils.icJwtConfig = icJwtConfig;
    }

    @Autowired(required = false)
    public void setOnlineCacheUtils(IOnlineUserService IOnlineUserService) {
        JWTUtils.onlineUserService = IOnlineUserService;
    }

    public static String getSubject() {
        return JwtContext.get().getSubject();
    }

    public static Long getUserId() {
        try {
            String subject = JwtContext.get().getSubject();
            return Long.parseLong(subject);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 是否刷新token
     *
     * @param jwt
     * @return
     */
    public static Boolean isRefreshToken(DecodedJWT jwt) {
        Boolean aBoolean = jwt.getClaim(REFRESH_TOKEN).asBoolean();
        return aBoolean != null && aBoolean;
    }

    /**
     * 获取当前用户类型
     *
     * @return
     */
    public static String getUserType() {
        return getUserType(JwtContext.get());
    }

    /**
     * 获取角色
     *
     * @param jwt
     * @return
     */
    public static String getUserType(DecodedJWT jwt) {
        if (jwt == null) {
            jwt = JwtContext.get();
        }
        if (jwt == null) {
            return null;
        }
        return jwt.getClaim(JWTUtils.USER_TYPE).asString();
    }

    /**
     * 获取角色
     *
     * @return
     */
    public static Long getTokenSessionId() {
        return getTokenSessionId(JwtContext.get());
    }

    /**
     * 获取角色
     *
     * @param jwt
     * @return
     */
    public static Long getTokenSessionId(DecodedJWT jwt) {
        if (jwt == null) {
            jwt = JwtContext.get();
        }
        if (jwt == null) {
            return null;
        }
        return Long.parseLong(jwt.getClaim(JWTUtils.TOKEN_SESSION_ID).asString());
    }

    /**
     * 是否超级管理员
     *
     * @return
     */
    public static boolean isSu() {
        return isSu(JwtContext.get());
    }

    /**
     * 是否超级管理员
     *
     * @param jwt
     * @return
     */
    public static boolean isSu(DecodedJWT jwt) {
        if (jwt == null) {
            jwt = JwtContext.get();
        }
        if (jwt == null) {
            return false;
        }
        Claim claim = jwt.getClaim(SU);
        if (claim == null) {
            return false;
        }
        return claim.asBoolean();
    }

    /**
     * 获取当前请求的RP对象（角色与权限）。
     * @return RP对象
     */
    public static RP getRP() {
        return getRP(JwtContext.get());
    }

    /**
     * 获取指定JWT的RP对象（角色与权限）。
     * @param jwt 解码后的JWT
     * @return RP对象
     */
    public static RP getRP(DecodedJWT jwt) {
        if (jwt == null) {
            return new RP();
        }
        RP rp = (RP) CacheUtils.get(TOKEN_CACHE_RP_PREFIX + jwt.getSubject());
        if (rp == null) {
            rp = saveUserRP(jwt.getSubject(), getUserType(jwt));
        }
        return rp;
    }

    /**
     * 获取当前请求的所有权限。
     * @return 权限集合
     */
    public static Set<String> getPermissions() {
        return getPermissions(JwtContext.get());
    }

    /**
     * 获取指定JWT的所有权限。
     * @param jwt 解码后的JWT
     * @return 权限集合
     */
    public static Set<String> getPermissions(DecodedJWT jwt) {
        if (jwt == null) {
            return Collections.emptySet();
        }
        RP rp = (RP) CacheUtils.get(TOKEN_CACHE_RP_PREFIX + jwt.getSubject());
        if (rp == null) {
            rp = saveUserRP(jwt.getSubject(), getUserType(jwt));
        }
        return rp.getPermissionPaths();
    }

    /**
     * 获取当前请求的所有角色。
     * @return 角色集合
     */
    public static Set<String> getRoles() {
        return getRoles(JwtContext.get());
    }

    /**
     * 获取指定JWT的所有角色。
     * @param jwt 解码后的JWT
     * @return 角色集合
     */
    public static Set<String> getRoles(DecodedJWT jwt) {
        if (jwt == null) {
            return Collections.emptySet();
        }
        RP rp = (RP) CacheUtils.get(TOKEN_CACHE_RP_PREFIX + jwt.getSubject());
        if (rp == null) {
            rp = saveUserRP(jwt.getSubject(), getUserType(jwt));
        }
        return rp.getRoles();
    }


    /**
     * 生成token（默认超时时间）。
     * @param userProps 用户信息
     * @return TokenInfo
     */
    public static TokenInfo createToken(UserProps userProps) {
        return createToken(userProps, icJwtConfig.getTimeout(), icJwtConfig.getTimeout() * 10, null);
    }

    /**
     * 生成token。
     * @param userProps 用户信息
     * @param timeOut token超时时间（分钟）
     * @param refreshTimeOut 刷新token超时时间（分钟）
     * @return TokenInfo
     */
    public static TokenInfo createToken(UserProps userProps, int timeOut, int refreshTimeOut) {
        return createToken(userProps, timeOut, refreshTimeOut, null);
    }

    /**
     * 生成token。
     * @param userProps 用户信息
     * @param timeOut token超时时间（分钟）
     * @param refreshTimeOut 刷新token超时时间（分钟）
     * @param sessionId 会话唯一标识
     * @return TokenInfo
     */
    public static TokenInfo createToken(UserProps userProps, int timeOut, int refreshTimeOut, Long sessionId) {
        TokenInfo tokenInfo = new TokenInfo();
        boolean create = false;
        if (sessionId == null) {
            create = true;
            sessionId = UUID.randomUUID().getMostSignificantBits();
        } else if (onlineUserService != null) {
            // 检查一下sessionId是否有效
            onlineUserService.verify(userProps.getUserId().toString(), sessionId);
        }

        long nowTime = System.currentTimeMillis();
        Date expireTime = new Date(nowTime + 1000L * 60 * timeOut);
        Date refreshExpireTime = new Date(nowTime + 1000L * 60 * refreshTimeOut);

        tokenInfo.setToken(createQuicklyToken(userProps, expireTime, sessionId));
        tokenInfo.setRefreshToken(createRefreshToken(userProps, refreshExpireTime, sessionId));

        if (create && onlineUserService != null) {
            IOnlineUserService.OnlineInfo onlineInfo = new IOnlineUserService.OnlineInfo();
            onlineInfo.setExpireTime(refreshExpireTime.getTime());
            onlineInfo.setLoginTime(nowTime);
            onlineInfo.setUserId(userProps.getUserId());
            onlineInfo.setSessionId(sessionId);
            onlineInfo.setUserType(userProps.getUserType());
            if (userProps.getRequest() != null) {
                String userAgent = userProps.getRequest().getHeader("User-Agent");
                String platform = userProps.getRequest().getHeader("Platform");
                onlineInfo.setIp(IpUtils.getIpAddress(userProps.getRequest()));
                if (platform.startsWith("APP")) {
                    onlineInfo.setSystem(userAgent);
                } else {
                    UserAgent parsedUserAgent = UserAgent.parseUserAgentString(userAgent);
                    String browser = parsedUserAgent.getBrowser().getName();
                    String os = parsedUserAgent.getOperatingSystem().getName();
                    onlineInfo.setBrowser(browser);
                    onlineInfo.setSystem(os);
                }
                onlineInfo.setPlatform(platform);
                onlineInfo.setLocation(IpUtils.getIpRegion(onlineInfo.getIp()));
            }
            onlineUserService.login(onlineInfo);
        } else if (onlineUserService != null) {
            onlineUserService.refresh(sessionId, refreshExpireTime.getTime());
        }
        // 缓存有效的token
        CacheUtils.set(TOKEN_CACHE_SESSION_ID_PREFIX + sessionId, 1, (refreshExpireTime.getTime() - System.currentTimeMillis()) / 1000);
        return tokenInfo;
    }

    private static String createQuicklyToken(UserProps userProps, Date expireTime, Long sessionId) {
        try {
            final Algorithm signer = Algorithm.HMAC256(icJwtConfig.getSecret());//生成签名
            String token = JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(String.valueOf(userProps.getUserId()))
                    .withClaim(USER_NAME, String.valueOf(userProps.getUsername()))
                    .withClaim(USER_TYPE, userProps.getUserType())
                    .withClaim(SU, userProps.isSu())
                    .withClaim(TOKEN_SESSION_ID, sessionId.toString())
                    .withClaim(REFRESH_TOKEN, false)
                    .withExpiresAt(expireTime)
                    .sign(signer);
            saveUserRP(userProps.getUserId(), userProps.getUserType());
            return token;
        } catch (Exception e) {
            log.error("生成token异常：", e);
            return null;
        }
    }

    /**
     * 刷新token（默认超时时间）。
     * @return TokenInfo
     */
    public static TokenInfo refreshToken() {
        return refreshToken(icJwtConfig.getTimeout(), icJwtConfig.getTimeout() * 10);
    }

    /**
     * 刷新token。
     * @param timeOut token超时时间（分钟）
     * @param refreshTimeOut 刷新token超时时间（分钟）
     * @return TokenInfo
     */
    public static TokenInfo refreshToken(int timeOut, int refreshTimeOut) {
        DecodedJWT decodedJWT = JwtContext.get();
        if (decodedJWT == null) {
            throw new TokenOutTimeException();
        }
        if (!decodedJWT.getClaim(REFRESH_TOKEN).asBoolean()) {
            throw new RuntimeException("非法刷新token");
        }
        UserProps userProps = new UserProps();
        userProps.setUserType(decodedJWT.getClaim(USER_TYPE).asString());
        userProps.setUsername(decodedJWT.getClaim(USER_NAME).asString());
        userProps.setSu(decodedJWT.getClaim(SU).asBoolean());
        userProps.setUserId(decodedJWT.getSubject());
        return createToken(userProps, timeOut, refreshTimeOut, Long.parseLong(decodedJWT.getClaim(TOKEN_SESSION_ID).asString()));
    }

    /**
     * 创建刷新token
     *
     * @param userProps
     * @param expireTime
     * @return
     */
    private static String createRefreshToken(UserProps userProps, Date expireTime, Long sessionId) {
        try {
            final Algorithm signer = Algorithm.HMAC256(icJwtConfig.getSecret());//生成签名
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(String.valueOf(userProps.getUserId()))
                    .withClaim(USER_NAME, String.valueOf(userProps.getUsername()))
                    .withClaim(USER_TYPE, userProps.getUserType())
                    .withClaim(SU, userProps.isSu())
                    .withClaim(TOKEN_SESSION_ID, sessionId.toString())
                    .withClaim(REFRESH_TOKEN, true)
                    .withExpiresAt(expireTime)
                    .sign(signer);
        } catch (Exception e) {
            log.error("生成token异常：", e);
            return null;
        }
    }


    /**
     * 校验token。
     * @param token token字符串
     * @return 解码后的JWT，若无效返回null
     */
    public static DecodedJWT verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(icJwtConfig.getSecret());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            // 判断token是否还有效
            Object object = CacheUtils.get(TOKEN_CACHE_SESSION_ID_PREFIX + getTokenSessionId(decodedJWT));
            if (object == null) {
                return null;
            }
            return decodedJWT;
        } catch (IllegalArgumentException | JWTVerificationException e) {
            return null;
        }
    }

    /**
     * 注销会话。
     * @param sessionId 会话唯一标识
     */
    public static void logout(Long sessionId) {
        CacheUtils.remove(TOKEN_CACHE_SESSION_ID_PREFIX + sessionId);
    }


    /**
     * 用实现类来加载角色权限。
     * @param userId 用户id
     * @param userType 用户类型
     * @return RP对象
     */
    private static RP saveUserRP(Object userId, String userType) {
        if (userRPService == null) {
            return null;
        }
        RP rp = userRPService.load(userId, userType);
        if (rp == null) {
            rp = new RP();
        }
        if (rp.getPermissionPaths() == null) {
            rp.setPermissionPaths(Collections.emptySet());
        }
        if (rp.getRoles() == null) {
            rp.setRoles(Collections.emptySet());
        }
        CacheUtils.set(TOKEN_CACHE_RP_PREFIX + userId, rp);
        return rp;
    }

}

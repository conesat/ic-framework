package cn.icframework.auth.standard;


import cn.icframework.auth.utils.JWTUtils;
import lombok.Getter;
import lombok.Setter;


/**
 * 用户角色权限
 *
 * @author hzl
 * @since 2022/6/30
 */
public interface IOnlineUserService {

    /**
     * 记录新建的在线会话。
     * 当 JWTUtils.createToken 首次创建 sessionId 时回调，用于保存在线用户状态。
     *
     * @param onlineInfo 在线会话信息
     */
    default void recordLoginSession(OnlineInfo onlineInfo) {
        login(onlineInfo);
    }

    /**
     * 用户登录后的在线状态回调。
     * 保留该方法用于兼容已有实现，新代码优先实现 {@link #recordLoginSession(OnlineInfo)}。
     *
     * @param onlineInfo 在线会话信息
     */
    @Deprecated(since = "1.0.3", forRemoval = false)
    default void login(OnlineInfo onlineInfo) {
        throw new UnsupportedOperationException("Please implement recordLoginSession(OnlineInfo)");
    }

    /**
     * token刷新
     * 当调用JWTUtils.refreshToken时回调
     * @param sessionId 会话唯一标识
     * @param timeOut 下次过期时间
     */
    void refresh(Long sessionId, long timeOut);

    /**
     * 检查token是否有效
     * 如果不允许使用，请抛出对应异常
     * @param userId
     * @param sessionId
     */
    void verify(String userId, Long sessionId);

    /**
     * 注销会话，强退
     */
    default void removeSessionId(Long sessionId) {
        JWTUtils.logout(sessionId);
    }

    /**
     * 恢复在线状态
     */
    default void recoveryUserOnlineStatus(Long sessionId) {
        JWTUtils.recoveryUserOnlineStatus(sessionId);
    }

    @Getter
    @Setter
    class OnlineInfo {
        /**
         * 用户id
         */
        private Object userId;
        /**
         * 登录ip
         */
        private String ip;
        /**
         * ip地址
         */
        private String location;
        /**
         * 系统类型
         */
        private String system;
        /**
         * 浏览器标识
         */
        private String browser;
        /**
         * 登录平台
         */
        private String platform;
        /**
         * 登录时间
         */
        private long loginTime;
        /**
         * 过期时间
         */
        private long expireTime;
        /**
         * 会话唯一标识
         */
        private Long sessionId;
        /**
         * 用户类型
         */
        private String userType;
    }
}

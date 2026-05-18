package cn.icframework.auth.config;

import cn.icframework.auth.annotation.NotValidateSystem;
import cn.icframework.auth.annotation.RequireAuth;
import cn.icframework.auth.entity.RP;
import cn.icframework.auth.processor.PermissionHelper;
import cn.icframework.auth.standard.IOnlineUserService;
import cn.icframework.auth.standard.ISystemVerifyService;
import cn.icframework.auth.threadlocal.JwtContext;
import cn.icframework.auth.utils.JWTUtils;
import cn.icframework.common.consts.Api;
import cn.icframework.core.common.exception.NeedLoginException;
import cn.icframework.core.common.exception.PermissionDeniedException;
import cn.icframework.core.common.exception.PermissionDenyException;
import cn.icframework.core.common.exception.SystemActivationFailException;
import cn.icframework.core.common.exception.SystemActivationOutDateException;
import cn.icframework.core.common.exception.SystemUninitializedException;
import cn.icframework.core.common.exception.TokenOutTimeException;
import cn.icframework.core.utils.Assert;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 请求拦截，登录过来，权限过滤
 * 
 * @author hzl
 * @since 2021-05-31 09:26:00
 */
public class AuthInterceptor implements HandlerInterceptor {
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired(required = false)
    private ISystemVerifyService systemService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired(required = false)
    private IOnlineUserService onlineUserService;

    /**
     * 初始化 contextPath，若为“/”则置为空字符串。
     */
    @PostConstruct
    public void init() {
        if ("/".equals(contextPath)) {
            contextPath = "";
        }
    }

    /**
     * 请求预处理，进行权限校验、token校验、系统可用性校验等。
     *
     * @param httpServletRequest  请求对象
     * @param httpServletResponse 响应对象
     * @param handler             处理器
     * @return 是否放行
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest httpServletRequest,
            @NonNull HttpServletResponse httpServletResponse,
            @NonNull Object handler) {
        if (handler instanceof ResourceHttpRequestHandler)
            return true;

        String token = httpServletRequest.getHeader("Authorization");
        String refreshToken = httpServletRequest.getHeader("Refresh-Token");
        String requestURI = httpServletRequest.getRequestURI();
        // OPTIONS请求类型直接返回不处理
        if ("OPTIONS".equals(httpServletRequest.getMethod())) {
            return true;
        }
        Class<?> clazz = null;
        Method method = null;
        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            clazz = ((HandlerMethod) handler).getBeanType();
            method = ((HandlerMethod) handler).getMethod();
        }

        String adminUrlPre = contextPath + Api.API_SYS;
        if (requestURI.replace("/", "").startsWith(adminUrlPre.replace("/", ""))) {
            // 校验系统可用性
            verifySystem(handler);
        }
        assert clazz != null;
        if (StringUtils.hasLength(refreshToken)) {
            verifyAuth(clazz, method, refreshToken);
            return true;
        }
        DecodedJWT decodedJWT = verifyAuth(clazz, method, token);
        // 是否限制单处登录
        if (decodedJWT != null && onlineUserService != null) {
            onlineUserService.verify(decodedJWT.getSubject(), JWTUtils.getTokenSessionId(decodedJWT));
        }
        return true;
    }

    /**
     * 校验权限，token、用户类型、角色、权限等。
     *
     * @param clazz  类对象
     * @param method 方法对象
     * @param token  token字符串
     * @return 解码后的JWT
     */
    private DecodedJWT verifyAuth(Class<?> clazz, Method method, String token) {
        if (method == null) {
            return null;
        }
        DecodedJWT decodedJWT = null;
        RequireAuth requireAuth = clazz.getAnnotation(RequireAuth.class);
        RequireAuth methodRequireAuth = method.getAnnotation(RequireAuth.class);
        String requireUserType = getUserType(requireAuth, methodRequireAuth);
        if (methodRequireAuth != null) {
            requireAuth = methodRequireAuth;
        }
        if (requireAuth != null) {
            if (!StringUtils.hasLength(token)) {
                throw new NeedLoginException();
            }
            decodedJWT = JWTUtils.verifyToken(token);
            if (decodedJWT == null) {
                throw new TokenOutTimeException();
            }
            // 只校验token
            if (requireAuth.onlyToken()) {
                JwtContext.set(decodedJWT);
                return decodedJWT;
            }
            if (JWTUtils.isRefreshToken(decodedJWT)) {
                throw new TokenOutTimeException();
            }
            // 检验用户类型是否一致
            if (StringUtils.hasLength(requireUserType)) {
                String userType = JWTUtils.getUserType(decodedJWT);
                if (!requireUserType.equals(userType)) {
                    throw new PermissionDeniedException();
                }
            }

            // mixRP = true 角色与权限满足一个即可，否则两个都要通过
            // 校验角色
            boolean pass = false;

            RP rp = JWTUtils.getRP(decodedJWT);
            if (requireAuth.role().length > 0) {
                // 限制了角色，并且允许具备角色或者权限就放行，首先判断角色
                for (String r : requireAuth.role()) {
                    pass = rp.getRoles().contains(r);
                    if (pass) {
                        break;
                    }
                }
                Assert.isFalse(!requireAuth.mixRP() && !pass, new PermissionDenyException());
            }

            // 角色未通过校验或者 角色通过校验但是不能mixRP，需要保证有权限才行
            if (!pass || !requireAuth.mixRP()) {
                Set<String> methodPermissionSet = PermissionHelper.getMethodPermissionSet(method);
                pass = methodPermissionSet.stream().anyMatch(rp.getPermissionPaths()::contains);
                Assert.isTrue(pass, new PermissionDenyException());
            }
            JwtContext.set(decodedJWT);
        }

        return decodedJWT;
    }

    /**
     * postHandle 方法，预留扩展，当前无实现。
     */
    @Override
    public void postHandle(@NonNull HttpServletRequest httpServletRequest,
            @NonNull HttpServletResponse httpServletResponse,
            @NonNull Object o, @Nullable ModelAndView modelAndView) throws Exception {

    }

    /**
     * afterCompletion 方法，预留扩展，当前无实现。
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest httpServletRequest,
            @NonNull HttpServletResponse httpServletResponse,
            @NonNull Object o, @Nullable Exception e) throws Exception {

    }

    /**
     * 校验系统可用性，未初始化、未激活、过期等抛出异常。
     *
     * @param handler 处理器
     */
    private void verifySystem(Object handler) {
        if (systemService == null) {
            return;
        }

        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            Class<?> clazz = ((HandlerMethod) handler).getMethod().getDeclaringClass();
            NotValidateSystem notValidateSystem = clazz.getAnnotation(NotValidateSystem.class);
            if (notValidateSystem != null) {
                return;
            }
        }

        ISystemVerifyService.StatusEnum validate = systemService.validate();
        if (ISystemVerifyService.StatusEnum.UNINITIALIZED == validate) {
            throw new SystemUninitializedException();
        }

        if (ISystemVerifyService.StatusEnum.NOT_ACTIVE == validate) {
            throw new SystemActivationFailException();
        }

        if (ISystemVerifyService.StatusEnum.OUT_DATE == validate) {
            throw new SystemActivationOutDateException();
        }
    }

    /**
     * 获取用户类型，优先取方法注解。
     *
     * @param requireAuth       类注解
     * @param methodRequireAuth 方法注解
     * @return 用户类型
     */
    private static String getUserType(RequireAuth requireAuth, RequireAuth methodRequireAuth) {
        if (methodRequireAuth != null && StringUtils.hasLength(methodRequireAuth.userType())) {
            return methodRequireAuth.userType();
        }
        if (requireAuth != null) {
            return requireAuth.userType();
        }
        return null;
    }
}

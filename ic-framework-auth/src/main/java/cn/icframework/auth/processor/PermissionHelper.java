package cn.icframework.auth.processor;

import cn.icframework.auth.annotation.RequireAuth;
import cn.icframework.auth.entity.PermissionGroupInit;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 初始化权限帮助类
 *
 * @author ic
 */
public class PermissionHelper {
    /**
     * 用于初始化权限
     * 这里的数据按controller分组，permission对应到每个controller的接口
     * 初始化完成以后会被释放掉
     */
    public static List<PermissionGroupInit> PERMISSION_GROUP_INIT_MAP = new ArrayList<>();

    /**
     * 这里保存所有接口对于需要的权限集合，用于鉴权时候校验是否拥有权限
     */
    private static final Map<String, Set<String>> METHOD_PERMISSION_GROUP_SET = new HashMap<>();

    /**
     * 获取方法对应的权限集合。
     * @param method 方法对象
     * @return 权限集合
     */
    public static Set<String> getMethodPermissionSet(Method method) {
        Set<String> set = METHOD_PERMISSION_GROUP_SET.get(method.toString());
        return set == null ? Collections.emptySet() : set;
    }

    /**
     * 接收一个controller类，解析里面的接口到PERMISSION_GROUP_INIT_MAP
     * 用于启动完成后通知系统进行初始化
     *
     * @param controllerClass controller类
     */
    public static void handle(Class<?> controllerClass) {
        RestController restController = controllerClass.getDeclaredAnnotation(RestController.class);
        if (restController == null) {
            return;
        }
        RequestMapping requestMappingClass = controllerClass.getDeclaredAnnotation(RequestMapping.class);
        String[] paths = requestMappingClass.value();
        for (String path : paths) {
            PermissionGroupInit permissionGroupInit = new PermissionGroupInit();
            permissionGroupInit.setName(requestMappingClass.name());
            permissionGroupInit.setPath(path.replaceAll("/", ":"));
            handelPermissions(controllerClass, permissionGroupInit);
            // 如果这个类下面没有需要鉴权的接口就跳过
            if (permissionGroupInit.getPermissions().isEmpty()) {
                continue;
            }
            PERMISSION_GROUP_INIT_MAP.add(permissionGroupInit);
        }
    }

    /**
     * 解析controller类下所有接口权限，填充到权限分组对象。
     * @param controllerClass controller类
     * @param permissionGroupInit 权限分组对象
     */
    private static void handelPermissions(Class<?> controllerClass, PermissionGroupInit permissionGroupInit) {
        RequireAuth requireAuth = controllerClass.getDeclaredAnnotation(RequireAuth.class);
        Method[] methods = controllerClass.getMethods();
        for (Method method : methods) {
            List<PermissionGroupInit.Permission> permissions = getPermissions(method, requireAuth);
            if (permissions.isEmpty()) {
                continue;
            }
            permissionGroupInit.getPermissions().addAll(permissions);
            for (PermissionGroupInit.Permission permission : permissions) {
                Set<String> permissionSet = METHOD_PERMISSION_GROUP_SET.computeIfAbsent(method.toString(), k -> new HashSet<>());
                permissionSet.add(permissionGroupInit.getPath() + permission.getPath());
            }
        }
    }

    /**
     * 获取方法上的权限信息。
     * @param method 方法对象
     * @param requireAuth 类上的RequireAuth注解
     * @return 权限信息列表
     */
    private static List<PermissionGroupInit.Permission> getPermissions(Method method, RequireAuth requireAuth) {
        List<PermissionGroupInit.Permission> res = new ArrayList<>();
        RequireAuth methodRequireAuth = method.getDeclaredAnnotation(RequireAuth.class);
        if (requireAuth == null && methodRequireAuth == null) {
            return res;
        }
        String requireUserType = getUserType(requireAuth, methodRequireAuth);
        if (methodRequireAuth != null) {
            requireAuth = methodRequireAuth;
        }
        if (requireAuth.onlyToken()) {
            return res;
        }

        RequestMapping requestMapping = method.getDeclaredAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            if (requestMapping.value().length == 0) {
                PermissionGroupInit.Permission permission = new PermissionGroupInit.Permission();
                permission.setName(requestMapping.name());
                permission.setPath(":");
                permission.setUserType(requireUserType);
                res.add(permission);
            }
            for (String path : requestMapping.value()) {
                PermissionGroupInit.Permission permission = new PermissionGroupInit.Permission();
                permission.setName(requestMapping.name());
                permission.setPath(path.replaceAll("/", ":"));
                permission.setUserType(requireUserType);
                res.add(permission);
            }
        }

        GetMapping getMapping = method.getDeclaredAnnotation(GetMapping.class);
        if (getMapping != null) {
            if (getMapping.value().length == 0) {
                PermissionGroupInit.Permission permission = new PermissionGroupInit.Permission();
                permission.setName(getMapping.name());
                permission.setPath(":get");
                permission.setUserType(requireUserType);
                res.add(permission);
            }
            for (String path : getMapping.value()) {
                PermissionGroupInit.Permission permission = new PermissionGroupInit.Permission();
                permission.setName(getMapping.name());
                permission.setPath(path.replaceAll("/", ":"));
                permission.setUserType(requireUserType);
                res.add(permission);
            }
        }

        PostMapping postMapping = method.getDeclaredAnnotation(PostMapping.class);
        if (postMapping != null) {
            if (postMapping.value().length == 0) {
                PermissionGroupInit.Permission permission = new PermissionGroupInit.Permission();
                permission.setName(postMapping.name());
                permission.setPath(":post");
                permission.setUserType(requireUserType);
                res.add(permission);
            }
            for (String path : postMapping.value()) {
                PermissionGroupInit.Permission permission = new PermissionGroupInit.Permission();
                permission.setName(postMapping.name());
                permission.setPath(path.replaceAll("/", ":"));
                permission.setUserType(requireUserType);
                res.add(permission);
            }
        }

        DeleteMapping deleteMapping = method.getDeclaredAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            if (deleteMapping.value().length == 0) {
                PermissionGroupInit.Permission permission = new PermissionGroupInit.Permission();
                permission.setName(deleteMapping.name());
                permission.setPath(":delete");
                permission.setUserType(requireUserType);
                res.add(permission);
            }
            for (String path : deleteMapping.value()) {
                PermissionGroupInit.Permission permission = new PermissionGroupInit.Permission();
                permission.setName(deleteMapping.name());
                permission.setPath(path.replaceAll("/", ":"));
                permission.setUserType(requireUserType);
                res.add(permission);
            }
        }

        PutMapping putMapping = method.getDeclaredAnnotation(PutMapping.class);
        if (putMapping != null) {
            if (putMapping.value().length == 0) {
                PermissionGroupInit.Permission permission = new PermissionGroupInit.Permission();
                permission.setName(putMapping.name());
                permission.setPath(":put");
                permission.setUserType(requireUserType);
                res.add(permission);
            }
            for (String path : putMapping.value()) {
                PermissionGroupInit.Permission permission = new PermissionGroupInit.Permission();
                permission.setName(putMapping.name());
                permission.setPath(path.replaceAll("/", ":"));
                permission.setUserType(requireUserType);
                res.add(permission);
            }
        }

        PatchMapping patchMapping = method.getDeclaredAnnotation(PatchMapping.class);
        if (patchMapping != null) {
            if (patchMapping.value().length == 0) {
                PermissionGroupInit.Permission permission = new PermissionGroupInit.Permission();
                permission.setName(patchMapping.name());
                permission.setPath(":patch");
                permission.setUserType(requireUserType);
                res.add(permission);
            }
            for (String path : patchMapping.value()) {
                PermissionGroupInit.Permission permission = new PermissionGroupInit.Permission();
                permission.setName(patchMapping.name());
                permission.setPath(path.replaceAll("/", ":"));
                permission.setUserType(requireUserType);
                res.add(permission);
            }
        }
        return res;
    }

    /**
     * 获取用户类型，优先取方法注解。
     * @param requireAuth 类注解
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

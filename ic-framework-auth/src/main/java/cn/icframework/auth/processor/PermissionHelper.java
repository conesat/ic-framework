package cn.icframework.auth.processor;

import cn.icframework.auth.annotation.RequireAuth;
import cn.icframework.auth.entity.PermissionGroupInit;
import org.springframework.core.annotation.AnnotationUtils;
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
     * 这里保存所有接口对应的权限ID集合，用于鉴权时优先进行数字匹配。
     */
    private static final Map<String, Set<Long>> METHOD_PERMISSION_ID_SET = new HashMap<>();

    /**
     * 重置扫描到的权限数据。
     * 启动初始化前会以Spring MVC实际注册的HandlerMapping为准重新扫描，避免代理或Bean初始化顺序导致漏扫。
     */
    public static void reset() {
        PERMISSION_GROUP_INIT_MAP = new ArrayList<>();
        METHOD_PERMISSION_GROUP_SET.clear();
        METHOD_PERMISSION_ID_SET.clear();
    }

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
     * 获取方法对应的权限ID集合。
     * @param method 方法对象
     * @return 权限ID集合
     */
    public static Set<Long> getMethodPermissionIdSet(Method method) {
        Set<Long> set = METHOD_PERMISSION_ID_SET.get(method.toString());
        return set == null ? Collections.emptySet() : set;
    }

    /**
     * 刷新权限路径到权限ID的映射。
     * 权限落库后由业务模块回填，用于运行期优先使用稳定的权限ID鉴权。
     *
     * @param permissionIdMap 权限完整路径与权限ID映射
     */
    public static void refreshPermissionIds(Map<String, Long> permissionIdMap) {
        METHOD_PERMISSION_ID_SET.clear();
        for (Map.Entry<String, Set<String>> entry : METHOD_PERMISSION_GROUP_SET.entrySet()) {
            Set<Long> permissionIds = new HashSet<>();
            for (String permissionPath : entry.getValue()) {
                Long permissionId = permissionIdMap.get(permissionPath);
                if (permissionId != null) {
                    permissionIds.add(permissionId);
                }
            }
            if (!permissionIds.isEmpty()) {
                METHOD_PERMISSION_ID_SET.put(entry.getKey(), permissionIds);
            }
        }
    }

    /**
     * 接收一个controller类，解析里面的接口到PERMISSION_GROUP_INIT_MAP
     * 用于启动完成后通知系统进行初始化
     *
     * @param controllerClass controller类
     */
    public static void handle(Class<?> controllerClass) {
        if (PERMISSION_GROUP_INIT_MAP == null) {
            PERMISSION_GROUP_INIT_MAP = new ArrayList<>();
        }
        RestController restController = AnnotationUtils.findAnnotation(controllerClass, RestController.class);
        if (restController == null) {
            return;
        }
        RequestMapping requestMappingClass = AnnotationUtils.findAnnotation(controllerClass, RequestMapping.class);
        if (requestMappingClass == null) {
            return;
        }
        String[] paths = getMappingPaths(requestMappingClass.value(), requestMappingClass.path());
        for (String path : paths) {
            String groupPath = toPermissionPath(path, ":");
            PermissionGroupInit permissionGroupInit = new PermissionGroupInit();
            permissionGroupInit.setName(requestMappingClass.name());
            permissionGroupInit.setPath(groupPath);
            handelPermissions(controllerClass, permissionGroupInit);
            // 如果这个类下面没有需要鉴权的接口就跳过
            if (permissionGroupInit.getPermissions().isEmpty()) {
                continue;
            }
            PermissionGroupInit exists = findPermissionGroup(groupPath);
            if (exists == null) {
                PERMISSION_GROUP_INIT_MAP.add(permissionGroupInit);
            } else {
                mergePermissions(exists, permissionGroupInit);
            }
        }
    }

    private static PermissionGroupInit findPermissionGroup(String path) {
        for (PermissionGroupInit permissionGroupInit : PERMISSION_GROUP_INIT_MAP) {
            if (path.equals(permissionGroupInit.getPath())) {
                return permissionGroupInit;
            }
        }
        return null;
    }

    private static void mergePermissions(PermissionGroupInit exists, PermissionGroupInit incoming) {
        Set<String> permissionPaths = new HashSet<>();
        for (PermissionGroupInit.Permission permission : exists.getPermissions()) {
            permissionPaths.add(permission.getPath());
        }
        for (PermissionGroupInit.Permission permission : incoming.getPermissions()) {
            if (permissionPaths.add(permission.getPath())) {
                exists.getPermissions().add(permission);
            }
        }
    }

    /**
     * 解析controller类下所有接口权限，填充到权限分组对象。
     * @param controllerClass controller类
     * @param permissionGroupInit 权限分组对象
     */
    private static void handelPermissions(Class<?> controllerClass, PermissionGroupInit permissionGroupInit) {
        RequireAuth requireAuth = AnnotationUtils.findAnnotation(controllerClass, RequireAuth.class);
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
        RequireAuth methodRequireAuth = AnnotationUtils.findAnnotation(method, RequireAuth.class);
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
            addPermissions(res, requestMapping.name(), requireUserType, ":",
                    getMappingPaths(requestMapping.value(), requestMapping.path()));
        }

        GetMapping getMapping = AnnotationUtils.findAnnotation(method, GetMapping.class);
        if (getMapping != null) {
            addPermissions(res, getMapping.name(), requireUserType, ":get",
                    getMappingPaths(getMapping.value(), getMapping.path()));
        }

        PostMapping postMapping = AnnotationUtils.findAnnotation(method, PostMapping.class);
        if (postMapping != null) {
            addPermissions(res, postMapping.name(), requireUserType, ":post",
                    getMappingPaths(postMapping.value(), postMapping.path()));
        }

        DeleteMapping deleteMapping = AnnotationUtils.findAnnotation(method, DeleteMapping.class);
        if (deleteMapping != null) {
            addPermissions(res, deleteMapping.name(), requireUserType, ":delete",
                    getMappingPaths(deleteMapping.value(), deleteMapping.path()));
        }

        PutMapping putMapping = AnnotationUtils.findAnnotation(method, PutMapping.class);
        if (putMapping != null) {
            addPermissions(res, putMapping.name(), requireUserType, ":put",
                    getMappingPaths(putMapping.value(), putMapping.path()));
        }

        PatchMapping patchMapping = AnnotationUtils.findAnnotation(method, PatchMapping.class);
        if (patchMapping != null) {
            addPermissions(res, patchMapping.name(), requireUserType, ":patch",
                    getMappingPaths(patchMapping.value(), patchMapping.path()));
        }
        return res;
    }

    private static String[] getMappingPaths(String[] values, String[] paths) {
        return values.length > 0 ? values : paths;
    }

    private static void addPermissions(List<PermissionGroupInit.Permission> permissions,
            String name,
            String userType,
            String defaultPath,
            String[] paths) {
        if (paths.length == 0) {
            addPermission(permissions, name, userType, defaultPath);
            return;
        }
        for (String path : paths) {
            addPermission(permissions, name, userType, toPermissionPath(path, defaultPath));
        }
    }

    private static void addPermission(List<PermissionGroupInit.Permission> permissions,
            String name,
            String userType,
            String path) {
        PermissionGroupInit.Permission permission = new PermissionGroupInit.Permission();
        permission.setName(name);
        permission.setPath(path);
        permission.setUserType(userType);
        permissions.add(permission);
    }

    private static String toPermissionPath(String path, String defaultPath) {
        if (!StringUtils.hasLength(path)) {
            return defaultPath;
        }
        String permissionPath = path.replaceAll("/", ":");
        if (!permissionPath.startsWith(":")) {
            permissionPath = ":" + permissionPath;
        }
        return permissionPath;
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

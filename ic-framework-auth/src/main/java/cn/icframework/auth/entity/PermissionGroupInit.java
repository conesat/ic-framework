package cn.icframework.auth.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 权限分组初始化实体，描述分组及其下权限的初始化信息。
 *
 * @author hzl
 * @since 2023/6/20
 */
@Getter
@Setter
public class PermissionGroupInit {

    /**
     * 分组路径。
     */
    private String path;

    /**
     * 分组名称。
     */
    private String name;

    /**
     * 权限列表。
     */
    private List<Permission> permissions = new ArrayList<>();

    /**
     * 权限信息内部类。
     */
    @Getter
    @Setter
    public static class Permission {
        /**
         * 权限路径。
         */
        private String path;
        /**
         * 用户类型。
         */
        private String userType;
        /**
         * 权限名称。
         */
        private String name;

        @Override
        public String toString() {
            return path + ":" + name + ":" + userType;
        }
    }

    @Override
    public String toString() {
        permissions.sort(Comparator.comparing(Permission::getPath));
        return path + ":" + name + ":" + permissions.toString();
    }
}

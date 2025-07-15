package cn.icframework.auth.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * 角色与权限信息实体。
 *
 * @author hzl
 * @since 2023/8/9
 */
@Getter
@Setter
public class RP {
    /**
     * 角色标识集合。
     */
    private Set<String> roles;
    /**
     * 权限路径集合。
     */
    private Set<String> permissionPaths;
}

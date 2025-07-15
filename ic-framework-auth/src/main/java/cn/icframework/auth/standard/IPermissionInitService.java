package cn.icframework.auth.standard;


import cn.icframework.auth.entity.PermissionGroupInit;

import java.util.List;

/**
 * 权限初始化
 *
 * @author hzl
 * @since 2022/6/30
 */
public interface IPermissionInitService {

    /**
     * 初始化权限
     */
    void init(List<PermissionGroupInit> permissions);
}

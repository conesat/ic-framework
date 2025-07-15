package cn.icframework.auth.standard;


import cn.icframework.auth.entity.RP;

/**
 * 用户角色权限
 *
 * @author hzl
 * @since 2022/6/30
 */
public interface IUserRPService {

    /**
     * 获取用户角色权限
     *
     */
    RP load(Object userId, String userType);
}

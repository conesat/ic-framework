package cn.icframework.auth;

import cn.icframework.auth.processor.PermissionHelper;
import cn.icframework.auth.standard.IPermissionInitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 权限初始化
 *
 * @author hzl
 * @since 2023/5/27 0027
 */
@Component
@Order(1)
public class AuthPermissionInitRunner implements CommandLineRunner {

    @Autowired(required = false)
    private IPermissionInitService permissionInitService;


    @Override
    public void run(String... args) {
        if (permissionInitService != null) {
            permissionInitService.init(PermissionHelper.PERMISSION_GROUP_INIT_MAP);
            PermissionHelper.PERMISSION_GROUP_INIT_MAP = null;
        }
    }

}

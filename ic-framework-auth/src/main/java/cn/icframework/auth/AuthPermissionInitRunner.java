package cn.icframework.auth;

import cn.icframework.auth.processor.PermissionHelper;
import cn.icframework.auth.standard.IPermissionInitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 权限初始化
 *
 * @author hzl
 * @since 2023/5/27 0027
 */
@Component
@Order(1)
@Slf4j
public class AuthPermissionInitRunner implements CommandLineRunner {

    @Autowired(required = false)
    private IPermissionInitService permissionInitService;

    @Autowired(required = false)
    private RequestMappingHandlerMapping requestMappingHandlerMapping;


    @Override
    public void run(String... args) {
        if (permissionInitService != null) {
            refreshPermissionsFromHandlerMapping();
            permissionInitService.init(PermissionHelper.PERMISSION_GROUP_INIT_MAP);
            PermissionHelper.PERMISSION_GROUP_INIT_MAP = null;
        }
    }

    private void refreshPermissionsFromHandlerMapping() {
        if (requestMappingHandlerMapping == null) {
            return;
        }
        PermissionHelper.reset();
        Set<Class<?>> controllerClasses = new LinkedHashSet<>();
        for (HandlerMethod handlerMethod : requestMappingHandlerMapping.getHandlerMethods().values()) {
            controllerClasses.add(handlerMethod.getBeanType());
        }
        for (Class<?> controllerClass : controllerClasses) {
            PermissionHelper.handle(controllerClass);
        }
        log.info("Permission initialization scanned {} controller classes and generated {} permission groups.",
                controllerClasses.size(),
                PermissionHelper.PERMISSION_GROUP_INIT_MAP.size());
    }

}

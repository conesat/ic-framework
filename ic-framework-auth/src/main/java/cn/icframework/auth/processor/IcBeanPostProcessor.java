package cn.icframework.auth.processor;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;


/**
 * 后置处理器：初始化前后进行处理工作
 *
 * @author hzl
 * @since 2023/6/28
 */
@Component
@AllArgsConstructor
public class IcBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, @NotNull String beanName) throws BeansException {
        if (bean.getClass().getDeclaredAnnotation(RestController.class) != null) {
            // api 需要生成权限
            PermissionHelper.handle(bean.getClass());
        }
        return bean;
    }


}
package cn.icframework.dber;

import cn.icframework.dber.cnnotation.EnableEntityDDL;
import cn.icframework.mybatis.annotation.Table;
import cn.icframework.core.utils.Assert;
import cn.icframework.mybatis.mapper.BasicMapper;
import lombok.AllArgsConstructor;
import org.apache.ibatis.binding.MapperProxy;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Map;
import cn.icframework.dber.utils.DDLHashCacheUtils;
import java.security.MessageDigest;


/**
 * 后置处理器：初始化前后进行处理工作
 *
 * @author hzl
 * @since 2023/6/28
 */
@Component
@AllArgsConstructor
public class IcTableBeanPostProcessor implements BeanPostProcessor {

    private final DDLHelper ddlHelper;

    private final ApplicationContext context;

    private static Boolean enableEntityDDL = null;

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        if (!getEnableEntityDDL()) {
            return bean;
        }
        if (bean instanceof BasicMapper<?>) {
            try {
                // 处理实体生成ddl
                MapperProxy<?> mapperProxy = (MapperProxy<?>) Proxy.getInvocationHandler(bean);
                Field f = mapperProxy.getClass().getDeclaredField("mapperInterface");
                f.setAccessible(true);
                Class<?> c = (Class<?>) f.get(mapperProxy);
                Type entityType = ((ParameterizedType) (c.getGenericInterfaces()[0])).getActualTypeArguments()[0];
                Table table = ((Class<?>) entityType).getAnnotation(Table.class);
                if (table != null && table.autoDDL()) {
                    // 1. 计算实体结构hash（将所有字段名和类型拼接后做MD5，唯一标识结构变化）
                    String entityHash = calcEntityHash((Class<?>) entityType);
                    // 2. 从本地缓存读取上次的hash
                    String cachedHash = DDLHashCacheUtils.getHash(((Class<?>) entityType).getName());
                    // 3. 如果hash一致，说明结构未变，跳过DDL校验
                    if (entityHash.equals(cachedHash)) {
                        // hash一致，跳过DDL
                        return bean;
                    }
                    // 4. 执行ddl（表结构同步）
                    try {
                        ddlHelper.runDDL((Class<?>) entityType);
                        // 5. 更新本地缓存hash
                        DDLHashCacheUtils.setHash(((Class<?>) entityType).getName(), entityHash);
                    } catch (SQLException e) {
                        SQLException sqlException = new SQLException(((Class<?>) entityType).getName() + " DDL执行出错:" + e.getMessage());
                        sqlException.setStackTrace(e.getStackTrace());
                        throw sqlException;
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return bean;
    }

    /**
     * 获取是否启用实体DDL
     */
    private boolean getEnableEntityDDL() {
        if (enableEntityDDL != null) {
            return enableEntityDDL;
        }
        Map<String, Object> entityDdlAnnotationMap = context.getBeansWithAnnotation(EnableEntityDDL.class);
        if (entityDdlAnnotationMap.isEmpty()) {
            // 从配置文件中读取是否启用实体DDL
            String enableEntityDDLStr = context.getEnvironment().getProperty("ic.framework.dber.enable-entity-ddl");
            enableEntityDDL = !StringUtils.hasLength(enableEntityDDLStr) || Boolean.parseBoolean(enableEntityDDLStr);
            return enableEntityDDL;
        }
        Assert.isTrue(entityDdlAnnotationMap.size() == 1, "EnableEntityDDL 注解只能有一个");
        Object object = entityDdlAnnotationMap.get(entityDdlAnnotationMap.keySet().iterator().next());
        enableEntityDDL = ((Class<?>) object.getClass().getGenericSuperclass()).getAnnotation(EnableEntityDDL.class).enable();
        return enableEntityDDL;
    }

    /**
     * 计算实体类结构的唯一hash值（字段名+类型，可扩展加注解等）
     * 用于判断结构是否发生变化，决定是否需要执行DDL
     */
    private String calcEntityHash(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        for (Field field : clazz.getDeclaredFields()) {
            sb.append(field.getName()).append(":").append(field.getType().getName());
            // 可加注解等
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(sb.toString().getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
package cn.icframework.dber;

import cn.icframework.dber.cnnotation.EnableEntityDDL;
import cn.icframework.mybatis.annotation.ForeignKey;
import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.annotation.TableField;
import cn.icframework.mybatis.annotation.Version;
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
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
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
                    // 1. 计算实体结构hash（字段和DDL相关注解变化都会触发同步）
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
                        // 5. 延迟到所有DDL执行成功后再更新hash，避免外键等延后SQL失败后被缓存跳过
                        ddlHelper.afterRunSuccess(() -> DDLHashCacheUtils.setHash(((Class<?>) entityType).getName(), entityHash));
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
     * 计算实体类结构的唯一hash值。
     * 字段名、字段类型和DDL相关注解都会影响最终结果，避免外键引用表等注解变化被缓存跳过。
     * 用于判断结构是否发生变化，决定是否需要执行DDL
     */
    private String calcEntityHash(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        appendClassDdlMeta(sb, clazz);
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            Field[] fields = current.getDeclaredFields();
            Arrays.sort(fields, Comparator.comparing(Field::getName));
            for (Field field : fields) {
                appendFieldDdlMeta(sb, field);
            }
            current = current.getSuperclass();
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void appendClassDdlMeta(StringBuilder sb, Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null) {
            return;
        }
        sb.append("table{")
                .append("value=").append(table.value())
                .append(",schema=").append(table.schema())
                .append(",camelToUnderline=").append(table.camelToUnderline())
                .append(",comment=").append(table.comment())
                .append(",autoDDL=").append(table.autoDDL())
                .append('}');
    }

    private void appendFieldDdlMeta(StringBuilder sb, Field field) {
        sb.append("field{")
                .append("name=").append(field.getName())
                .append(",type=").append(field.getType().getName());

        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null) {
            sb.append(",tableField[")
                    .append("value=").append(tableField.value())
                    .append(",onInsertValue=").append(tableField.onInsertValue())
                    .append(",onUpdateValue=").append(tableField.onUpdateValue())
                    .append(",type=").append(tableField.type())
                    .append(",comment=").append(tableField.comment())
                    .append(",notNull=").append(tableField.notNull())
                    .append(",length=").append(tableField.length())
                    .append(",fraction=").append(tableField.fraction())
                    .append(",defaultValue=").append(tableField.defaultValue())
                    .append(']');
        }

        Id id = field.getAnnotation(Id.class);
        if (id != null) {
            sb.append(",id[").append("idType=").append(id.idType()).append(']');
        }

        Version version = field.getAnnotation(Version.class);
        if (version != null) {
            sb.append(",version");
        }

        ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
        if (foreignKey != null) {
            sb.append(",foreignKey[")
                    .append("name=").append(foreignKey.name())
                    .append(",references=").append(foreignKey.references().getName())
                    .append(",referencesColumn=").append(foreignKey.referencesColumn())
                    .append(",onDelete=").append(foreignKey.onDelete())
                    .append(",onUpdate=").append(foreignKey.onUpdate())
                    .append(']');
        }
        sb.append('}');
    }
}

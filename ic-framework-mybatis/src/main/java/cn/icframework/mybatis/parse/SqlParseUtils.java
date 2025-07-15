package cn.icframework.mybatis.parse;

import cn.icframework.common.interfaces.IEnum;
import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.annotation.TableField;
import cn.icframework.mybatis.consts.IcParamsConsts;
import cn.icframework.mybatis.consts.IdType;
import cn.icframework.mybatis.utils.ModelClassUtils;
import cn.icframework.mybatis.wrapper.UpdateWrapper;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * SqlParseUtils 工具类，提供 SQL 解析和生成相关的辅助方法。
 * <p>
 * 主要用于处理实体类与 SQL 语句之间的映射、字段校验、主键条件生成、更新语句片段处理等。
 * 
 */
public class SqlParseUtils {
    /**
     * 校验字段是否可用于 SQL 生成。
     * <p>
     * 过滤掉主键自增、无注解等不应参与 SQL 生成的字段。
     * 
     *
     * @param field TableFieldInfo 字段信息封装对象
     * @return 字段名（可用于 SQL），若不可用则返回 null
     */
    public static String verifyField(Table table,TableFieldInfo field) {
        String fieldName;
        TableField tableField = field.getTableField();
        Id id = field.getId();
        if (tableField == null && id == null) {
            return null;
        }
        if (id != null && id.idType() == IdType.AUTO) {
            return null;
        }
        return ModelClassUtils.getColumnName(table, tableField, field.getField().getName());
    }

    /**
     * 获取 Mapper 泛型中的实体类型。
     * <p>
     * 递归查找 Mapper 接口的泛型参数，获取实体类的 Class 类型。
     * 
     *
     * @param mapperClass Mapper 类对象
     * @return 实体类的 Class 类型，若未找到则返回 null
     */
    public static Class<?> getEntityClass(Class<?> mapperClass) {
        if (mapperClass == null || mapperClass == Object.class) {
            return null;
        }
        Type[] genericInterfaces = mapperClass.getGenericInterfaces();
        if (genericInterfaces.length == 1) {
            Type type = genericInterfaces[0];
            if (type instanceof ParameterizedType) {
                Type actualTypeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
                return actualTypeArgument instanceof Class ? (Class<?>) actualTypeArgument : null;
            } else if (type instanceof Class) {
                return getEntityClass((Class<?>) type);
            }
        }
        return getEntityClass(mapperClass.getSuperclass());
    }

    /**
     * 构造主键 in (...) 的 where 条件 SQL 片段。
     * <p>
     * 用于批量操作时生成主键 in 集合的 SQL 片段，结合 MyBatis foreach 使用。
     * 
     *
     * @param entityClass 实体类的 Class 类型
     * @return 主键 in (...) 的 SQL 片段字符串
     * @throws IllegalArgumentException 如果实体未指定主键 @Id 注解
     */
    public static String getWhereIdIn(Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        Field idField = ModelClassUtils.getIdField(entityClass);
        String idFieldName = ModelClassUtils.getTableColumnName(table, idField);
        Assert.isTrue(StringUtils.hasLength(idFieldName), "实体未指定主键 @Id");
        return String.format("""
                %s IN
                <foreach collection="%s" index="index" item="item" open="(" separator="," close=")">
                        #{item}
                </foreach>
                """, idFieldName, IcParamsConsts.PARAMETER_PRIMARY_KEYS);
    }

    /**
     * 处理更新 SQL 的 set 片段。
     * <p>
     * 根据字段类型、注解、是否允许 null、是否为枚举等，自动生成 set 语句片段。
     * 支持 onUpdateValue、枚举类型、主键过滤、自定义参数等。
     * 
     *
     * @param entity   实体对象
     * @param withNull 是否包含 null 值字段
     * @param field    字段信息封装对象
     * @param sql      UpdateWrapper SQL 构建器
     */
    public static void handlerUpdateSet(Object entity, boolean withNull, TableFieldInfo field, UpdateWrapper sql) {
        Table table = entity.getClass().getAnnotation(Table.class);
        String fieldName = ModelClassUtils.getTableColumnName(table, field.getField());
        if (fieldName == null) {
            return;
        }
        TableField tableField = field.getTableField();
        if (tableField != null && StringUtils.hasLength(tableField.onUpdateValue())) {
            sql.SET_SOURCE_PARAM(fieldName, tableField.onUpdateValue());
            return;
        }

        if (!withNull) {
            try {
                field.getField().setAccessible(true);
                if (field.getField().get(entity) == null) {
                    return;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        Id id = field.getId();
        if (id != null) {
            return;
        }

        if (IEnum.class.isAssignableFrom(field.getField().getType())) {
            try {
                field.getField().setAccessible(true);
                IEnum iEnum = (IEnum) field.getField().get(entity);
                if (iEnum == null) {
                    sql.SET_SOURCE_PARAM(fieldName, String.format("#{%s.%s}", IcParamsConsts.PARAMETER_ENTITY, field.getField().getName()));
                } else {
                    sql.SET_SOURCE_PARAM(fieldName, iEnum.code());
                }
            } catch (IllegalAccessException e) {
                sql.SET_SOURCE_PARAM(fieldName, String.format("#{%s.%s}", IcParamsConsts.PARAMETER_ENTITY, field.getField().getName()));
            }
        } else {
            sql.SET_SOURCE_PARAM(fieldName, String.format("#{%s.%s}", IcParamsConsts.PARAMETER_ENTITY, field.getField().getName()));
        }
    }
} 
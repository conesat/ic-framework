package cn.icframework.mybatis.parse;

import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.annotation.TableField;
import cn.icframework.common.interfaces.IEnum;
import cn.icframework.mybatis.consts.IcParamsConsts;
import cn.icframework.mybatis.utils.ModelClassUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * InsertSqlParse 用于生成实体对象的插入SQL语句。
 * <p>
 * 该类实现了ISqlParse接口，主要负责根据实体对象生成单条和批量插入的SQL语句，
 * 支持自定义插入值、枚举类型处理等。
 * 
 */
public class InsertSqlParse implements ISqlParse {
    /**
     * 解析单个实体对象，生成对应的INSERT SQL语句。
     *
     * @param entity 需要插入的实体对象，不能为空
     * @return 生成的SQL语句字符串，包含MyBatis动态SQL标签
     */
    @Override
    public String parseInsert(@NonNull Object entity) {
        // columns用于存储字段名，values用于存储对应的值表达式
        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();
        // 获取表名
        String tableName = ModelClassUtils.getTableName(entity.getClass());
        Table table = entity.getClass().getAnnotation(Table.class);
        // 遍历实体字段，处理每个字段的插入逻辑
        ModelClassUtils.handleTableFieldInfo(entity.getClass(), field -> {
            // 校验字段是否合法，获取数据库字段名
            String fieldName = SqlParseUtils.verifyField(table, field);
            if (fieldName == null) {
                return true;
            }
            // 处理@TableField注解的onInsertValue属性（如有）
            TableField tableField = field.getTableField();
            if (tableField != null && StringUtils.hasLength(tableField.onInsertValue())) {
                columns.add(fieldName);
                values.add(tableField.onInsertValue());
                return true;
            }
            // 反射获取字段值
            field.getField().setAccessible(true);
            Object object;
            try {
                object = field.getField().get(entity);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            // 枚举类型特殊处理，取其code值
            if (IEnum.class.isAssignableFrom(field.getField().getType())) {
                IEnum iEnum = (IEnum) object;
                if (iEnum == null) {
                    values.add(String.format("#{%s.%s}", IcParamsConsts.PARAMETER_ENTITY, field.getField().getName()));
                } else {
                    values.add(String.valueOf(iEnum.code()));
                }
                columns.add(fieldName);
            } else if (object != null) {
                columns.add(fieldName);
                values.add(String.format("#{%s.%s}", IcParamsConsts.PARAMETER_ENTITY, field.getField().getName()));
            }
            return true;
        });
        // 组装最终SQL
        return """
                <script>
                   INSERT INTO %tableName (`%columns`) VALUES (%values)
                </script>
                """
                .replaceAll("%tableName", tableName)
                .replaceAll("%columns", String.join("`,`", columns))
                .replaceAll("%values", String.join(",", values));
    }

    /**
     * 解析实体对象集合，生成批量插入的INSERT SQL语句。
     *
     * @param entities 需要批量插入的实体对象集合，不能为空且至少包含一个元素
     * @return 生成的批量插入SQL语句，包含MyBatis动态SQL标签和foreach
     */
    @Override
    public String parseInsertBatch(List<?> entities) {
        // 取第一个实体对象用于字段分析
        Object entity = entities.getFirst();
        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();
        String tableName = ModelClassUtils.getTableName(entity.getClass());
        Table table = entity.getClass().getAnnotation(Table.class);
        // 遍历字段，组装列名和批量插入的值表达式
        ModelClassUtils.handleTableFieldInfo(entity.getClass(), field -> {
            String fieldName = SqlParseUtils.verifyField(table, field);
            if (fieldName == null) {
                return true;
            }
            columns.add(fieldName);
            TableField tableField = field.getTableField();
            if (tableField != null && StringUtils.hasLength(tableField.onInsertValue())) {
                values.add(tableField.onInsertValue());
                return true;
            }
            // 枚举类型特殊处理
            if (IEnum.class.isAssignableFrom(field.getField().getType())) {
                try {
                    field.getField().setAccessible(true);
                    IEnum iEnum = (IEnum) field.getField().get(entity);
                    if (iEnum == null) {
                        values.add("#{item." + field.getField().getName() + "}");
                    } else {
                        values.add(String.valueOf(iEnum.code()));
                    }
                } catch (IllegalAccessException e) {
                    values.add("#{item." + field.getField().getName() + "}");
                }
            } else {
                values.add("#{item." + field.getField().getName() + "}");
            }
            return true;
        });
        // 组装批量插入SQL，使用MyBatis foreach标签
        return """
                <script>
                   INSERT INTO %tableName (`%columns`) VALUES
                   <foreach collection="%arrays" separator="," index="index" item="item">
                        (%values)
                   </foreach>
                </script>
                """
                .replaceAll("%tableName", tableName)
                .replaceAll("%columns", String.join("`,`", columns))
                .replaceAll("%arrays", IcParamsConsts.PARAMETER_ENTITY)
                .replaceAll("%values", String.join(",", values));
    }

    /**
     * 不支持的操作，抛出异常。
     *
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseUpdateById(Object entity, boolean withNull) {
        throw new UnsupportedOperationException();
    }

    /**
     * 不支持的操作，抛出异常。
     *
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseUpdate(Object entity, java.util.Map<String, Object> params, boolean withNull) {
        throw new UnsupportedOperationException();
    }

    /**
     * 不支持的操作，抛出异常。
     *
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseSelectOneById(Class<?> mapperType) {
        throw new UnsupportedOperationException();
    }

    /**
     * 不支持的操作，抛出异常。
     *
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseSelectByIds(Class<?> mapperType) {
        throw new UnsupportedOperationException();
    }

    /**
     * 不支持的操作，抛出异常。
     *
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseDeleteByIds(Class<?> mapperType) {
        throw new UnsupportedOperationException();
    }

    /**
     * 不支持的操作，抛出异常。
     *
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseDeleteById(Class<?> mapperType) {
        throw new UnsupportedOperationException();
    }
} 
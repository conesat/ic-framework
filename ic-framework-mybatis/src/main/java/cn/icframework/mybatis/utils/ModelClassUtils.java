package cn.icframework.mybatis.utils;

import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.annotation.TableField;
import cn.icframework.mybatis.consts.IcParamsConsts;
import cn.icframework.mybatis.parse.TableFieldInfo;
import cn.icframework.mybatis.consts.CompareEnum;
import cn.icframework.mybatis.wrapper.SqlWrapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelClassUtils {


    /**
     * 构造多主键  where 条件
     *
     * @param field
     * @param sql
     */
    public static void mutilateWhereKey(Table table, TableFieldInfo field, SqlWrapper sql) {
        Id id = field.getId();
        if (id != null) {
            sql.WHERE(getTableColumnName(table, field.getField()) + CompareEnum.EQ.getKey(sql.isCoverXml()) + String.format("#{%s.%s}", IcParamsConsts.PARAMETER_ENTITY, field.getField().getName()));
        }
    }


    /**
     * 获取字段名
     *
     * @param field
     * @return
     */
    public static String getTableColumnName(Table table, Field field) {
        if (field == null) {
            return null;
        }
        TableField tableField = field.getDeclaredAnnotation(TableField.class);
        Id id = field.getDeclaredAnnotation(Id.class);
        if (tableField == null && id == null) {
            return null;
        }
        return getColumnName(table, tableField, field.getName());
    }

    /**
     * 获取数据库列名 自动转换大消息
     *
     * @return 数据库列名
     */
    public static String getTableColumnName(Field field) {
        return getColumnName(field.getDeclaringClass().getAnnotation(Table.class), field.getDeclaredAnnotation(TableField.class), field.getName());
    }

    /**
     * 获取数据库列名 自动转换大消息
     *
     * @param tableField 表字段注解
     * @param fieldName  字段名
     * @return 数据库列名
     */
    public static String getColumnName(Table table, TableField tableField, String fieldName) {
        if (tableField != null && StringUtils.hasLength(tableField.value())) {
            return tableField.value();
        }
        if (table != null && table.camelToUnderline()) {
            return FieldUtils.luCaseToUnderLine(fieldName);
        }
        return fieldName;
    }

    /**
     * 获取表名
     *
     * @param aClass
     * @return
     */
    public static String getTableName(Class<?> aClass) {
        Table table = aClass.getDeclaredAnnotation(Table.class);
        if (table == null || !StringUtils.hasLength(table.value())) {
            return FieldUtils.luCaseToUnderLine(aClass.getSimpleName());
        } else {
            String schema = table.schema();
            if (StringUtils.hasLength(schema)) {
                return schema + "." + table.value();
            } else {
                return table.value();
            }
        }
    }

    /**
     * 判断是否基本类型
     *
     * @param type 需要判断的类
     * @return true 表示是基本类型，false 表示不是基本类型
     */
    public static boolean isDefaultType(Class<?> type) {
        return type.equals(int.class)
                || type.equals(long.class)
                || type.equals(double.class)
                || type.equals(float.class)
                || type.equals(boolean.class)
                || type.equals(char.class)
                || type.equals(Integer.class)
                || type.equals(Long.class)
                || type.equals(Double.class)
                || type.equals(Float.class)
                || type.equals(Boolean.class)
                || type.equals(Character.class)
                || type.equals(String.class);
    }

    /**
     * 获取对象字段。
     *
     * @param clazz     要检查的类
     * @param fieldName 需要查找的字段名
     */
    public static Field getDeclaredField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (fieldName.equals(declaredField.getName())) {
                    return declaredField;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    /**
     * 获取对象字段。
     *
     * @param clazz      要检查的类
     * @param methodName 需要查找的字段名
     */
    public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... args) {
        while (clazz != null) {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                if (methodName.equals(method.getName())
                        && arrayContentsEq(args, method.getParameterTypes())
                ) {
                    return method;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    /**
     * 获取对象字段set方法。
     *
     * @param clazz     要检查的类
     * @param fieldName 需要查找的字段名
     */
    public static Method getSetMethod(Class<?> clazz, String fieldName) {
        String methodName = "set" + StringUtils.capitalize(fieldName);
        while (clazz != null) {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                if (methodName.equals(method.getName())
                        && method.getParameterTypes().length == 1
                ) {
                    return method;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }


    /**
     * 获取对象所有字段
     *
     * @param clazz
     */
    public static List<Field> getDeclaredFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Set<String> fieldNames = new HashSet<>();
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (fieldNames.contains(declaredField.getName())) {
                    continue;
                }
                fieldNames.add(declaredField.getName());
                fields.add(declaredField);
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }


    /**
     * 获取类及其所有父类中声明的所有字段。
     *
     * @param clazz                 要检查的类
     * @param declaredFieldCallback 每获取一个字段就回调一次
     */
    public static void handleAllDeclaredFields(Class<?> clazz, DeclaredFieldCallback declaredFieldCallback) {
        Set<String> fieldNames = new HashSet<>();
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (fieldNames.contains(declaredField.getName())) {
                    continue;
                }
                if (!declaredFieldCallback.callBack(declaredField)) {
                    return;
                }
                fieldNames.add(declaredField.getName());
            }
            clazz = clazz.getSuperclass();
        }
    }


    /**
     * 获取类及其所有父类中声明的所有字段。
     *
     * @param clazz 要检查的类
     */
    public static <T extends Annotation> T getDeclaredAnnotation(Class<?> clazz, Class<T> annotation) {
        while (clazz != null) {
            T declaredAnnotation = clazz.getDeclaredAnnotation(annotation);
            if (declaredAnnotation != null) {
                return declaredAnnotation;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }


    /**
     * 获取带指定注解的字段
     *
     * @param clazz      要检查的类
     * @param annotation 需要获取的注解
     */
    public static List<Field> getFiledByAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Field> fields = new ArrayList<>();
        handleAllDeclaredFields(clazz, (r) -> {
            if (r.isAnnotationPresent(annotation)) {
                fields.add(r);
            }
            return true;
        });
        return fields;
    }

    /**
     * 获取带指定注解的字段
     *
     * @param clazz      要检查的类
     * @param annotation 需要获取的注解
     */
    public static <T extends Annotation> List<FieldAndAnnotation<T>> getFiledAnnotations(Class<?> clazz, Class<T> annotation) {
        List<FieldAndAnnotation<T>> fields = new ArrayList<>();
        handleAllDeclaredFields(clazz, (r) -> {
            T declaredAnnotation = r.getDeclaredAnnotation(annotation);
            if (declaredAnnotation != null) {
                FieldAndAnnotation<T> fieldAndAnnotation = new FieldAndAnnotation<>();
                fieldAndAnnotation.setAnnotation(declaredAnnotation);
                fieldAndAnnotation.setField(r);
                fields.add(fieldAndAnnotation);
            }
            return true;
        });
        return fields;
    }

    /**
     * 获取实体主键。
     *
     * @param clazz 实体类
     */
    public static Field getIdField(Class<?> clazz) {
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                Id id = field.getDeclaredAnnotation(Id.class);
                if (id != null) {
                    return field;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }


    /**
     * 获取id值
     *
     * @param obj
     * @return
     */
    public static Object getIdValue(Object obj) {
        Field idField = getIdField(obj.getClass());
        if (idField == null) {
            return null;
        }
        idField.setAccessible(true);
        try {
            return idField.get(obj);
        } catch (IllegalAccessException e) {
            return null;
        }
    }


    /**
     * 获取逻辑删除字段
     *
     * @param clazz
     * @return
     */
    public static Field getLogicDelete(Class<?> clazz) {
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                TableField tableField = field.getDeclaredAnnotation(TableField.class);
                if (tableField != null && tableField.isLogicDelete()) {
                    return field;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    /**
     * 获取类及其所有父类中声明的所有字段。
     *
     * @param clazz    要检查的类
     * @param callback 每获取一个字段就回调一次
     */
    public static void handleTableFieldInfo(Class<?> clazz, TableFieldInfoCallback callback) {
        Set<String> fieldNames = new HashSet<>();
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                if (fieldNames.contains(field.getName())) {
                    continue;
                }
                TableField tableField = field.getDeclaredAnnotation(TableField.class);
                Id id = field.getDeclaredAnnotation(Id.class);
                if (tableField == null && id == null) {
                    continue;
                }
                TableFieldInfo tableFieldInfo = new TableFieldInfo();
                tableFieldInfo.setTableColumnName(tableField == null || org.apache.commons.lang3.StringUtils.isEmpty(tableField.value()) ? field.getName() : tableField.value());
                tableFieldInfo.setTableField(tableField);
                tableFieldInfo.setId(id);
                tableFieldInfo.setField(field);
                if (!callback.callBack(tableFieldInfo)) {
                    return;
                }
                fieldNames.add(field.getName());
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * 通过无参构造方法构建对象
     *
     * @param rType
     * @param <R>
     * @return
     */
    public static <R> R constructor(Class<R> rType) {
        R r = null;
        Constructor<R> declaredConstructor = null;
        try {
            declaredConstructor = rType.getDeclaredConstructor();
            r = declaredConstructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            if (declaredConstructor != null) {
                declaredConstructor.setAccessible(true);
                try {
                    r = declaredConstructor.newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                }
            }
        }
        Assert.notNull(r, rType.getSimpleName() + "类没有无参构造方法");
        return r;
    }

    /**
     * 回调接口
     */
    public interface DeclaredFieldCallback {
        /**
         * 回调方法
         *
         * @param field
         * @return true 继续遍历 false终止
         */
        boolean callBack(Field field);
    }

    /**
     * 回调接口
     */
    public interface TableFieldInfoCallback {
        /**
         * 回调方法
         *
         * @param field
         * @return true 继续遍历 false终止
         */
        boolean callBack(TableFieldInfo field);
    }


    private static boolean arrayContentsEq(Object[] a1, Object[] a2) {
        if (a1 == null) {
            return a2 == null || a2.length == 0;
        }

        if (a2 == null) {
            return a1.length == 0;
        }

        if (a1.length != a2.length) {
            return false;
        }

        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i]) {
                return false;
            }
        }

        return true;
    }

    @Getter
    @Setter
    public static class FieldAndAnnotation<T extends Annotation> {
        private Field field;
        private T annotation;
    }
}

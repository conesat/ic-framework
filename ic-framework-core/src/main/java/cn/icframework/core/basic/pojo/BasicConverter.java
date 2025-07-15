package cn.icframework.core.basic.pojo;

import cn.icframework.common.interfaces.IEnum;
import cn.icframework.core.common.bean.PageResponse;
import cn.icframework.core.utils.BeanUtils;
import cn.icframework.mybatis.utils.ModelClassUtils;
import lombok.AllArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * dto转换基类
 * <p>
 * 提供实体与VO对象的通用转换能力，支持枚举字段自动映射。
 *
 * @param <T> 实体类型
 * @param <V> VO类型
 * @author hzl
 * @since 2021-06-04  17:52:00
 */
public abstract class BasicConverter<T, V> {
    private final Class<V> vClass;

    private List<IEnumConvert> iEnumConverts;

    /**
     * 构造方法，初始化枚举字段映射
     */
    @SuppressWarnings("unchecked")
    public BasicConverter() {
        Type[] actualTypeArguments = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        Class<T> tClass = ((Class<T>) actualTypeArguments[0]);
        vClass = ((Class<V>) actualTypeArguments[1]);

        // 判断原对象中是否有枚举，如果有枚举的话映射到对应的VO
        for (Field declaredField : ModelClassUtils.getDeclaredFields(tClass)) {
            Class<?> type = declaredField.getType();
            if (!type.isEnum() || !IEnum.class.isAssignableFrom(type)) {
                continue;
            }
            if (iEnumConverts == null) {
                iEnumConverts = new ArrayList<>();
            }
            Field voCode = null;
            try {
                voCode = vClass.getDeclaredField(declaredField.getName());
                if (voCode.getType() != int.class &&
                        voCode.getType() != Integer.class) {
                    continue;
                }
            } catch (NoSuchFieldException ignored) {}
            if (voCode != null) {
                declaredField.setAccessible(true);
                voCode.setAccessible(true);
                IEnumConvert iEnumConvert = new IEnumConvert(declaredField, voCode);
                iEnumConverts.add(iEnumConvert);
            }
        }
    }

    /**
     * 重写这个方法用于自定义构建dto
     *
     * @param vo 目标VO对象
     * @param entity 源实体对象
     */
    protected void convert(V vo, T entity) {
    }

    /**
     * 重写这个方法可以批量处理转换后的dto
     *
     * @param list VO对象列表
     */
    protected void enhance(List<V> list) {
    }

    /**
     * 单个对象转换
     *
     * @param entity 源实体对象
     * @return VO对象
     */
    final public V convert(T entity) {
        if (entity == null) {
            return null;
        } else {
            V v = doConvert(entity);
            this.enhance(Collections.singletonList(v));
            return v;
        }
    }

    /**
     * 列表对象转换
     *
     * @param entities 实体对象列表
     * @return VO对象列表
     */
    final public List<V> convert(List<T> entities) {
        if (entities == null) {
            return null;
        }
        if (entities.isEmpty()) {
            return Collections.emptyList();
        }
        List<V> collect = entities.stream().map(this::doConvert).collect(Collectors.toList());
        if (!collect.isEmpty()) {
            this.enhance(collect);
        }
        return collect;
    }

    /**
     * 分页对象转换
     *
     * @param page 分页实体对象
     * @return 分页VO对象
     */
    final public PageResponse<V> convert(PageResponse<T> page) {
        PageResponse<V> dPage = new PageResponse<>();
        dPage.setIndex(page.getIndex());
        dPage.setSize(page.getSize());
        dPage.setPages(page.getPages());
        dPage.setTotal(page.getTotal());
        dPage.setRecords(convert(page.getRecords()));
        return dPage;
    }

    /**
     * 复制属性
     *
     * @param source 源对象
     * @param target 目标对象
     */
    final public void copyBean(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
    }

    /**
     * 实际转换逻辑
     *
     * @param t 源实体对象
     * @return VO对象
     */
    private V doConvert(T t) {
        try {
            V v = vClass.getDeclaredConstructor().newInstance();
            copyBean(t, v);
            if (iEnumConverts != null) {
                for (IEnumConvert iEnumConvert : iEnumConverts) {
                    IEnum object = (IEnum) iEnumConvert.field.get(t);
                    if (object == null) {
                        continue;
                    }
                    if (iEnumConvert.voCode != null) {
                        iEnumConvert.voCode.set(v, object.code());
                    }
                }
            }
            convert(v, t);
            return v;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 枚举字段映射内部类
     */
    @AllArgsConstructor
    static class IEnumConvert {
        /** 实体字段 */
        private Field field;
        /** VO字段 */
        private Field voCode;
    }
}

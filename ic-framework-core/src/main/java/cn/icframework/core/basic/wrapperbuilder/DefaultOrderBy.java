package cn.icframework.core.basic.wrapperbuilder;

import cn.icframework.mybatis.query.QueryField;
import lombok.Getter;
import lombok.Setter;

/**
 * 默认排序对象
 *
 * 用于描述排序字段及排序类型。
 *
 * @author hzl
 * @since 2023/6/28
 */
@Getter
@Setter
public class DefaultOrderBy {
    /**
     * 默认排序类型
     */
    public enum OrderType {
        /**
         * 升序
         */
        ASC,
        /**
         * 降序
         */
        DESC
    }

    /**
     * 排序字段
     */
    private String field;
    /**
     * 排序类型
     */
    private OrderType orderType;

    /**
     * 构造方法
     * @param field 排序字段
     * @param orderType 排序类型
     */
    public DefaultOrderBy(String field, OrderType orderType) {
        this.field = field;
        this.orderType = orderType;
    }

    /**
     * 构造方法
     * @param field 排序字段（QueryField）
     * @param orderType 排序类型
     */
    public DefaultOrderBy(QueryField<?> field, OrderType orderType) {
        this.field = field.getTableColumn();
        this.orderType = orderType;
    }

    /**
     * 构建升序排序对象
     * @param field 排序字段（QueryField）
     * @return DefaultOrderBy
     */
    public static DefaultOrderBy orderAsc(QueryField<?> field) {
        return new DefaultOrderBy(field, OrderType.ASC);
    }

    /**
     * 构建降序排序对象
     * @param field 排序字段（QueryField）
     * @return DefaultOrderBy
     */
    public static DefaultOrderBy orderDesc(QueryField<?> field) {
        return new DefaultOrderBy(field, OrderType.DESC);
    }

    /**
     * 构建降序排序对象
     * @param field 排序字段
     * @return DefaultOrderBy
     */
    public static DefaultOrderBy orderDesc(String field) {
        return new DefaultOrderBy(field, OrderType.DESC);
    }

    /**
     * 构建升序排序对象
     * @param field 排序字段
     * @return DefaultOrderBy
     */
    public static DefaultOrderBy orderAsc(String field) {
        return new DefaultOrderBy(field, OrderType.ASC);
    }

}

package cn.icframework.core.basic.wrapperbuilder;

import cn.icframework.mybatis.query.QueryField;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 排序构建器
 *
 * 提供排序相关的构建方法。
 */
@Getter
@Setter
public class OrderBuilder {
    /**
     * 默认构造方法
     */
    public OrderBuilder() {}

    /**
     * 排序条件列表
     */
    List<DefaultOrderBy> orderByList = new ArrayList<>();

    /**
     * 添加升序排序字段
     * @param fields 排序字段
     * @return 当前OrderBuilder
     */
    public OrderBuilder orderAsc(QueryField<?>... fields) {
        for (QueryField<?> field : fields) {
            orderByList.add(DefaultOrderBy.orderAsc(field));
        }
        return this;
    }

    /**
     * 添加降序排序字段
     * @param fields 排序字段
     * @return 当前OrderBuilder
     */
    public OrderBuilder orderDesc(QueryField<?>... fields) {
        for (QueryField<?> field : fields) {
            orderByList.add(DefaultOrderBy.orderDesc(field));
        }
        return this;
    }

    /**
     * 构建排序条件列表
     * @return 排序条件列表
     */
    public List<DefaultOrderBy> build() {
        return orderByList;
    }
}

package cn.icframework.core.basic.wrapperbuilder;

import cn.icframework.common.consts.RequestValue;
import cn.icframework.core.common.bean.OrderItem;
import cn.icframework.core.common.consts.ParamsConst;
import cn.icframework.mybatis.query.QueryField;
import cn.icframework.mybatis.query.QueryTable;
import cn.icframework.mybatis.wrapper.SqlWrapper;
import cn.icframework.mybatis.wrapper.Wrapper;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * 通用SQL包装器构建器基类
 * <p>
 * 提供排序、默认排序、参数构建等通用能力，供业务继承扩展。
 *
 * @param <T> 表定义类型，需继承QueryTable
 * @author hzl
 * @since 2023/6/28
 */
public abstract class BasicWrapperBuilder<T extends QueryTable<T>> extends Wrapper {
    T tableDef;

    /**
     * 构造方法
     * @param t 表定义
     */
    public BasicWrapperBuilder(T t) {
        this.tableDef = t;
    }

    /**
     * 排序实现，由子类实现
     * @param orderItem 排序项
     * @param tableDef 表定义
     * @return 排序字段（QueryField&lt;?&gt; 或 String）
     */
    abstract protected Object doSort(OrderItem orderItem, T tableDef);

    /**
     * 默认排序，由子类实现
     * @param tableDef 表定义
     * @return 默认排序列表
     */
    abstract protected List<DefaultOrderBy> defaultSort(T tableDef);

    /**
     * 由业务处理具体构建规则
     * @param params 查询参数
     * @param tableDef 表定义
     * @return SQL包装器
     */
    abstract protected SqlWrapper list(QueryParams params, T tableDef);

    /**
     * 构建SQL包装器，自动处理排序
     * @param params 查询参数
     * @return SQL包装器
     */
    public SqlWrapper build(QueryParams params) {
        SqlWrapper sqlWrapper = list(params, tableDef);
        doSort(sqlWrapper, params.get(ParamsConst.ORDERS));
        return sqlWrapper;
    }

    /**
     * 进行排序
     * @param sqlWrapper SQL包装器
     * @param sort 排序参数
     */
    private void doSort(SqlWrapper sqlWrapper, RequestValue sort) {
        if (sort == null) {
            buildDefaultOrder(sqlWrapper);
            return;
        }
        String sortString = sort.getValue().toString();
        if (!sortString.startsWith("[")) {
            sortString = "[" + sortString + "]";
        }
        List<OrderItem> orderItems = JSONArray.parseArray(sortString, OrderItem.class);
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getSortBy() == null) {
                continue;
            }
            Object field = this.doSort(orderItem, tableDef);

            if (field != null) {
                if (field instanceof QueryField<?> queryField) {
                    sqlWrapper.ORDER_BY(queryField.getTableColumn() + (orderItem.isDescending() ? " DESC" : " ASC"));
                } else {
                    sqlWrapper.ORDER_BY(field + (orderItem.isDescending() ? " DESC" : " ASC"));
                }
            }
        }
        // sql中没有排序条件就会进入排序方法由开发者指定排序字段
        if (!sqlWrapper.hasOrders()) {
            buildDefaultOrder(sqlWrapper);
        }
    }

    /**
     * 默认排序
     * @param sqlWrapper SQL包装器
     */
    private void buildDefaultOrder(SqlWrapper sqlWrapper) {
        List<DefaultOrderBy> defaultOrderBys = defaultSort(tableDef);
        if (defaultOrderBys != null && !defaultOrderBys.isEmpty()) {
            for (DefaultOrderBy defaultOrderBy : defaultOrderBys) {
                sqlWrapper.ORDER_BY(defaultOrderBy.getField() + (defaultOrderBy.getOrderType() == DefaultOrderBy.OrderType.ASC ? " ASC" : " DESC"));
            }
        }
    }
}

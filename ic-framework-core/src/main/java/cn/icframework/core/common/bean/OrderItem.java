package cn.icframework.core.common.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 排序项
 * 用于描述排序字段及顺序。
 */
@Getter
@Setter
public class OrderItem {
    /**
     * 默认构造方法
     */
    public OrderItem() {}
    /**
     * 排序字段
     */
    private String sortBy;
    /**
     * 是否正序
     */
    private boolean descending;
}

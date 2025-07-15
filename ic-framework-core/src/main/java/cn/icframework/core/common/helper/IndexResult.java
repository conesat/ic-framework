package cn.icframework.core.common.helper;

import lombok.Getter;
import lombok.Setter;

/**
 * 索引结果
 *
 * 用于描述索引相关的结果。
 */
@Getter
@Setter
public class IndexResult {
    /**
     * 默认构造方法
     */
    public IndexResult() {}
    /**
     * 索引名称
     */
    private String name;
    /**
     * 索引字段
     */
    private String column;
    /**
     * 索引顺序
     */
    private int index;
    /**
     * 是否唯一索引
     */
    private boolean unique;
}

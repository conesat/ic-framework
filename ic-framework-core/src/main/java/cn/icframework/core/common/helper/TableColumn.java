package cn.icframework.core.common.helper;

import lombok.Getter;
import lombok.Setter;

/**
 * 已有的数据库表字段信息。
 * <p>
 * 用于描述数据库表的字段结构。
 * </p>
 */
@Getter
@Setter
public class TableColumn {
    /**
     * 字段名
     */
    private String field;
    /**
     * 字段类型
     */
    private String type;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 是否非空
     */
    private boolean notNull;
}

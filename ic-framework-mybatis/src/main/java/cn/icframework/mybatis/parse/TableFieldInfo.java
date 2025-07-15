package cn.icframework.mybatis.parse;

import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

/**
 * 表字段信息。
 * 用于存储数据库表字段的相关信息。
 * @author hzl
 */
@Getter
@Setter
public class TableFieldInfo {
    private Id id;
    private TableField tableField;
    private Field field;
    private String tableColumnName;
}

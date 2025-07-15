package cn.icframework.mybatis.parse;

import cn.icframework.mybatis.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

/**
 * 逻辑删除字段信息。
 * 用于存储逻辑删除字段的相关信息。
 * @author hzl
 */
@Getter
@Setter
public class LogicDeleteField {
    private TableField tableField;
    private Field field;
}

package cn.icframework.mybatis.query;

import cn.icframework.mybatis.consts.CompareEnum;
import cn.icframework.mybatis.consts.WhereJoinType;
import cn.icframework.mybatis.wrapper.SelectWrapper;
import cn.icframework.mybatis.wrapper.SqlWrapper;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 查询条件类。
 * 用于构建SQL查询条件。
 * @author hzl
 * @since 2023/5/18
 */
@Getter
@Setter
public class Condition implements Cloneable {
    /**
     * 主表字段
     */
    protected QueryField<?> tableFieldDef;
    /**
     * 另一个表字段
     */
    protected QueryField<?> otherTableFieldDef;
    /**
     * 条件操作符
     */
    protected CompareEnum compare;
    /**
     * 连接符 AND OR
     */
    protected WhereJoinType type;
    /**
     * 嵌套sql
     */
    protected SqlWrapper sqlWrapper;
    /**
     * 嵌套sql
     */
    protected SelectWrapper selectWrapper;
    /**
     * 单个参数
     */
    protected Object value;
    /**
     * 列表参数
     */
    protected List<?> values;


    public static Condition create(QueryField<?> fieldDef, CompareEnum compareEnum, Object value) {
        Condition condition = new Condition();
        condition.setTableFieldDef(fieldDef);
        condition.setCompare(compareEnum);
        if (value != null) {
            switch (value) {
                case QueryField<?> queryField -> condition.setOtherTableFieldDef(queryField);
                case SqlWrapper wrapper -> condition.setSqlWrapper(wrapper);
                case SelectWrapper wrapper -> condition.setSelectWrapper(wrapper);
                default -> condition.setValue(value);
            }
        }
        return condition;
    }

    public static Condition create(WhereJoinType type) {
        Condition condition = new Condition();
        condition.setType(type);
        return condition;
    }

    public static Condition createByList(QueryField<?> fieldDef, CompareEnum compareEnum, List<?> values) {
        Condition condition = new Condition();
        condition.setTableFieldDef(fieldDef);
        condition.setCompare(compareEnum);
        condition.setValues(values);
        return condition;
    }

    // 假设MyObject类有实现clone方法
    @Override
    public Condition clone() throws CloneNotSupportedException {
        return (Condition) super.clone();
    }
}

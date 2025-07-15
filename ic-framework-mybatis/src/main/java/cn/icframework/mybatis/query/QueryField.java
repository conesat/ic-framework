package cn.icframework.mybatis.query;

import cn.icframework.common.lambda.LambdaGetter;
import cn.icframework.common.lambda.LambdaUtils;
import cn.icframework.mybatis.consts.CompareEnum;
import cn.icframework.mybatis.wrapper.SelectWrapper;
import cn.icframework.mybatis.wrapper.SqlWrapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 查询字段类。
 * 用于表示查询中的字段信息。
 * @param <QT> 查询表类型
 * @author hzl
 */
@SuppressWarnings("ALL")
@Getter
@Setter
@NoArgsConstructor
public class QueryField<QT extends QueryTable<?>> {
    /**
     * 数据库字段名
     */
    private String tableColumn;
    /**
     * 数据库别名
     */
    private String asName;
    /**
     * 该字段需要执行的函数
     */
    private String func;
    /**
     * 表
     */
    private QT table;

    public QueryField(QT table, String tableColumn) {
        this.table = table;
        this.tableColumn = tableColumn;
    }

    /**
     * 字段 as
     *
     * @param as
     * @return
     */
    public QueryField<QT> as(String as) {
        this.asName = as;
        return this;
    }

    /**
     * 字段 as
     *
     * @param field
     * @return
     */
    public <T> QueryField as(LambdaGetter<T> field) {
        this.asName = LambdaUtils.getFieldName(field);
        return this;
    }

    /**
     * 字段 =
     *
     * @param val 字段属性或者值
     * @return
     */
    public QT eq(Object val) {
        if (val == null) {
            return table;
        }
        QT qt = cloneQt();
        qt.getWheres().add(Condition.create(this, CompareEnum.EQ, val));
        return qt;
    }

    /**
     * 字段 !=
     *
     * @param val 字段属性或者值
     * @return
     */
    public QT ne(Object val) {
        if (val == null) {
            return table;
        }
        QT qt = cloneQt();
        qt.getWheres().add(Condition.create(this, CompareEnum.NE, val));
        return qt;
    }

    /**
     * 字段 小于等于
     *
     * @param val
     * @return
     */
    public QT le(Object val) {
        if (val == null) {
            return table;
        }
        QT qt = cloneQt();
        qt.getWheres().add(Condition.create(this, CompareEnum.LE, val));
        return qt;
    }

    /**
     * 字段 小于
     *
     * @param val
     * @return
     */
    public QT lt(Object val) {
        if (val == null) {
            return table;
        }
        QT qt = cloneQt();
        qt.getWheres().add(Condition.create(this, CompareEnum.LT, val));
        return qt;
    }

    /**
     * 字段 >=
     *
     * @param val
     * @return
     */
    public QT ge(Object val) {
        if (val == null) {
            return table;
        }
        QT qt = cloneQt();
        qt.getWheres().add(Condition.create(this, CompareEnum.GE, val));
        return qt;
    }

    /**
     * 字段 >
     *
     * @param val
     * @return
     */
    public QT gt(Object val) {
        if (val == null) {
            return table;
        }
        QT qt = cloneQt();
        qt.getWheres().add(Condition.create(this, CompareEnum.GT, val));
        return qt;
    }

    /**
     * 字段 like '%xxx%'
     *
     * @param val
     * @return
     */
    public QT like(Object val) {
        if (val == null) {
            return table;
        }
        QT qt = cloneQt();
        qt.getWheres().add(Condition.create(this, CompareEnum.LIKE, val));
        return qt;
    }

    /**
     * 字段 like '%xxx'
     *
     * @param val
     * @return
     */
    public QT leftLike(Object val) {
        if (val == null) {
            return table;
        }
        QT qt = cloneQt();
        qt.getWheres().add(Condition.create(this, CompareEnum.LEFT_LIKE, val));
        return qt;
    }

    /**
     * 字段 like 'xxx%'
     *
     * @param val
     * @return
     */
    public QT rightLike(Object val) {
        if (val == null) {
            return table;
        }
        QT qt = cloneQt();
        qt.getWheres().add(Condition.create(this, CompareEnum.RIGHT_LIKE, val));
        return qt;
    }

    /**
     * 字段 in
     *
     * @param vals 字段属性或者值
     * @return
     */
    public QT in(List<?> vals) {
        if (vals == null) {
            return table;
        }
        QT qt = cloneQt();
        qt.getWheres().add(Condition.createByList(this, CompareEnum.IN, vals));
        return qt;
    }

    /**
     * 字段 in
     *
     * @param vals 字段属性或者值
     * @return
     */
    public QT in(Object[] vals) {
        if (vals == null) {
            return table;
        }
        QT qt = cloneQt();
        qt.getWheres().add(Condition.createByList(this, CompareEnum.IN, Arrays.stream(vals).toList()));
        return qt;
    }

    /**
     * 字段 in
     *
     * @param sqlWrapper 字段属性或者值
     * @return
     */
    public QT in(SqlWrapper sqlWrapper) {
        if (sqlWrapper == null) {
            return table;
        }
        QT qt = cloneQt();
        qt.getWheres().add(Condition.create(this, CompareEnum.IN, sqlWrapper));
        return qt;
    }

    /**
     * 字段 in
     *
     * @param sqlWrapper 字段属性或者值
     * @return
     */
    public QT in(SelectWrapper sqlWrapper) {
        if (sqlWrapper == null) {
            return table;
        }
        QT qt = cloneQt();
        qt.getWheres().add(Condition.create(this, CompareEnum.IN, sqlWrapper));
        return qt;
    }

    /**
     * 字段 not in
     *
     * @param vals 字段属性或者值
     * @return
     */
    public QT notIn(List<?> vals) {
        if (vals == null) {
            return table;
        }
        QT qt = cloneQt();
        qt.getWheres().add(Condition.createByList(this, CompareEnum.NOT_IN, vals));
        return qt;
    }

    /**
     * 字段 in
     *
     * @param vals 字段属性或者值
     * @return
     */
    public QT notIn(Object[] vals) {
        if (vals == null) {
            return table;
        }
        QT qt = cloneQt();
        qt.getWheres().add(Condition.createByList(this, CompareEnum.NOT_IN, Arrays.stream(vals).toList()));
        return qt;
    }

    /**
     * 字段 in
     *
     * @param sqlWrapper 字段属性或者值
     * @return
     */
    public QT notIn(SqlWrapper sqlWrapper) {
        if (sqlWrapper == null) {
            return table;
        }
        QT qt = cloneQt();
        qt.getWheres().add(Condition.create(this, CompareEnum.NOT_IN, sqlWrapper));
        return qt;
    }

    /**
     * 字段 in
     *
     * @param sqlWrapper 字段属性或者值
     * @return
     */
    public QT notIn(SelectWrapper sqlWrapper) {
        if (sqlWrapper == null) {
            return table;
        }
        QT qt = cloneQt();
        qt.getWheres().add(Condition.create(this, CompareEnum.NOT_IN, sqlWrapper));
        return qt;
    }

    /**
     * 字段非空
     *
     * @return
     */
    public QT notNull() {
        QT qt = cloneQt();
        qt.getWheres().add(Condition.create(this, CompareEnum.IS_NOT_NULL, null));
        return qt;
    }

    /**
     * 字段为空
     *
     * @return
     */
    public QT isNull() {
        QT qt = cloneQt();
        qt.getWheres().add(Condition.create(this, CompareEnum.IS_NULL, null));
        return qt;
    }

    // ++++++++++++赋值+++++++++++++++++++
    public QT set(Object val) {
        QT qt = cloneQt();
        qt.getSets().add(new DataSet(getTableColumn(), val));
        return qt;
    }

    /**
     * 转换
     * @param object
     * @return
     */
    public static QueryField<?> of(Object object) {
        QueryField<QueryTable<?>> queryField = new QueryField<>(null, object.toString());
        return queryField;
    }

    // ++++++++++++++++排序++++++++++++++++++++++++++

    /**
     * 正序排序
     *
     * @return
     */
    public QT asc() {
        QT qt = cloneQt();
        qt.getOrders().add(String.format("%s ASC", getAsNameOrName()));
        return qt;
    }

    /**
     * 倒序排序
     *
     * @return
     */
    public QT desc() {
        QT qt = cloneQt();
        qt.getOrders().add(String.format("%s DESC", getAsNameOrName()));
        return qt;
    }

    public String getAsNameOrName() {
        if (StringUtils.hasLength(asName)) {
            return asName;
        }
        return tableColumn;
    }

    public String getAsNameOrNameWithTable() {
        if (StringUtils.hasLength(asName)) {
            if (table == null) {
                return asName;
            }
            return table.getAsNameOrName() + "." + asName;
        }
        if (table == null) {
            return tableColumn;
        }
        return table.getAsNameOrName() + "." + tableColumn;
    }

    public String getNameWithTable() {
        if (table == null) {
            return tableColumn;
        }
        return table.getAsNameOrName() + "." + tableColumn;
    }

    /**
     * 克隆当前QT对象。
     * <p>
     * 设计说明：
     * <ul>
     *   <li>每次链式调用都会新建一个QT对象，旧对象理论上不再使用。</li>
     *   <li>如果当前table不是root对象，则将table引用置为null，便于GC及时回收内存。</li>
     *   <li>如果是root对象，则清空其wheres、orders、sets集合，避免数据堆积。</li>
     *   <li>新克隆对象的root标记始终为false。</li>
     *   <li>注意：外部请勿再使用旧对象。</li>
     * </ul>
     * <b>线程不安全！</b>
     *
     * @return 克隆的新QT对象
     * @throws IllegalStateException 如果table为null
     */
    private QT cloneQt() {
        if (table == null) {
            throw new IllegalStateException("QueryField.table 为空，无法克隆QT对象！");
        }
        // 通过table的newInstance方法创建新QT对象
        QT newObj = (QT) table.newInstance();
        // 拷贝基本属性（浅拷贝）
        BeanUtils.copyProperties(table, newObj);
        // 拷贝集合属性（浅拷贝List，但元素本身未深拷贝）
        newObj.setWheres(table.getWheres() != null ? table.getWheres().stream().collect(Collectors.toList()) : null);
        newObj.setOrders(table.getOrders() != null ? table.getOrders().stream().collect(Collectors.toList()) : null);
        newObj.setSets(table.getSets() != null ? table.getSets().stream().collect(Collectors.toList()) : null);
        // 释放旧对象引用或清空集合，便于GC
        if (!table.isRoot()) {
            // 非root对象，直接置空，外部请勿再用旧对象
            table = null;
        } else {
            // root对象，清空集合，避免数据堆积
            if (table.getWheres() != null) table.getWheres().clear();
            if (table.getOrders() != null) table.getOrders().clear();
            if (table.getSets() != null) table.getSets().clear();
        }
        // 新对象不是root
        newObj.setRoot(false);
        return newObj;
    }
}

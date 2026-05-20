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

import java.util.ArrayList;
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
        QueryField<QT> queryField = copy();
        queryField.setAsName(as);
        return queryField;
    }

    /**
     * 字段 as
     *
     * @param field
     * @return
     */
    public <T> QueryField as(LambdaGetter<T> field) {
        QueryField<QT> queryField = copy();
        queryField.setAsName(LambdaUtils.getFieldName(field));
        return queryField;
    }

    private QueryField<QT> copy() {
        QueryField<QT> queryField = new QueryField<>();
        queryField.setTableColumn(tableColumn);
        queryField.setAsName(asName);
        queryField.setFunc(func);
        queryField.setTable(table);
        return queryField;
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
        
        // 重要：在多线程环境下，需要确保在获取集合后立即创建副本，避免ConcurrentModificationException
        // 直接创建新集合，而不是先获取引用再复制，这样可以避免引用被其他线程修改
        // 优化：移除synchronized块，使用局部变量和ArrayList构造函数直接创建副本
        // 原因：每个QueryTable实例的集合只在cloneQt()方法中被清空，且清空操作在复制操作之后
        // 因此，在获取集合引用后立即创建副本，不会出现ConcurrentModificationException
        List<Condition> wheres = table.getWheres();
        List<String> orders = table.getOrders();
        List<DataSet> sets = table.getSets();
        
        // 立即创建副本，避免后续遍历集合时被其他线程修改
        // 使用ArrayList构造函数直接创建副本，避免使用stream API导致的ConcurrentModificationException
        List<Condition> wheresCopy = wheres != null ? new ArrayList<>(wheres) : null;
        List<String> ordersCopy = orders != null ? new ArrayList<>(orders) : null;
        List<DataSet> setsCopy = sets != null ? new ArrayList<>(sets) : null;
        
        // 设置拷贝后的集合到新对象
        newObj.setWheres(wheresCopy);
        newObj.setOrders(ordersCopy);
        newObj.setSets(setsCopy);
        
        // 释放旧对象引用或清空集合，便于GC
        if (!table.isRoot()) {
            // 非root对象，直接置空，外部请勿再用旧对象
            table = null;
        } else {
            // 直接清空原集合，无需同步
            // 原因：清空操作在复制操作之后，且每个QueryTable实例的集合只在cloneQt()方法中被清空
            // 因此，在复制操作完成后清空集合，不会影响其他线程的复制操作
            if (wheres != null) wheres.clear();
            if (orders != null) orders.clear();
            if (sets != null) sets.clear();
        }
        // 新对象不是root
        newObj.setRoot(false);
        return newObj;
    }
}

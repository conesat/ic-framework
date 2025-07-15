package cn.icframework.mybatis.query;

import cn.icframework.common.lambda.LambdaGetter;
import cn.icframework.common.lambda.LambdaUtils;
import cn.icframework.mybatis.annotation.Collection;
import cn.icframework.mybatis.consts.WhereJoinType;
import cn.icframework.mybatis.utils.ModelClassUtils;
import cn.icframework.mybatis.wrapper.SqlWrapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询表类。
 * 用于表示查询中的表信息。
 * @param <QT> 查询表类型
 * @author hzl
 */
@Getter
@Setter
@NoArgsConstructor
public class QueryTable<QT extends QueryTable<?>> {
    private String asName;
    private String name;
    private Class<?> tableClass;
    /**
     * 查询条件
     */
    private List<Condition> wheres = new ArrayList<>();

    /**
     * 赋值
     */
    private List<DataSet> sets = new ArrayList<>();

    /**
     * 排序
     */
    private List<String> orders = new ArrayList<>();

    /**
     * 该字段需要执行的函数
     */
    private String func;

    /**
     * 嵌套子查询，是一个sql，没有tableClass
     */
    private SqlWrapper sqlWrapper;

    private boolean isRoot = true;
    // 用于嵌套子条件
    private WhereJoinType whereJoinType; // 嵌套条件连接符
    private QueryTable<?>[] childrenQueryTables; // 子条件

    public QueryTable(WhereJoinType type, QueryTable<?>... queryTables) {
        this.childrenQueryTables = queryTables;
        this.whereJoinType = type;
    }

    @SuppressWarnings("unchecked")
    public QueryTable(Class<?> tableClass) {
        this.tableClass = tableClass;
        this.name = ModelClassUtils.getTableName(tableClass);
    }

    @SuppressWarnings("unchecked")
    public QT or() {
        if (!CollectionUtils.isEmpty(getWheres())) {
            getWheres().add(Condition.create(WhereJoinType.OR));
        }
        return (QT) this;
    }

    @SuppressWarnings("unchecked")
    public QT as(String asName) {
        this.asName = asName;
        return (QT) this;
    }

    /**
     * 字段 as
     *
     * @param lambdaGetter
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> QT as(LambdaGetter<T> lambdaGetter) {
        Field field = LambdaUtils.getField(lambdaGetter);
        Collection collection = field.getDeclaredAnnotation(Collection.class);
        if (collection != null && StringUtils.hasLength(collection.prefix())) {
            this.asName = collection.prefix();
        } else {
            this.asName = field.getName();
        }
        return (QT) this;
    }


    public String getAsNameOrName() {
        if (StringUtils.hasLength(asName)) {
            return asName;
        }
        return name;
    }

    public String getAsNameWrap() {
        if (StringUtils.hasLength(asName)) {
            return name + " " + asName;
        }
        return name;
    }

    @SuppressWarnings("unchecked")
    public QT newInstance() {
        try {
            Constructor<?> constructor = this.getClass().getConstructor(Class.class);
            return (QT) constructor.newInstance(tableClass);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}

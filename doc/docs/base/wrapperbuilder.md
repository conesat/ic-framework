# SqlWrapperBuilder

该类用于构建查询语句。下面是部门的构建示例。

你可能会注意到api中查询page方法，有这么一行代码
```java
SqlWrapper sqlWrapper = deptWrapperBuilder.build(getQueryMap(request));
```
其实就是调用下面的doBuild方法构建查询条件，然后再调用doSort构建排序字段，如果doSort返回null，就会调用defaultSort。最后返回构建完成的sqlWrapper。

```java
package cn.icframework.system.module.dep.wrapperbuilder;

import consts.cn.icframework.common.RequestValue;
import cn.icframework.core.basic.service.BasicWrapperBuilder;
import cn.icframework.core.basic.service.DefaultOrderBy;
import cn.icframework.core.basic.service.OrderBuilder;
import bean.common.cn.icframework.core.OrderItem;
import consts.common.cn.icframework.core.ParamsConst;
import query.cn.icframework.mybatis.QueryField;
import wrapper.cn.icframework.mybatis.SqlWrapper;
import cn.icframework.system.module.dep.def.DeptDef;
import cn.icframework.system.module.dep.pojo.vo.DeptVO;
import cn.icframework.system.module.depuser.DepUser;
import cn.icframework.system.module.depuser.def.DepUserDef;
import cn.icframework.system.module.user.def.UserDef;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * wrapper构建器 作者推荐复杂sql写在这个builder里面
 * @author ic generator
 * @since 2023/07/11
 */
@Component
public class DeptWrapperBuilder extends BasicWrapperBuilder<DeptDef> {
    public DeptWrapperBuilder() {
        super(DeptDef.table());
    }

    /**
     * 遍历查询条件，此方法会在AdminApi调用page时使用，你可以仿照page接口，用于其他接口查询
     * params 是前端传入的所有参数
     * 在这里根据参数和值 去构建SQLWrapper 并返回
     *
     * @param params 前端传入的所有参数
     * @return 构建的SQLWrapper条件
     */
    @Override
    protected SqlWrapper list(Map<String, RequestValue> params, DeptDef def) {
        UserDef userDef = UserDef.table();
        DepUserDef depUserDef = DepUserDef.table();
        // 部门负责人只有一个，所以这里直接join，否则需要实体里面封装list
        SqlWrapper sqlWrapper = SELECT(def._all, userDef.id.as(DeptVO::getLeaderUserId), userDef.name.as(DeptVO::getLeaderUserName))
                .FROM(def)
                .LEFT_JOIN(depUserDef).ON(depUserDef.depId.eq(def.id).manager.eq(true))
                .LEFT_JOIN(userDef).ON(userDef.id.eq(depUserDef.userId));
        params.forEach((key, rv) -> {
            String value = rv.getValue(); // 单个参数
            String[] values = rv.getValues(); // 数组参数、
            switch (key) {
                case ParamsConst.SEARCH_KEY ->
                        sqlWrapper.WHERE(AND(def.name.like(value), OR(), userDef.name.like(value)));
                case "id" -> sqlWrapper.WHERE(def.id.eq(value));
            }
        });
        return sqlWrapper;
    }

    /**
     * 处理排序条件，页面传参时需要将排序条件转成json字符串，key必须是 orders。例如 orders:"[{column:'排序字段或自定义内容', asc:false},{column:'排序字段或自定义内容', asc:true}]"
     *
     * @param orderItem orders已经在BasicWrapperBuilder转成了 OrderItem数组。这里就是遍历每一项排序内容
     * @return 这里只需要返回排序的数据库字段 或者 其他条件内容。倒序还是正序是根据OrderItem判断的
     */
    @Override
    protected QueryField<?> doSort(OrderItem orderItem, DeptDef def) {
        UserDef userDef = UserDef.table();
        return switch (orderItem.getSortBy()) {
            case "sort" -> def.sort;
            case "name" -> def.name;
            case "leaderUserId", "leaderUserName" -> userDef.name;
            case "phone" -> def.phone;
            case "createTime" -> def.createTime;
            default -> null;
        };
    }

    /**
     * 默认排序 当不满足doSort时，会使用这里返回的内容进行排序
     *
     * @return 可以是多个排序条件的数组，按顺序进行排序
     */
    @Override
    protected List<DefaultOrderBy> defaultSort(DeptDef def) {
        return new OrderBuilder().orderAsc(def.sort).orderDesc(def.createTime).build();
    }
}
```
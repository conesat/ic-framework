# IC-Mybatis 复杂查询
>
以下示例是集成 `BasicService` 的查询方式。

这一章重点讲 `ic-mybatis` 在业务里最常见的高级查询能力：子查询、`EXISTS`、多表关联、分组统计、动态条件、子查询分页和复杂排序。

## 先记住几个核心入口

复杂查询基本围绕这几类能力展开：

- `SELECT / SELECT_DISTINCT`
- `FROM / LEFT_JOIN / INNER_JOIN / ON`
- `WHERE / AND / OR`
- `GROUP_BY / ORDER_BY / LIMIT / OFFSET / PAGE`
- `FunctionWrapper` 里的 `COUNT / SUM / AVG / DISTINCT / EXISTS / ANY_VALUE / GROUP_CONCAT / CONCAT`
- `Checks.CHECK` 条件拼装

## 1. 在 select 字段中嵌套子查询

适合“查主表，同时补一个派生字段”的场景。

```java
UserDef userDef = UserDef.table();
UserDepDef userDepDef = UserDepDef.table();

/**
 * select
 *   user.*,
 *   (
 *     select name
 *     from user_dep
 *     where user_dep.id = user.dep_id
 *     limit 1
 *   ) as depName
 * from user
 * where user.id = 1
 */
SqlWrapper sqlWrapper = SELECT(
        userDef,
        SELECT(userDepDef.name)
                .FROM(userDepDef)
                .WHERE(userDepDef.id.eq(userDef.depId))
                .LIMIT(1)
                .AS("depName")
)
        .FROM(userDef)
        .WHERE(userDef.id.eq(1));
```

这类写法很适合：

- 补充名称字段
- 补充最新一条记录
- 补充统计值或状态值

## 2. 在 where 中使用子查询

`in / notIn` 都支持直接接收 `SqlWrapper` 或 `SelectWrapper`。

```java
UserDef userDef = UserDef.table();
UserDepDef userDepDef = UserDepDef.table();

/**
 * select *
 * from user
 * where user.id in (
 *   select user_id
 *   from user_dep
 *   where id = '123'
 * )
 */
SqlWrapper sqlWrapper = SELECT()
        .FROM(userDef)
        .WHERE(
                userDef.id.in(
                        SELECT(userDepDef.userId)
                                .FROM(userDepDef)
                                .WHERE(userDepDef.id.eq("123"))
                )
        );
```

`notIn` 也是同样的写法：

```java
SqlWrapper sqlWrapper = SELECT()
        .FROM(userDef)
        .WHERE(
                userDef.id.notIn(
                        SELECT(userDepDef.userId)
                                .FROM(userDepDef)
                                .WHERE(userDepDef.id.eq("123"))
                )
        );
```

## 3. EXISTS / NOT EXISTS 查询

这类语义在“是否存在关联记录”场景里会比 `in` 更自然。

```java
import static cn.icframework.mybatis.wrapper.FunctionWrapper.EXISTS;
import static cn.icframework.mybatis.wrapper.FunctionWrapper.NOT_EXISTS;

UserDef userDef = UserDef.table();
UserRoleDef userRoleDef = UserRoleDef.table().alias("ur");

/**
 * select *
 * from user
 * where exists (
 *   select *
 *   from user_role ur
 *   where ur.user_id = user.id
 * )
 */
SqlWrapper existsSql = SELECT()
        .FROM(userDef)
        .WHERE(
                EXISTS(
                        SELECT()
                                .FROM(userRoleDef)
                                .WHERE(userRoleDef.userId.eq(userDef.id))
                )
        );

SqlWrapper notExistsSql = SELECT()
        .FROM(userDef)
        .WHERE(
                NOT_EXISTS(
                        SELECT()
                                .FROM(userRoleDef)
                                .WHERE(userRoleDef.userId.eq(userDef.id))
                )
        );
```

## 4. 把子查询当成 from 表继续 join

这是 `ic-mybatis` 很实用的一点。你可以先做一段分页或过滤子查询，再把结果当临时表继续关联。

```java
IPage page = new IPage();
page.setPageIndex(1);
page.setPageSize(10);

UserDef userDef = UserDef.table();
UserRoleDef userRoleDef = UserRoleDef.table();

// 先把查询转成子查询表
UserDef userSub = SELECT()
        .FROM(userDef)
        .WHERE(userDef.name.like("张三"))
        .PAGE(page)
        .AS(UserDef.class);

/**
 * select *
 * from (
 *   select * from user where name like '%张三%' limit 0,10
 * ) user
 * left join user_role on user.id = user_role.user_id
 */
SqlWrapper sqlWrapper = SELECT()
        .FROM(userSub)
        .LEFT_JOIN(userRoleDef)
        .ON(userSub.id.eq(userRoleDef.userId));
```

这个写法特别适合：

- 先筛一批主数据再继续关联
- 把分页放在子查询内控制数据量
- 用子查询隔离复杂 where

## 5. 多表 join 与复杂 on 条件

`ON` 不只是单个等值判断，也可以继续拼复合条件。

```java
UserDef userDef = UserDef.table();
UserRoleDef userRoleDef = UserRoleDef.table();
RoleDef roleDef = RoleDef.table();

SqlWrapper sqlWrapper = SELECT(userDef, roleDef.name.as("roleName"))
        .FROM(userDef)
        .LEFT_JOIN(userRoleDef)
        .ON(userDef.id.eq(userRoleDef.userId))
        .LEFT_JOIN(roleDef)
        .ON(roleDef.id.eq(userRoleDef.roleId).or().status.eq(1))
        .WHERE(userDef.status.eq(1));
```

如果 join 条件非常复杂，建议把逻辑收进 `WrapperBuilder`，这样 API 层会更干净。

表别名使用 `alias(...)`，它会返回一个新的 `Def` 副本，不会修改原来的 `Def`。复杂 SQL 里建议显式接住返回值：

```java
RoleDef roleDef = RoleDef.table();
RoleDef role = roleDef.alias("role");

SqlWrapper sqlWrapper = SELECT(role.name.as("roleName"))
        .FROM(role);

// roleDef 仍然没有表别名，后续可以继续安全复用
SqlWrapper rawSqlWrapper = SELECT(roleDef.name)
        .FROM(roleDef);
```

字段别名只作用于 `as(...)` 返回的字段副本，不会污染原来的 `xxxDef` 字段。比如 `roleDef.name.as("roleName")` 只影响这次 `SELECT`，后面继续用 `roleDef.name` 做 `WHERE / ORDER_BY / COUNT` 时仍然按原字段 `name` 处理。这样同一个 `Def` 可以安全地在复杂查询和后续查询里复用。

```java
UserDef userDef = UserDef.table();
RoleDef roleDef = RoleDef.table();

SqlWrapper listSql = SELECT(
        userDef.id.as("userId"),
        roleDef.name.as("roleName")
)
        .FROM(userDef)
        .LEFT_JOIN(roleDef).ON(roleDef.id.eq(userDef.roleId));

SqlWrapper countSql = SELECT(COUNT(userDef.id).as("total"))
        .FROM(userDef)
        .ORDER_BY_DESC(userDef.id);
```

如果需要复用带别名的字段，请显式接住返回值：

```java
QueryField<?> userId = userDef.id.as("userId");
SqlWrapper sqlWrapper = SELECT(userId).FROM(userDef);
```

## 6. 条件分组：AND / OR 嵌套

复杂检索里最容易乱的是条件括号，`AND(...)` 和 `OR(...)` 就是为这个准备的。

```java
UserDef userDef = UserDef.table();

/**
 * where ((id = 1 and name like '%张三%') or name like '%李四%')
 */
SqlWrapper sqlWrapper = SELECT()
        .FROM(userDef)
        .WHERE(
                AND(
                        userDef.id.eq(1),
                        AND(),
                        userDef.name.like("张三")
                ),
                OR(),
                userDef.name.like("李四")
        );
```

当你需要一层层拼括号时，优先用这个，而不是手写 SQL 字符串。

## 7. 动态条件拼装：CHECK

很多页面筛选条件都是“有值才拼”，`CHECK` 就是这个场景的快捷写法。

```java
import static cn.icframework.mybatis.query.Checks.CHECK;

UserDef userDef = UserDef.table();

String name = null;
Integer status = 1;

SqlWrapper sqlWrapper = SELECT()
        .FROM(userDef)
        .WHERE(
                CHECK(name != null, userDef.name::like, name),
                CHECK(status != null, userDef.status::eq, status)
        );
```

`CHECK(false, ...)` 会返回 `null`，`WHERE(...)` 内会自动跳过，不需要你自己写一堆 `if`。

这也是业务 `WrapperBuilder` 里最值得多用的技巧之一。

## 8. 分组统计

`GROUP_BY` 可以直接接 `QueryField`，聚合函数从 `FunctionWrapper` 里拿。

```java
import static cn.icframework.mybatis.wrapper.FunctionWrapper.COUNT;

UserDef userDef = UserDef.table();

/**
 * select status, count(1) as total
 * from user
 * group by status
 * order by total desc
 */
SqlWrapper sqlWrapper = SELECT(
        userDef.status,
        COUNT().as("total")
)
        .FROM(userDef)
        .GROUP_BY(userDef.status)
        .ORDER_BY_DESC(COUNT().as("total"));
```

> 当前 `SqlWrapper` 内部已经支持 `HAVING` 语句拼接，但公开链式 API 里没有直接暴露 `HAVING(...)` 方法，所以文档里不建议把它当现成能力使用。业务里如果确实需要，优先改成子查询方案，或者在框架里补公开接口后再用。

## 9. 聚合函数与高级函数

除 `COUNT / SUM / AVG / MAX / MIN` 外，复杂报表里更常用的有：

- `DISTINCT`
- `ANY_VALUE`
- `CONCAT`
- `DATE_FORMAT`
- `GROUP_CONCAT`

### DISTINCT

```java
SqlWrapper sqlWrapper = SELECT(DISTINCT(userDef.name))
        .FROM(userDef);
```

### ANY_VALUE

MySQL 分组时，如果某个字段只是想“随便取一个代表值”，可以用：

```java
import static cn.icframework.mybatis.wrapper.FunctionWrapper.ANY_VALUE;
import static cn.icframework.mybatis.wrapper.FunctionWrapper.COUNT;

DeptDef deptDef = DeptDef.table();
UserDef userDef = UserDef.table();

SqlWrapper sqlWrapper = SELECT(
        deptDef.id,
        ANY_VALUE(deptDef.name).as("deptName"),
        COUNT().as("userCount")
)
        .FROM(deptDef)
        .LEFT_JOIN(userDef).ON(userDef.depId.eq(deptDef.id))
        .GROUP_BY(deptDef.id);
```

### GROUP_CONCAT

```java
import cn.icframework.mybatis.wrapper.FunctionWrapper;
import static cn.icframework.mybatis.wrapper.FunctionWrapper.GROUP_CONCAT;

RoleDef roleDef = RoleDef.table();
UserRoleDef userRoleDef = UserRoleDef.table();
UserDef userDef = UserDef.table();

SqlWrapper sqlWrapper = SELECT(
        userDef.id,
        userDef.name,
        GROUP_CONCAT(
                new FunctionWrapper.GroupConcat(roleDef.name)
                        .orderColumn(roleDef.id)
                        .separator(",")
        ).as("roleNames")
)
        .FROM(userDef)
        .LEFT_JOIN(userRoleDef).ON(userDef.id.eq(userRoleDef.userId))
        .LEFT_JOIN(roleDef).ON(roleDef.id.eq(userRoleDef.roleId))
        .GROUP_BY(userDef.id, userDef.name);
```

## 10. 子查询排序

排序字段也可以来自一段子查询。

```java
UserDef userDef = UserDef.table();
UserDepDef userDepDef = UserDepDef.table();

/**
 * order by (
 *   select name from user_dep where user_dep.id = user.dep_id
 * )
 */
SqlWrapper sqlWrapper = SELECT()
        .FROM(userDef)
        .WHERE(userDef.id.eq(1).or().name.like("张三"))
        .ORDER_BY_ASC(
                SELECT(userDepDef.name)
                        .FROM(userDepDef)
                        .WHERE(userDepDef.id.eq(userDef.depId))
        );
```

这在“按关联表名称排序”“按最新一条记录时间排序”这类需求里非常顺手。

## 11. 复杂查询后的结果映射

复杂查询通常不会直接回实体，而是回 VO：

```java
SqlWrapper sqlWrapper = ...;
List<UserVO> list = userService.selectAs(sqlWrapper, UserVO.class);
PageResponse<UserVO> pageData = userService.pageAs(page, sqlWrapper, UserVO.class);
```

如果查询里有嵌套对象或列表对象，继续配合：

- `@Association`
- `@Collection`
- `@Join`
- `@Joins`

可以再看：

- [/docs/ic-mybatis/query-as](/docs/ic-mybatis/query-as)

## 12. 什么时候该把查询放进 WrapperBuilder

符合下面这些情况时，推荐不要把 SQL 直接写在 Controller 或 Service 里：

- 查询条件来自页面参数
- 存在多个可选筛选项
- 有默认排序和自定义排序
- 需要 join 多张表
- 需要复用同一段查询逻辑

这时最自然的落点就是 `WrapperBuilder`。system 模块里的各类 `wrapperbuilder` 就是现成样板。

# IC-Mybatis 基本查询
>
以下示例是集成BasicService的查询方式

## 查询单个

```java
// 无条件查询 limit 1
User user = userService.selectOne();

// 根据id查询
User user = userService.selectById(1);

// 根据条件查询 limit 1
UserDef userDef = UserDef.table();
SqlWrapper sqlWrapper = SELECT().FROM(userDef).WHERE(userDef.id.eq(1));
// 查询实体
User user = userService.selectOne(sqlWrapper);
// 映射返回类型
UserVO userVO = userService.selectOne(sqlWrapper, UserVO.class);
```

## 查询多个

```java
// 无分页查询所有
List<User> user = userService.selectAll();
List<UserVO> userVOList = userService.selectAll(UserVO.class);


// 分页查询
PageRequest page = PageRequest.of(1, 10);
UserDef userDef = UserDef.table();
SqlWrapper sqlWrapper = SELECT().FROM(userDef).WHERE(userDef.name.like("张三"));
// 查询实体
PageResponse<User> pageUser = userService.page(sqlWrapper, page);
// 映射返回类型
PageResponse<UserVO> pageUserVO = userService.page(sqlWrapper, page, UserVO.class);

// 分页查询以后 page 会回填页数与总数

```
```java
// 分页查询
UserDef userDef = UserDef.table();
SqlWrapper sqlWrapper = SELECT()
        .FROM(userDef)
        .WHERE(userDef.name.like("张三"))
        .OFFSET(1)
        .LIMIT(10);
```

## 简单条件拼接

```java
UserDef userDef = UserDef.table();
// select * from user where id = 1 or name like '张三'
SqlWrapper sqlWrapper = SELECT()
        .FROM(userDef)
        .WHERE(userDef.id.eq(1).or().name.like("张三"));

// select * from user where (id = 1 and name like '张三') or name like '李四'
SqlWrapper sqlWrapper = SELECT()
        .FROM(userDef)
        .WHERE(userDef.id.eq(1).or().name.like("张三"))
        .OR(userDef.name.like("李四"));

/**
 select * from user where 
 ((id = 1 and name like '张三') or name like '李四') or name like '王五'
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
        )
        .OR(userDef.name.like("王五"));



DepDef depDef = DepDef.table();
UserDepDef userDepDef = UserDepDef.table();
/**
 select * from user 
 left join user_dep on user.dep_id = user_dep.user_id 
 left join dep on user_dep.dep_id = dep.id 
 where user.id = 1
 */
SqlWrapper sqlWrapper = SELECT()
        .FROM(userDef)
        .LEFT_JOIN(userDepDef).ON(userDef.depId.eq(userDepDef.userId))
        .LEFT_JOIN(depDef).ON(userDepDef.depId.eq(depDef.id))
        .WHERE(userDef.id.eq(1));
```

## 字段别名

`QueryField.as(...)` 用于给当前查询结果字段起别名。它会返回一个新的字段对象，不会修改 `UserDef.table()` 里原本的字段定义。

```java
UserDef userDef = UserDef.table();

// 推荐：直接使用 as(...) 返回值
SqlWrapper sqlWrapper = SELECT(
        userDef.id.as("userId"),
        userDef.name.as("userName")
)
        .FROM(userDef);

// userDef.id 本身仍然是 id，后续复用不会继承 userId 这个别名
SqlWrapper nextSqlWrapper = SELECT(userDef)
        .FROM(userDef)
        .ORDER_BY_DESC(userDef.id);
```

不要依赖下面这种写法：

```java
userDef.id.as("userId");
SELECT(userDef.id).FROM(userDef);
```

上面第一行返回的新字段没有被接收，别名不会生效。需要别名时，请把 `as(...)` 放进 `SELECT(...)`、函数或变量里。

## 表别名

表 `Def` 起别名统一使用 `alias(...)`，不再提供 `def.as(...)`。`alias(...)` 同样会返回新的 `Def` 副本，不会修改原对象。

```java
UserDef userDef = UserDef.table();
UserRoleDef userRoleDef = UserRoleDef.table();
UserRoleDef ur = userRoleDef.alias("ur");

SqlWrapper sqlWrapper = SELECT(userDef.id, ur.name.as("roleName"))
        .FROM(userDef)
        .LEFT_JOIN(ur).ON(ur.id.eq(userDef.id));

// userRoleDef 仍然没有别名，可以继续作为 user_role 使用
SqlWrapper rawSqlWrapper = SELECT(userRoleDef.id, userRoleDef.name)
        .FROM(userRoleDef);
```

## 排序

```java
UserDef userDef = UserDef.table();
// select * from user where id = 1 or name like '张三' order by id desc, name asc
SqlWrapper sqlWrapper = SELECT()
        .FROM(userDef)
        .WHERE(userDef.id.eq(1).or().name.like("张三"))
        .ORDER_BY(userDef.id.desc())
        .ORDER_BY_ASC(userDef.name);
```

## 函数

```java
UserDef userDef = UserDef.table();
// select max(id) as maxId from user where id = 1 or name like '张三'
SqlWrapper sqlWrapper = SELECT(MAX(userDef.id).AS("maxId"))
        .FROM(userDef)
        .WHERE(userDef.id.eq(1).or().name.like("张三"));

// select DISTINCT id from user where id = 1 or name like '张三'
SqlWrapper sqlWrapper = SELECT(DISTINCT(userDef.id))
        .FROM(userDef)
        .WHERE(userDef.id.eq(1).or().name.like("张三"));

// 更多函数参考 cn.icframework.mybatis.wrapper.FunctionWrapper
```

目前已有函数
```java
public interface SqlFunctionProvider {
    QueryField<?> max(String field);

    QueryField<?> min(String field);

    QueryField<?> avg(String field);

    QueryField<?> sum(String field);

    QueryField<?> lower(String field);

    QueryField<?> upper(String field);

    QueryField<?> length(String field);

    QueryField<?> substring(String field, int pos, int len);

    QueryField<?> trim(String field);

    QueryField<?> round(String field, int scale);

    QueryField<?> ceil(String field);

    QueryField<?> floor(String field);

    QueryField<?> abs(String field);

    QueryField<?> now();

    QueryField<?> dateFormat(String field, String format);

    QueryField<?> year(String field);

    QueryField<?> month(String field);

    QueryField<?> day(String field);

    QueryField<?> count(String field);

    QueryField<?> count();

    QueryField<?> anyValue(String field);

    QueryField<?> concat(Object... vals);

    QueryField<?> distinct(String field);
}
```

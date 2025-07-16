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


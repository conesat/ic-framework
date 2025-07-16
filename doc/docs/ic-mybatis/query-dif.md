# IC-Mybatis 复杂查询
>
以下示例是集成BasicService的查询方式

## 嵌套子sql

子查询内也可继续嵌套子查询

```java
// 查询条件嵌套子sql
UserDef userDef = UserDef.table();
UserDepDef userDepDef = UserDepDef.table();
/**
 select 
    user.*,
    (select name from user_dep where user.dep_id = user_dep.id) as dep
 from user 
 where id = 1
 */
SqlWrapper sqlWrapper = 
        SELECT(
                userDef, 
                SELECT(userDepDef.name)
                        .FROM(userDepDef)
                        .WHERE(userDepDef.id.eq(userDef.depId))
                        .LIMIT(1)
                        .AS("dep")
        )
        .FROM(table)
        .WHERE(userDef.id.eq(1));


/**
 select * from user where id in (select user_id from user_dep where id = '123')
 */
SqlWrapper sqlWrapper = SELECT()
        .FROM(table)
        .WHERE(
                userDef.id.in(
                        SELECT(userDepDef.userId)
                        .FROM(userDepDef)
                        .WHERE(userDepDef.id.eq('123'))
                )
        );
```

## 排序

```java
UserDef userDef = UserDef.table();
UserDepDef userDepDef = UserDepDef.table();
/**
 select * from user where id = 1 or name like '张三' 
 order by (select name from user_dep where user.dep_id = user_dep.id)
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

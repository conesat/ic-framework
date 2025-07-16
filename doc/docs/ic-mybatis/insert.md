# IC-Mybatis 插入
>
以下示例是集成BasicService的方式

## 单个

```java
// 实体
User user = new User();
user.setName("张三");
userService.insert(user);

// 用wrapper
SqlWrapper insert = INSERT()
        .INTO(User.class)
        .COLUMNS(User::getName, User::getId)
        .VALUES("张三", "2");
userService.insert(insert);
```

## 批量

```java
// 默认
User user = new User();
user.setName("张三");
List<User> userList = new ArrayList<>();
userService.insertBatch(userList);

// 跳过错误，这个会利用虚拟线程多批次写入
userService.insertBatch(userList, true);

```

## select into

```java
UserDef userDef = UserDef.table();
SqlWrapper insert = INSERT()
        .INTO(User.class)
        .COLUMNS(User::getDel, User::getName, User::getId)
        .VALUES(
                SELECT(AS(1, User::getDel), userDef.name, userDef.id)
                        .FROM(table)
                        .WHERE(table.name.like("123"))
        );
userService.inser(insert);
```

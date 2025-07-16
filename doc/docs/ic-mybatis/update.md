# IC-Mybatis 更新
>
以下示例是集成BasicService的方式

```java
// 实体
// 默认会跳过空字段
User user = new User();
user.setId(1);
user.setName("张三");
userService.updateById(user);

// 允许设置为空
userService.updateById(user, true);

// 用wrapper
/**
 UPDATE user
 SET `id` = null, `name` = 123
 WHERE (user.name IS NULL)
 */
UserDef userDef = UserDef.table();
SqlWrapper where =
        UPDATE(userDef.id.set(null).name.set("123").name.isNull());
userService.update(userDef);
```

# IC-Mybatis 结果映射与关联查询

IC-Mybatis 提供了两种方式来处理对象之间的关联关系：一种是基于注解的 **结果映射**（手动 Join），另一种是更加自动化的 **关联填充**（自动 Join）。

---

## 方式一：结果映射 (@Association / @Collection)

这种方式对应 MyBatis XML 中的 `association` 和 `collection`。它**只负责将 SQL 查询出来的扁平结果集封装到嵌套对象中**，不会自动发起额外的查询或自动连接表。你需要在 `SqlWrapper` 中手动编写 `LEFT_JOIN`。

### 1. 一对一映射 (@Association)

用于将前缀匹配的字段封装到单个对象中。

```java
public class UserVO {
  private int id;
  private String name;
  // prefix="p" 代表字段别名为 p.xxx 的都会被映射到 profile 对象的属性中
  @Association(prefix="p")
  private ProfileVO profile;
}
```

**用法示例：**
```java
UserDef u = UserDef.table();
ProfileDef p = ProfileDef.table().alias("p");

SqlWrapper sw = SELECT(u, p) // 给关联表起别名 'p'
        .FROM(u)
        .LEFT_JOIN(p).ON(u.profileId.eq(p.id));

List<UserVO> vOs = userService.selectList(sw, UserVO.class);
```

### 2. 一对多映射 (@Collection)

用于将结果集按主表 ID 进行分组，并封装到 `List` 中。

```java
public class UserVO {
  private int id;
  @Collection(prefix="p", groupMainId="id") // groupMainId 指定主表的主键字段名
  private List<ProfileVO> profiles;
}
```

---

## 方式二：自动关联填充 (@Joins / @Join)

这是 IC-Mybatis 提供的**高级自动化方案**。你只需在 VO 字段上通过注解定义表之间的逻辑关系，框架在查询时会**自动为你生成 `LEFT_JOIN` 语句**并提取字段。

### 核心注解说明

- **`@Joins`**: 容器注解，包含一个或多个 `@Join`。
- **`@Join`**: 
    - `joinTable`: 目标表（实体类）。
    - `selfField`: 当前主（或上级）实体的关联字段。
    - `joinTableField`: 目标表的匹配字段。

### 用法示例 (多表连查)

假设我们要查询用户及其所属角色（用户 -> 用户/角色关系表 -> 角色表）：

```java
public class UserVO {
    private Long id;
    private String name;
    
    // 自动通过 UserRole 和 Role 表进行连表查询
    @Joins(joins = {
            @Join(joinTable = UserRole.class, selfField = "id", joinTableField = "userId"),
            @Join(joinTable = Role.class, selfField = "roleId", joinTableField = "id")
    })
    private List<RoleVO> roles;
}
```

**用法示例：**
```java
UserDef userDef = UserDef.table();
SqlWrapper sw = SELECT().FROM(userDef).WHERE(userDef.id.eq(1));

// 框架会自动识别 @Joins 并补全 SQL 中的 JOIN 部分
List<UserVO> vos = userService.selectList(sw, UserVO.class);
```

---

## 两种方式对比

| 特性 | @Association / @Collection | @Joins / @Join |
| :--- | :--- | :--- |
| **SQL 编写** | 需要手动写 `LEFT_JOIN` | 框架自动生成 `JOIN` |
| **字段控制** | 完全掌控 SQL，性能更优 | 开发效率极高，自动拉取所有字段 |
| **复杂逻辑** | 适合极其复杂的关联条件 | 适合标准的主外键关联 |
| **灵活性** | 高 | 中 |

---

> 详细实现请参考：`cn.icframework.mybatis.annotation` 包下的相关源码。

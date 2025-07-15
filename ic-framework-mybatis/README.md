# IC Framework MyBatis 扩展模块

## 概述

IC Framework MyBatis 扩展模块在原生MyBatis的基础上进行了功能增强，提供了更便捷的查询构建器、链式调用、自动分页等功能，大大提高了开发效率。

## 主要特性

### 🔍 增强查询构建器
- 链式SQL查询构建
- 类型安全的查询条件
- 自动参数绑定
- 复杂查询支持

### 📄 自动分页
- 自动分页查询
- 分页结果封装
- 支持多种分页方式

### 🔗 关联查询
- 自动关联查询
- 一对一、一对多、多对多支持
- 延迟加载配置

### 🛡️ 安全防护
- SQL注入防护
- 参数校验
- 异常处理

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>cn.icframework</groupId>
    <artifactId>ic-framework-mybatis</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置MyBatis

```yaml
mybatis:
  mapper-locations: classpath*:mapper/*.xml
  type-aliases-package: cn.icframework.**.pojo
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

### 3. 创建实体类

```java
@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String email;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

### 4. 创建查询定义类

```java
@TableDef("user")
public class UserDef {
    public static final UserDef table = new UserDef();
    
    public final Field id = new Field("id");
    public final Field name = new Field("name");
    public final Field email = new Field("email");
    public final Field status = new Field("status");
    public final Field createTime = new Field("create_time");
    public final Field updateTime = new Field("update_time");
}
```

## 使用示例

### 基础查询

```java
// 简单查询
UserDef userDef = UserDef.table();
List<User> users = SELECT(userDef.all())
    .FROM(userDef)
    .WHERE(userDef.status.eq(Status.ENABLE))
    .list();

// 条件查询
List<User> users = SELECT(userDef.name, userDef.email)
    .FROM(userDef)
    .WHERE(userDef.name.like("张"))
    .AND(userDef.status.eq(Status.ENABLE))
    .ORDER_BY(userDef.createTime.desc())
    .list();
```

### 复杂查询

```java
// 关联查询
UserDef userDef = UserDef.table();
UserRoleDef userRoleDef = UserRoleDef.table();

List<UserVO> userList = SELECT(
    userDef.name, 
    userDef.email,
    userRoleDef.roleName.as("roleName")
)
.FROM(userDef)
.LEFT_JOIN(userRoleDef)
.ON(userDef.id.eq(userRoleDef.userId))
.WHERE(userDef.status.eq(Status.ENABLE))
.ORDER_BY(userDef.createTime.desc())
.list();
```

### 分页查询

```java
// 分页查询
Page<User> page = SELECT(userDef.all())
    .FROM(userDef)
    .WHERE(userDef.status.eq(Status.ENABLE))
    .ORDER_BY(userDef.createTime.desc())
    .page(1, 10);
```

### 聚合查询

```java
// 统计查询
Long count = SELECT(COUNT(userDef.id))
    .FROM(userDef)
    .WHERE(userDef.status.eq(Status.ENABLE))
    .count();

// 分组查询
List<Map<String, Object>> result = SELECT(
    userDef.status,
    COUNT(userDef.id).as("count")
)
.FROM(userDef)
.GROUP_BY(userDef.status)
.list();
```

## 高级功能

### 动态查询

```java
// 动态条件查询
public List<User> searchUsers(String name, Integer status) {
    UserDef userDef = UserDef.table();
    
    QueryBuilder query = SELECT(userDef.all())
        .FROM(userDef);
    
    if (StringUtils.hasText(name)) {
        query.WHERE(userDef.name.like(name));
    }
    
    if (status != null) {
        query.AND(userDef.status.eq(status));
    }
    
    return query.list();
}
```

### 批量操作

```java
// 批量插入
List<User> users = Arrays.asList(user1, user2, user3);
batchInsert(users);

// 批量更新
batchUpdate(users);

// 批量删除
batchDelete(Arrays.asList(1L, 2L, 3L));
```

### 事务支持

```java
@Transactional
public void updateUserWithRole(User user, Role role) {
    // 更新用户信息
    userMapper.updateById(user);
    
    // 更新用户角色
    userRoleMapper.updateByUserId(user.getId(), role.getId());
}
```

## 配置说明

### 数据源配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ic_framework
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

### MyBatis配置

```yaml
mybatis:
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true
    lazy-loading-enabled: true
    aggressive-lazy-loading: false
    multiple-result-sets-enabled: true
    use-column-label: true
    use-generated-keys: true
    auto-mapping-behavior: partial
    auto-mapping-unknown-column-behavior: warning
    default-executor-type: simple
    default-statement-timeout: 25
    default-fetch-size: 100
    safe-row-bounds-enabled: false
    safe-result-handler-enabled: true
    map-underscore-to-camel-case: true
    local-cache-scope: session
    jdbc-type-for-null: other
    lazy-load-trigger-methods: equals,clone,hashCode,toString
    call-setters-on-nulls: false
    return-instance-for-empty-row: false
    log-prefix: mybatis
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

## 最佳实践

### 1. 查询优化

```java
// 推荐：只查询需要的字段
SELECT(userDef.name, userDef.email)
    .FROM(userDef)
    .WHERE(userDef.id.eq(1L))
    .one();

// 不推荐：查询所有字段
SELECT(userDef.all())
    .FROM(userDef)
    .WHERE(userDef.id.eq(1L))
    .one();
```

### 2. 索引使用

```java
// 推荐：使用索引字段作为查询条件
SELECT(userDef.all())
    .FROM(userDef)
    .WHERE(userDef.id.eq(1L))  // 主键索引
    .one();

// 推荐：复合索引
SELECT(userDef.all())
    .FROM(userDef)
    .WHERE(userDef.status.eq(Status.ENABLE))
    .AND(userDef.createTime.gt(LocalDateTime.now().minusDays(7)))
    .list();
```

### 3. 分页优化

```java
// 推荐：使用游标分页
SELECT(userDef.all())
    .FROM(userDef)
    .WHERE(userDef.id.gt(lastId))
    .ORDER_BY(userDef.id.asc())
    .LIMIT(20)
    .list();
```

## 常见问题

### Q: 如何处理复杂的动态查询？
A: 可以使用QueryBuilder的链式调用，根据条件动态添加WHERE子句。

### Q: 如何优化查询性能？
A: 建议使用索引、只查询必要字段、合理使用分页、避免N+1查询问题。

### Q: 如何处理大数据量的查询？
A: 可以使用分页查询、游标查询或者流式查询来处理大数据量。

## 相关链接

- [MyBatis官方文档](https://mybatis.org/mybatis-3/)
- [框架文档](https://icframework.chinahg.top)
- [查询构建器文档](doc/docs/ic-mybatis/query.md) 
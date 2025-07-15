# IC Framework Spring Boot Starter

## 概述

IC Framework Spring Boot Starter 是框架的启动器模块，提供了完整的自动配置功能，让您能够快速集成IC Framework的所有功能。

## 主要特性

### 🚀 一键启动
- 自动配置所有框架组件
- 零配置即可使用
- 支持条件化配置

### 🔧 自动配置
- 自动扫描和注册Bean
- 自动配置数据源
- 自动配置缓存
- 自动配置安全

### 📦 模块化集成
- 认证授权模块自动集成
- 通用工具模块自动集成
- 核心功能模块自动集成
- MyBatis扩展自动集成

## 快速开始

### 1. 添加依赖

在您的Spring Boot项目中添加IC Framework Starter依赖：

```xml
<dependency>
    <groupId>cn.icframework</groupId>
    <artifactId>ic-framework-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置数据库

在 `application.yml` 中配置数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ic_framework
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis:
  mapper-locations: classpath*:mapper/*.xml
  type-aliases-package: cn.icframework.**.pojo
  configuration:
    map-underscore-to-camel-case: true
```

### 3. 配置Redis（可选）

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your_password
    database: 0
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms
```

### 4. 启动应用

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 自动配置功能

### 认证授权自动配置

当引入starter依赖后，以下认证功能会自动生效：

- JWT Token认证
- 权限拦截器
- 跨域配置
- 权限初始化

```java
// 自动配置的权限拦截器
@RequireAuth(permissions = {"user:view"})
@GetMapping("/users")
public Result<List<User>> getUsers() {
    return Result.success(userService.list());
}
```

### 缓存自动配置

Redis和本地缓存会自动配置：

```java
@Service
public class UserService {
    
    @ApiCache(key = "user", expire = 3600)
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
}
```

### MyBatis自动配置

MyBatis扩展功能会自动启用：

```java
// 增强的查询构建器
UserDef userDef = UserDef.table();
List<User> users = SELECT(userDef.all())
    .FROM(userDef)
    .WHERE(userDef.status.eq(Status.ENABLE))
    .list();
```

## 配置说明

### 基础配置

```yaml
ic:
  jwt:
    secret: your-jwt-secret-key
    timeout: 7200
  log:
    printStackTrace: true
    printError: true
  sql:
    updatePath: classpath:/sqls
    initPath: classpath*:/sqls/init.sql
  snowflake:
    dataCenterId: 1
    workerId: 1
  noFilterUrls:
    - /api/public/**
    - /api/auth/**
```

### 认证配置

```yaml
ic:
  auth:
    enable: true
    excludeUrls:
      - /api/public/**
      - /api/auth/**
    jwt:
      secret: your-secret
      expiration: 7200
    cors:
      allowedOrigins: "*"
      allowedMethods: "*"
      allowedHeaders: "*"
```

### 缓存配置

```yaml
ic:
  cache:
    redis:
      enable: true
      defaultExpiration: 3600
    local:
      enable: true
      maxSize: 1000
      expireAfterWrite: 1800
```

## 使用示例

### 创建Controller

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @RequireAuth(permissions = {"user:view"})
    @GetMapping("/list")
    public Result<IPage<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        IPage<User> userPage = userService.page(new Page<>(page, size));
        return Result.success(userPage);
    }
    
    @RequireAuth(permissions = {"user:add"})
    @PostMapping("/add")
    public Result<User> addUser(@RequestBody @Valid User user) {
        userService.save(user);
        return Result.success(user);
    }
    
    @RequireAuth(permissions = {"user:edit"})
    @PutMapping("/update")
    public Result<User> updateUser(@RequestBody @Valid User user) {
        userService.updateById(user);
        return Result.success(user);
    }
    
    @RequireAuth(permissions = {"user:delete"})
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success("删除成功");
    }
}
```

### 创建Service

```java
@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    
    @ApiCache(key = "user", expire = 3600)
    public User getUserById(Long id) {
        return getById(id);
    }
    
    @Transactional
    public void updateUserWithRole(User user, Role role) {
        // 更新用户信息
        updateById(user);
        
        // 更新用户角色
        userRoleService.updateByUserId(user.getId(), role.getId());
    }
}
```

### 创建Mapper

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM user WHERE status = #{status}")
    List<User> selectByStatus(@Param("status") Integer status);
    
    @Select("SELECT u.*, r.name as role_name FROM user u " +
            "LEFT JOIN user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN role r ON ur.role_id = r.id " +
            "WHERE u.status = #{status}")
    List<UserVO> selectUserWithRole(@Param("status") Integer status);
}
```

## 条件配置

### 排除自动配置

如果需要排除某些自动配置，可以使用以下方式：

```java
@SpringBootApplication(exclude = {
    IcFrameworkAutoConfiguration.class
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 条件化配置

```java
@Configuration
@ConditionalOnProperty(name = "ic.auth.enable", havingValue = "true")
public class AuthAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public AuthInterceptor authInterceptor() {
        return new AuthInterceptor();
    }
}
```

## 自定义配置

### 自定义Bean

```java
@Configuration
public class CustomConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public CustomService customService() {
        return new CustomService();
    }
    
    @Bean
    public CustomInterceptor customInterceptor() {
        return new CustomInterceptor();
    }
}
```

### 自定义属性

```java
@ConfigurationProperties(prefix = "ic.custom")
@Data
public class CustomProperties {
    private String name;
    private String value;
    private List<String> items;
}
```

## 最佳实践

### 1. 启动类配置

```java
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("cn.icframework.**.mapper")
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            System.out.println("IC Framework 启动成功！");
        };
    }
}
```

### 2. 配置文件组织

```yaml
# application.yml
spring:
  profiles:
    active: dev

---
# application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ic_framework_dev
    username: dev_user
    password: dev_password

---
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://prod-server:3306/ic_framework_prod
    username: prod_user
    password: prod_password
```

### 3. 异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统异常，请联系管理员");
    }
    
    @ExceptionHandler(ValidationException.class)
    public Result<String> handleValidationException(ValidationException e) {
        return Result.error("参数校验失败：" + e.getMessage());
    }
}
```

## 常见问题

### Q: 如何禁用某些自动配置？
A: 可以使用 `@SpringBootApplication(exclude = {...})` 或者在配置文件中设置相应的开关。

### Q: 如何自定义配置？
A: 可以创建自定义的配置类，使用 `@Configuration` 和 `@Bean` 注解。

### Q: 如何处理Bean冲突？
A: 可以使用 `@Primary` 或 `@Qualifier` 注解来解决Bean冲突问题。

### Q: 如何扩展框架功能？
A: 可以实现框架提供的接口，或者继承框架提供的基类来扩展功能。

## 相关链接

- [Spring Boot文档](https://spring.io/projects/spring-boot)
- [框架文档](https://icframework.chinahg.top)
- [自动配置文档](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-developing-auto-configuration) 
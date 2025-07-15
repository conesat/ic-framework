# IC Framework Annotation 模块

## 概述

IC Framework Annotation 模块提供了框架中使用的各种注解，用于简化开发流程，提高代码的可读性和维护性。

## 包含的注解

### @ApiCache
用于API接口的缓存控制。

```java
@ApiCache(key = "user", expire = 3600)
public User getUserById(Long id) {
    return userMapper.selectById(id);
}
```

**参数说明：**
- `key`: 缓存键名
- `expire`: 过期时间（秒）

### @Author
用于标记代码作者信息。

```java
@Author(name = "张三", email = "zhangsan@example.com")
public class UserService {
    // 业务逻辑
}
```

**参数说明：**
- `name`: 作者姓名
- `email`: 作者邮箱

## 使用方式

### 1. 添加依赖

```xml
<dependency>
    <groupId>cn.icframework</groupId>
    <artifactId>ic-framework-annotation</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 在代码中使用注解

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @ApiCache(key = "user:list", expire = 1800)
    @GetMapping("/list")
    public Result<List<User>> getUserList() {
        // 业务逻辑
        return Result.success(userList);
    }
    
    @Author(name = "李四", email = "lisi@example.com")
    @PostMapping("/add")
    public Result<User> addUser(@RequestBody User user) {
        // 业务逻辑
        return Result.success(user);
    }
}
```

## 注解处理器

框架提供了注解处理器来自动处理这些注解：

- **缓存处理器**: 自动处理 `@ApiCache` 注解，实现缓存逻辑
- **作者信息处理器**: 处理 `@Author` 注解，用于代码文档生成

## 扩展自定义注解

如果需要添加新的注解，可以：

1. 在 `cn.icframework.annotation` 包下创建新的注解类
2. 实现相应的注解处理器
3. 在配置类中注册处理器

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomAnnotation {
    String value() default "";
}
```

## 注意事项

1. 注解处理器需要在Spring容器中注册才能生效
2. 缓存注解需要配合缓存管理器使用
3. 自定义注解需要遵循Java注解规范
4. 建议为自定义注解添加详细的文档说明

## 相关链接

- [框架文档](https://icframework.chinahg.top)
- [Spring Boot注解](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-annotation-processing) 
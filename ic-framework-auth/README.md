# IC Framework Auth 模块

## 概述

IC Framework Auth 是框架的认证授权模块，提供了自动配置功能，当项目中引入此依赖时，会自动扫描并注册所有相关的认证授权Bean。

## 自动配置功能

### 自动扫描的包路径

当引入 `ic-framework-auth` 依赖时，会自动扫描 `cn.icframework.auth` 包及其所有子包中的所有Spring组件：

- `cn.icframework.auth` (根包)
- `cn.icframework.auth.config` (配置包)
- `cn.icframework.auth.processor` (处理器包)
- `cn.icframework.auth.utils` (工具包)
- `cn.icframework.auth.standard` (标准接口包)
- `cn.icframework.auth.entity` (实体包)
- `cn.icframework.auth.threadlocal` (线程本地包)
- `cn.icframework.auth.annotation` (注解包)
- 以及所有其他子包

### 自动注册的组件

1. **配置类**
   - `AuthWebConfig` - 认证Web配置（拦截器、CORS等）

2. **工具类**
   - `JWTUtils` - JWT工具类（需要依赖注入）
   - `AuthPermissionInitRunner` - 权限初始化运行器（需要依赖注入）

3. **处理器类**
   - `IcBeanPostProcessor` - Bean后置处理器（需要依赖注入）

4. **所有带有Spring注解的类**
   - `@Component`
   - `@Service`
   - `@Repository`
   - `@Configuration`

## 使用方法

### 1. 添加依赖

在项目的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>cn.icframework</groupId>
    <artifactId>ic-framework-auth</artifactId>
    <version>${ic.framework.version}</version>
</dependency>
```

### 2. 启动类配置

在Spring Boot启动类上添加 `@EnableAutoConfiguration` 注解（通常 `@SpringBootApplication` 已经包含了此注解）：

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 3. 自动生效

引入依赖后，以下功能会自动生效：

- 认证拦截器会自动注册
- CORS跨域配置会自动生效
- JWT工具类会自动注册
- 权限初始化会自动执行

## 配置说明

### 依赖注入说明

Auth模块中的一些组件使用了特殊的依赖注入方式：

1. **JWTUtils** - 使用静态字段和 `@Autowired` 方法注入
2. **AuthPermissionInitRunner** - 使用 `@AllArgsConstructor` 注解
3. **IcBeanPostProcessor** - 使用 `@AllArgsConstructor` 注解

这些组件会通过 `@ComponentScan` 自动注册，Spring会自动处理依赖注入。

### 条件注解

自动配置使用了以下条件注解：

- `@ConditionalOnMissingBean` - 只有在没有相同类型的Bean时才创建
- `@ConditionalOnClass` - 只有在类路径中存在指定类时才创建

### 自定义配置

如果需要自定义某些配置，可以在项目中创建同名的Bean来覆盖默认配置。

## 注意事项

1. 确保项目中使用了Spring Boot的自动配置机制
2. 如果遇到Bean冲突，可以使用 `@Primary` 或 `@Qualifier` 注解来解决
3. 可以通过 `@EnableAutoConfiguration(exclude = {IcAuthAutoConfiguration.class})` 来排除自动配置
4. `JWTUtils` 需要 `IcConfig` 和 `CacheUtils` 依赖，确保这些依赖已正确配置
5. `AuthPermissionInitRunner` 需要 `IPermissionInitService` 实现类

## 扩展

如果需要添加新的自动配置，可以：

1. 在 `IcAuthAutoConfiguration` 类中添加新的 `@Bean` 方法
2. 在 `@ComponentScan` 中添加新的包路径
3. 在 `@Import` 中添加新的配置类 
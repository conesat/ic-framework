# IC Framework Core 模块

## 概述

IC Framework Core 是框架的核心模块，提供了自动配置功能，当项目中引入此依赖时，会自动扫描并注册所有相关的Bean。

## 自动配置功能

### 自动扫描的包路径

当引入 `ic-framework-core` 依赖时，会自动扫描 `cn.icframework.core` 包及其所有子包中的所有Spring组件：

- `cn.icframework.core` (根包)
- `cn.icframework.core.common` (通用包)
- `cn.icframework.core.basic` (基础包)
- `cn.icframework.core.cache` (缓存包)
- `cn.icframework.core.utils` (工具包)
- `cn.icframework.core.config` (配置包)
- 以及所有其他子包

### 自动注册的组件

1. **配置类**
   - `GlobalConfig` - 全局配置
   - `CoreWebConfig` - Web配置
   - `LocalDateTimeSerializerConfig` - 日期时间序列化配置
   - `PropertySourcesPlaceholderConfig` - 属性源配置
   - `EnumConvertFactory` - 枚举转换工厂

2. **工具类**
   - `IcFrameworkStarter` - 框架启动器
   - `CacheUtils` - 缓存工具类（需要RedisUtils依赖）
   - `RedisUtils` - Redis工具类（需要RedisTemplate依赖）

3. **所有带有Spring注解的类**
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
    <artifactId>ic-framework-core</artifactId>
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

- 框架启动时会显示IC Framework的启动横幅
- 所有core模块中的Bean会被自动注册到Spring容器中
- Web配置等会自动生效

## 配置说明

### 条件注解

自动配置使用了以下条件注解：

- `@ConditionalOnMissingBean` - 只有在没有相同类型的Bean时才创建
- `@ConditionalOnClass` - 只有在类路径中存在指定类时才创建

### 自定义配置

如果需要自定义某些配置，可以在项目中创建同名的Bean来覆盖默认配置。

## 注意事项

1. 确保项目中使用了Spring Boot的自动配置机制
2. 如果遇到Bean冲突，可以使用 `@Primary` 或 `@Qualifier` 注解来解决
3. 可以通过 `@EnableAutoConfiguration(exclude = {IcCoreAutoConfiguration.class})` 来排除自动配置

## 扩展

如果需要添加新的自动配置，可以：

1. 在 `IcCoreAutoConfiguration` 类中添加新的 `@Bean` 方法
2. 在 `@ComponentScan` 中添加新的包路径
3. 在 `@Import` 中添加新的配置类 
# IC Framework Common 模块

## 概述

IC Framework Common 模块提供了框架的基础组件，包括配置属性、常量、工具类等。

## 自动配置

当项目中引入 `ic-framework-common` 依赖时，会自动启用以下配置：

### 配置属性

- `IcConfig`: 框架主配置类，支持以下配置项：
  - `ic.jwt`: JWT相关配置
  - `ic.log`: 日志相关配置
  - `ic.sql`: SQL相关配置
  - `ic.snowflake`: 雪花算法配置
  - `ic.noFilterUrls`: 不需要过滤的URL数组

### 组件扫描

自动扫描 `cn.icframework.common` 包下的所有组件，包括：
- 常量类（`BasicLoginInfo` 等）
- 工具类（`ConvertUtils`、`SpringContextUtil` 等）
- 枚举类（`Sex`、`Status` 等）
- Lambda工具类

## 使用方式

### 1. 添加依赖

```xml
<dependency>
    <groupId>cn.icframework</groupId>
    <artifactId>ic-framework-common</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置属性

在 `application.yml` 中配置：

```yaml
ic:
  jwt:
    secret: your-jwt-secret
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

### 3. 使用工具类

```java
// 使用SpringContextUtil获取Bean
UserService userService = SpringContextUtil.getBeanByClass(UserService.class);

// 使用ConvertUtils进行类型转换
Boolean result = ConvertUtils.toBoolean("true", false);
Integer number = ConvertUtils.toInteger("123", 0);
```

## 自动配置原理

通过 `@EnableConfigurationProperties(IcConfig.class)` 启用配置属性绑定，通过 `@ComponentScan(basePackages = "cn.icframework.common")` 扫描组件，通过 `META-INF/spring.factories` 注册自动配置类。 
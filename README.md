# IC Framework

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-green.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.11.0-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> IC Framework - 集成开发框架，Java开发脚手架

## 📖 项目介绍

IC Framework 是一个Java开发框架，基于Spring Boot 3.5.3构建，提供了一套完整的开发脚手架。框架集成了认证授权、数据访问、代码生成、缓存管理等核心功能，旨在提高开发效率，规范代码结构。

在线文档 [http://icframework.chinahg.top](http://icframework.chinahg.top)

搭建项目请移步[ic-framework-service](http://github.com/conesat/ic-framework-service) [ic-framework-service是使用ic framework集成鉴权、组织机构等基础功能的项目]

### ✋🏻 前言
佛系作者，开源纯属个人爱好，可以不爱请别伤害

### 🎯 设计理念

- **模块化设计**：采用多模块架构，职责分离清晰
- **开箱即用**：提供完整的自动配置，减少重复代码
- **规范统一**：统一的代码规范和最佳实践
- **扩展性强**：支持自定义扩展和插件机制

## ✨ 主要特性

### 🔐 认证授权
- JWT Token认证
- 基于角色的权限控制(RBAC)
- 自动权限初始化
- 跨域配置支持

### 🗄️ 数据访问
- 基于MyBatis的增强查询构建器
- 支持复杂SQL查询的链式调用
- 自动分页和排序
- 数据库连接池优化

### 🛠️ 代码生成
- 自动生成CRUD代码
- 支持Vue3前端代码生成
- 模板化代码生成
- 数据库表结构自动同步

### 💾 缓存管理
- Redis缓存支持
- 本地缓存(Caffeine)
- 缓存注解支持
- 缓存策略配置

### 🔧 工具支持
- 统一异常处理
- 全局响应封装
- 参数校验支持
- 日志记录工具

## 🏗️ 项目结构

```
ic-framework/
├── ic-framework-annotation/          # 注解模块
├── ic-framework-dependencies/        # 依赖管理模块
├── ic-framework-common/             # 通用工具模块
├── ic-framework-auth/               # 认证授权模块
├── ic-framework-core/               # 核心功能模块
├── ic-framework-mybatis/            # MyBatis扩展模块
├── ic-framework-gen/                # 代码生成模块
├── ic-framework-gen-template/       # 代码生成模板
├── ic-framework-dber/               # 数据库工具模块
├── ic-framework-mybatis-processor/  # MyBatis注解处理器
├── ic-framework-spring-boot-starter/ # Spring Boot启动器
├── ic-framework-parent/             # 父模块
└── doc/                            # 文档目录
```

## 🚀 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- Spring Boot 3.5.3+

### 安装使用

#### 1. 添加依赖

在您的Spring Boot项目中添加IC Framework依赖：

```xml
<dependency>
    <groupId>cn.icframework</groupId>
    <artifactId>ic-framework-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### 2. 配置数据库

在 `application.yml` 中配置数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis:
  mapper-locations: classpath*:mapper/*.xml
  type-aliases-package: cn.icframework.**.pojo
```

#### 3. 配置Redis（可选）

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your_password
    database: 0
```

#### 4. 启动应用

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 📦 模块说明

### ic-framework-annotation
提供框架中使用的各种注解，如API缓存、权限验证等注解。

### ic-framework-common
通用工具模块，包含：
- 配置属性管理
- 常量定义
- 工具类集合
- 枚举类型

### ic-framework-auth
认证授权模块，提供：
- JWT Token管理
- 权限拦截器
- 角色权限控制
- 自动权限初始化

### ic-framework-core
核心功能模块，包含：
- 缓存管理
- 异常处理
- 工具类
- 配置管理

### ic-framework-mybatis
MyBatis扩展模块，提供：
- 增强的查询构建器
- 链式SQL查询
- 自动分页
- 复杂查询支持

### ic-framework-gen
代码生成模块，支持：
- 自动生成CRUD代码
- Vue3前端代码生成
- 数据库表结构同步
- 模板化代码生成

## 🔧 配置说明

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

### 权限配置

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
```

## 📚 使用示例

### MyBatis查询示例

```java
// 基础查询
UserDef userDef = UserDef.table();
List<User> users = SELECT(userDef.all())
    .FROM(userDef)
    .WHERE(userDef.status.eq(Status.ENABLE))
    .list();

// 复杂查询
UserDef userDef = UserDef.table();
UserRoleDef userRoleDef = UserRoleDef.table();

List<UserVO> userList = SELECT(
    userDef.name, 
    userRoleDef.name.as("userRoleName")
)
.FROM(userDef)
.LEFT_JOIN(userRoleDef)
.ON(userDef.id.eq(userRoleDef.userId))
.WHERE(userDef.name.like("ic"))
.list();
```

### 权限控制示例

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @RequireAuth(permissions = {"user:view"})
    @GetMapping("/list")
    public Result<List<User>> getUserList() {
        // 业务逻辑
        return Result.success(userList);
    }
    
    @RequireAuth(permissions = {"user:add"})
    @PostMapping("/add")
    public Result<User> addUser(@RequestBody User user) {
        // 业务逻辑
        return Result.success(user);
    }
}
```

## 🛠️ 开发指南

### 添加新模块

1. 在根目录创建新的模块目录
2. 在模块目录下创建标准的Maven项目结构
3. 在根pom.xml中添加模块声明
4. 在ic-framework-dependencies中添加依赖管理

### 自定义配置

```java
@Configuration
public class CustomConfig {
    
    @Bean
    @ConditionalOnMissingBean
    public CustomService customService() {
        return new CustomService();
    }
}
```

### 扩展功能

框架支持通过以下方式扩展功能：
- 实现框架提供的接口
- 使用框架提供的注解
- 继承框架提供的基类
- 使用框架提供的工具类

## 📖 文档

详细文档请参考：[https://icframework.chinahg.top](https://icframework.chinahg.top)

- [快速开始](doc/docs/introduction/about-ic.md)
- [架构说明](doc/docs/introduction/structure.md)
- [API文档](doc/docs/base/api.md)
- [MyBatis扩展](doc/docs/ic-mybatis/ic-mybatis.md)
- [版本管理](docs/VERSIONING.md)

## 🤝 贡献指南

我们欢迎所有形式的贡献，包括但不限于：

1. 🐛 报告Bug
2. 💡 提出新功能建议
3. 📝 改进文档
4. 🔧 提交代码修复

### 贡献步骤

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交您的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request


## 📞 联系我们

- 项目地址：[https://github.com/conesat/ic-framework](https://github.com/your-org/ic-framework)
- 官方网站：[https://icframework.chinahg.top](https://icframework.chinahg.top)
- 问题反馈：[Issues](https://github.com/conesat/ic-framework/issues)

---

⭐ 如果这个项目对您有帮助，请给我们一个星标！

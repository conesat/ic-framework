# IC Framework

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-green.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.11.0-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📖 项目介绍

IC Framework 是一个Java开发框架，基于Spring Boot 3.5.3构建，提供了一套完整的开发脚手架。框架集成了认证授权、数据访问、代码生成、缓存管理等核心功能，旨在提高开发效率，规范代码结构。

在线文档 [http://icframework.chinahg.top](http://icframework.chinahg.top)

搭建项目请移步[ic-framework-service](http://github.com/conesat/ic-framework-service) [ic-framework-service是使用ic framework集成鉴权、组织机构等基础功能的项目]

### ✋🏻 前言

作者开发 <span style="color:#e74c3c">【短线客：一个ai股票分析k线策略训练模拟器，欢迎各位扫码体验】</span> app前期需要开发后台，
试过热门项目[若依](https://gitee.com/y_project/RuoYi/tree/master)，若依上手简单做的也很好，但是页面与java风格不太习惯，于是决定手搓一个，这就有了框架主体。

到选orm又犯难了，试过老牌[MybatisPlus](https://baomidou.com/)，和新势力[MybatisFlex](https://mybatis-flex.com/)。
两个都很不错，个人更倾向MyBatisFlex，但是复杂的sql还是需要写xml，作者灵光一闪决定参考MybatisFlex再手搓一个mybatis增强工具，就想再复杂的sql也要不写xml。

如今短线客上架AppStore已经很久了，框架个人感觉还行，就决定开源出来供各位看官老爷赏乐。

<img src="/doc/public/imgs/dxk.png" style="width: 400px;height: auto"  alt="短线客">

集成框架请移步[giteee](https://gitee.com/ic-framework/ic-framework/) [github](http://github.com/conesat/ic-framework-service)
，预览图：

<table>
<tr>
<td >
<img src="/doc/public/imgs/project1.png" alt="ic-framework-service">
</td>
<td ><img src="/doc/public/imgs/project2.png" alt="ic-framework-service">
</td>
</tr>
</table>

### 🎯 设计理念

- **模块化设计**：采用多模块架构，职责分离清晰
- **开箱即用**：提供完整的自动配置，减少重复代码
- **规范统一**：统一的代码规范和最佳实践
- **扩展性强**：支持自定义扩展和插件机制

## ✨ 主要特性

### 🗄️ Mybatis增强

- 基于MyBatis的增强查询构建器
- 支持复杂SQL查询的链式调用
- 自动分页和排序
- 数据库连接池优化

### 🗄️ Dber

- 实体DDL
- sql版本升级

### 🔐 认证授权

- JWT Token认证
- 基于角色的权限控制(RBAC)
- 自动权限初始化
- 跨域配置支持

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
- 每个接口都能默认鉴权，后台通过角色配置权限，即可访问对应接口

### ic-framework-dber

数据库工具模块，提供：

- 自动实体DDL-不用手动建表啦，启动就会同步数据库字段
- sql升级-程序启动需要调整数据的，可以用sql升级功能

### ic-framework-core

核心功能模块，包含：

- 缓存管理
- 异常处理
- 工具类
- 配置管理
- 基础Mapper
- 基础Service

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

### IcMyBatis示例

#### 查询，这里只展示复杂条件查询，各种join各种嵌套可以自己摸索或者待文档完善

```java
// 多条件,select里面不写或者写table._all就是查询所有字段
UserDef table = UserDef.table();
SqlWrapper sqlWrapper = SELECT(table.name)
        .FROM(table)
        .WHERE(
                table.name.in(
                        SELECT(table.name).FROM(table).WHERE(table.name.eq("2").id.eq("3"))
                ).or().name.like("2")
        );

// 还可以这么写
SqlWrapper sqlWrapper = SELECT(table.name)
        .FROM(table)
        .WHERE(
                table.name.in(
                        SELECT(table.name).FROM(table).WHERE(table.name.eq("2").id.eq("3"))
                )
        ).OR(table.name.like("2"));

// 还可以这么写
SqlWrapper sqlWrapper = SELECT(table.name)
        .FROM(table)
        .WHERE(
                table.name.in(
                        SELECT(table.name).FROM(table).WHERE(table.name.eq("2").id.eq("3"))
                ), OR(), table.name.like("2")
        );

// 去重，不指定条件就是按所有字段去重
SqlWrapper sqlWrapper = SELECT_DISTINCT(table.name)
        .FROM(table)
        .WHERE(
                table.name.in(
                        SELECT(table.name).FROM(table).WHERE(table.name.eq("2").id.eq("3"))
                ).or().name.like("2")
        );

// 指定字段去重
SqlWrapper sqlWrapper = SELECT(DISTINCT(table.id), table.name)
        .FROM(table)
        .WHERE(
                table.name.in(
                        SELECT(table.name).FROM(table).WHERE(table.name.eq("2").id.eq("3"))
                ).or().name.like("2")
        );

// 排序
SqlWrapper sqlWrapper = SELECT(table.name)
        .FROM(table)
        .WHERE(
                table.name.in(
                        SELECT(table.name).FROM(table).WHERE(table.name.eq("2").id.eq("3"))
                ).or().name.like("2")
        )
        .ORDER_BY(
                SELECT(table.id).FROM(table).WHERE(table.name.eq("2"))
        )
        .ORDER_BY(table.name.asc().id.desc())
        .ORDER_BY_ASC(table.name);

// 子查询
UserDef table2 = UserDef.table();
SqlWrapper sqlWrapper = SELECT(table.name, SELECT(table2.name).FROM(table2).WHERE(table2.name.eq(table.name)).AS("name2"))
        .FROM(table)
        .WHERE(
                table.name.in(
                        SELECT(table.name).FROM(table).WHERE(table.name.eq("2").id.eq("3"))
                ).or().name.like("2")
        );
```

#### 插入

```java
// 基础
User user = new User();
user.

setName("张三");
userService.

insert(user);

// 批量插入
List<User> userList = new ArrayList<>();
userList.

add(user);
userService.

insertBatch(user);

// 批量插入如果不需要捕获异常，可以使用skipError，会用虚拟线程分批入库，大幅度提升性能
List<User> userList = new ArrayList<>();
userList.

add(user);
userService.

insertBatch(user, true);

// into select
UserDef table = UserDef.table();
SqlWrapper insert = INSERT()
        .INTO(User.class)
        .COLUMNS(User::getName, User::getDel, User::getId)
        .VALUES(
                SELECT(AS(1, User::getName), AS(1, User::getDel), table.id)
                        .FROM(table)
                        .WHERE(table.name.eq("123"))
        );
userService.

insert(insert);
// 得到以下sql
// INSERT INTO user (name, del, id)
// SELECT 1 AS `name`, 1 AS `del`, user.id
// FROM user WHERE (user.name = #{params.p_0})

```

### MyBatis注解

sql可以查了，怎么映射到实体也挺重要

```java

// 结果映射，以下两个注解的前提是sql已经查询出来了
// 也就是如下sql：
// sqlWrapper = select u.*,r.name as `roles.name`,dep.name as `dep.name` from user u left join user_role ur ....
// 然后就会自动封装roles 和 dep了
List<UserDetailVO> userVos = userService.select(sqlWrapper, UserDetailVO.class);

public class UserDetailVO extends UserVO {
    /**
     * 对应mybatis的association，把一对一关系映射到实体
     */
    @Collection
    private List<RoleSimpleVO> roles;

    /**
     * 对应mybatis的association，把一对一关系映射到实体
     */
    @Association
    private DeptSimpleVO dep;

}

// 多层连接查询
// 下面连接是已经查询出User信息也就是只执行了sqlWrapper = select * from user ....，然后返回需要附加如角色、部门、岗位等信息
List<UserDetailVO> userVos = userService.select(sqlWrapper, UserDetailVO.class);

public class UserDetailVO extends UserVO {
    /**
     * 所属角色
     * 以下内容含义就是用 当前UserVO的idd字段，left join UserRole表的userId
     * 再用UserRole表的roleId left join Role表的id，最终获得RoleSimpleVO信息
     * 可以简单理解内部实现为
     * select * from user_role ur left join role r on ur.role_id = r.id where ur.user_id = #{UserVO.id}
     * 但是实际内部是批量处理的, 会用虚拟线程分批查询，再分配到对应的UserVO
     * select * from user_role ur left join role r on ur.role_id = r.id where ur.user_id in #{ids}
     */
    @Joins(joins = {
            @Join(joinTable = UserRole.class, selfField = "id", joinTableField = "userId"),
            @Join(joinTable = Role.class, selfField = "roleId", joinTableField = "id")
    })
    private List<RoleSimpleVO> roles;

    /**
     * 所属部门
     * 如果不是List则取join第一个
     */
    @Joins(joins = {
            @Join(joinTable = DepUser.class, selfField = "id", joinTableField = "userId"),
            @Join(joinTable = Dept.class, selfField = "depId", joinTableField = "id")
    })
    private DeptSimpleVO dep;

}
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

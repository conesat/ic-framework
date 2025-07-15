# IC Framework 项目介绍

<a href='#'  target=_blank>IC Framework</a> 是一个套开发脚手架，后端使用<a href='https://spring.io/' target=_blank>
SpringBoot</a>
，中台使用<a href='https://tdesign.tencent.com/' target=_blank>TDesign（vue3）</a>
，小程序使用<a href='https://uniapp.dcloud.net.cn/' target=_blank>uniapp</a>，
APP使用<a href='https://flutter.cn/' target=_blank>flutter</a>。
集成登录、角色权限、用户部门、文件管理等基础功能。规范设计代码，提供代码生成工具，以供开发者快速开发成套平台。

## 项目定位

- **一站式集成**：集成权限、缓存、代码生成、数据库增强等常用能力
- **模块化设计**：各模块解耦，按需引入，便于扩展
- **多端支持**：适用于中台、管理后台、小程序、App 等多种场景
- **开源共建**：欢迎有兴趣的开发者参与完善

## 适用场景

- 中后台系统
- 多端一体化项目（如 Web+小程序+App）
- 需要快速搭建权限、缓存、代码生成等基础设施的项目
- 追求高扩展性、可维护性的 Java 项目

## 核心优势

- **Spring Boot 3.5.3+ / JDK 21+**：紧跟主流技术栈
- **权限体系**：内置权限注解、认证拦截、角色管理
- **缓存体系**：支持本地、Redis、统一接口
- **代码生成**：支持 Java/Vue 多端代码生成
- **数据库增强**：表结构管理、DDL 辅助
- **mybatis增强**：规范、统一sql编写
- **丰富的工具类与通用配置**



## ic-mybatis

ic-mybatis在<a href='https://github.com/mybatis' target=_blank>mybatis</a>的基础上进行了扩展，以配合ic-framework的功能，减少sql的编写，提高基础查询的编写效率。更多复杂查询，查看ic-mybatis板块

**ic-mybatis查询示例**

````java
UserDef userDef = UserDef.table();
UserRoleDef userRoleDef = UserRoleDef.table();

SELECT(userDef.name, userRoleDef.name.as("userRoleName"))
.FROM(userDef).LEFT_JOIN(userRoleDef).ON(userDef.id.eq(userRoleDef.id))
.WHERE(userDef.name.like("ic"));
````
上述示例将得到sql如下

````sql
select user.name,user_role.name as userRoleName 
from user left join user_role on user.id = user_role.id 
where user.name like concat('%', ?, '%')
````


## 发展现状

- 框架处于开发阶段，部分功能持续完善中
- 文档逐步补充，欢迎 issue/PR 参与共建

---

> 目标：让开发者专注业务，基础设施交给框架！

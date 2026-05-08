# IC Framework 项目介绍

IC Framework 是一套面向业务开发的集成框架。它不是只提供几个工具类，而是把一个完整项目里最常重复搭建的基础层先做好：

- 后端基础结构
- 权限与登录
- 用户、角色、菜单、部门、岗位
- MyBatis 查询增强
- 代码生成
- 自动建表与升级脚本
- 中台前端集成示例

它的目标很直接：让开发者从“搭脚手架、补基础设施”切换到“直接做业务模块”。

## 你可以把它理解成什么

如果你第一次接触这个仓库，最适合的理解方式是：

1. `ic-framework` 是底层框架能力集合
2. `ic-framework-service` 是基于这套框架做出的集成示例工程
3. 日常开发通常不是从零拼框架模块，而是从集成工程起步，直接扩展自己的业务模块

所以文档也建议按这个顺序阅读：

1. 先看怎么把示例工程跑起来
2. 再看一个业务模块是怎么组织的
3. 再理解 `ic-framework-system` 和 `ic-mybatis`

## 核心特点

- 基于 Spring Boot 和 Java 的主流后端栈
- 约定清晰，模块结构统一
- `ic-mybatis` 支持复杂查询与结果映射
- `dber` 支持实体 DDL 和升级脚本
- 自带 system 基础模块，减少重复建设
- 有代码生成器和 IDEA 插件辅助
- 提供后台、小程序、App 的集成方向

## 适合谁

比较适合下面这几类项目：

- 管理后台
- 带组织、角色、权限体系的业务系统
- 想快速起一个完整中后台骨架的团队
- 想统一项目代码结构、减少重复 CRUD 的团队

## 这套文档怎么读最顺

如果你是第一次接项目，推荐顺序：

1. [/docs/start/import](/docs/start/import)
2. [/docs/introduction/structure](/docs/introduction/structure)
3. [/docs/start/java](/docs/start/java)
4. [/docs/system/system](/docs/system/system)
5. [/docs/ic-mybatis/ic-mybatis](/docs/ic-mybatis/ic-mybatis)

如果你已经把项目跑起来了，后面最常用的是这些章节：

- [/docs/base/entity](/docs/base/entity)
- [/docs/base/api](/docs/base/api)
- [/docs/base/wrapperbuilder](/docs/base/wrapperbuilder)
- [/docs/ic-mybatis/query](/docs/ic-mybatis/query)
- [/docs/ic-mybatis/query-dif](/docs/ic-mybatis/query-dif)

## 一句话目标

让开发者专注业务，基础设施交给框架。

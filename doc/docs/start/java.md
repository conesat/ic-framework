# 后端开发结构

这一页不讲所有框架能力，只讲一件事：  
**在 IC Framework 里，一个后端业务模块应该怎么组织。**

## 先记住一句话

一个模块通常围绕这几类文件展开：

- `Entity`
- `DTO / VO`
- `Mapper`
- `Service`
- `Api`
- `WrapperBuilder`

如果你顺着这条线开发，大部分模块都会长得很一致。

## 1. 模块目录长什么样

以 `system` 里的模块为例，一个典型模块通常类似这样：

```text
module/user/
  User.java
  api/
    ApiSysUser.java
    ApiAppUser.java
    ApiPublicUser.java
  dao/
    UserMapper.java
  service/
    UserService.java
  pojo/
    dto/
    vo/
  wrapperbuilder/
    UserWrapperBuilder.java
```

你自己在 `ic-framework-project` 里写业务模块时，也建议继续沿用这个结构。

## 2. 每一层各自负责什么

### Entity

实体负责：

- 表结构映射
- 字段注解
- DDL 生成基础
- `ic-mybatis` def 生成基础

也就是说，实体不仅仅是“数据库对象”，它还是后面很多自动化能力的入口。

### DTO

DTO 只负责入参。  
适合放：

- 表单字段
- 校验注解
- 新增 / 编辑输入参数

### VO

VO 只负责出参。  
适合放：

- 页面展示字段
- 关联对象
- 聚合查询后的返回结构

### Mapper

Mapper 继承 `BasicMapper<T>`，负责基础数据库操作能力接入。

通常你不需要先自己写很多 SQL，先把：

```java
public interface XxxMapper extends BasicMapper<Xxx> {
}
```

接好就能做很多事。

### Service

Service 继承 `BasicService<Mapper, Entity>`，是业务逻辑主落点。

适合放：

- 保存前校验
- 编辑逻辑
- 事务逻辑
- 业务状态变更

### Api

Api 层负责：

- 接收请求
- 调用 `WrapperBuilder`
- 调用 `Service`
- 返回统一响应

这里尽量薄，不建议把复杂查询和业务逻辑都堆进 Controller。

### WrapperBuilder

这是 IC Framework 很值得养成习惯的一层。

它主要负责：

- 列表筛选
- 动态条件拼装
- 默认排序
- 联表查询
- 页面级查询模型

如果你有分页、多条件筛选、自定义排序，这层几乎一定会用到。

## 3. 一个最常见的开发顺序

新增一个业务模块时，最自然的顺序一般是：

1. 先写实体 `Entity`
2. 生成或补全 `DTO / VO / Mapper / Service / Api / WrapperBuilder`
3. 配置查询条件和排序
4. 接前端页面

如果你使用代码生成器，这个过程会快很多；但即使不用生成器，也建议按这个顺序走。

## 4. 为什么推荐把查询放进 WrapperBuilder

很多项目的问题是，查询逻辑分散在：

- Controller 里一点
- Service 里一点
- Mapper XML 里一点

最后没人能一眼看清页面列表到底怎么查的。

在 IC Framework 里，推荐把“页面查询逻辑”集中放进 `WrapperBuilder`：

- API 层只负责取参数
- WrapperBuilder 负责组 SQL
- Service 负责执行和业务逻辑

这样维护成本会低很多。

## 5. system 模块为什么值得多看

如果你不知道模块该怎么写，最好的参考不是凭空想，而是直接看 `ic-framework-system`。

它本身就把这些结构跑通了：

- 用户
- 角色
- 菜单
- 部门
- 岗位
- 在线用户

所以 `system` 模块既是现成功能，也是最好的代码样板。

## 6. 开发时最常配合看的文档

写后端模块时，最常搭配阅读的是：

- [/docs/base/entity](/docs/base/entity)
- [/docs/base/api](/docs/base/api)
- [/docs/base/service](/docs/base/service)
- [/docs/base/mapper](/docs/base/mapper)
- [/docs/base/wrapperbuilder](/docs/base/wrapperbuilder)
- [/docs/ic-mybatis/query](/docs/ic-mybatis/query)

## 7. 一句话建议

把 `Entity + WrapperBuilder + Service` 这三层写清楚，整个模块基本就稳了。

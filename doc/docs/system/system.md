# ic-framework-system

`ic-framework-system` 是一套可以直接复用的系统基础模块，适合做中后台、SaaS 后台、组织架构系统和带权限控制的业务项目。

它不是单一的“登录模块”，而是一组已经按 `module / api / service / wrapperbuilder / dao / pojo` 组织好的系统能力集合。

## 模块内容

当前模块里已经包含这些能力：

- 用户、登录、在线用户
- 角色、权限、权限分组
- 菜单、角色菜单
- 部门、岗位、用户岗位、用户角色
- 字典、系统设置、通知、文件
- WebSocket 消息通道
- 系统监控
- 启动初始化任务

从源码目录也能看到这些模块都是成套存在的，直接可以作为业务项目基础层使用。

## 什么时候先看这页

如果你已经把项目跑起来了，但还不清楚：

- 哪些能力 system 已经帮你做了
- 业务模块应该和 system 怎么配合
- 哪些初始化、鉴权、菜单、登录逻辑是现成的

那就该先看这一页。

## 依赖方式

如果你是基于 `ic-framework-service` 开发，通常已经自带了 `ic-framework-system`。

单独引用时，坐标如下：

```xml
<dependency>
    <groupId>cn.icframework</groupId>
    <artifactId>ic-framework-system</artifactId>
    <version>1.0.3</version>
</dependency>
```

这个模块本身依赖了：

- `ic-framework-core`
- `ic-framework-auth`
- `ic-framework-mybatis`

并且编译时会使用 `ic-framework-mybatis-processor` 生成 def 文件。

## 最推荐的使用姿势

对大多数业务项目来说，最顺手的做法不是“自己重新造一个 system”，而是：

1. 直接复用 `ic-framework-system`
2. 把自己的业务模块写在 `ic-framework-project`
3. 让业务模块去对接现成的用户、角色、菜单、部门、岗位能力

这样你真正新增的，通常只剩业务自己的表、接口和页面。

## 自动装配

`ic-framework-system` 通过 Spring Boot 自动装配注册了四组能力：

- `SystemDataAutoConfiguration`
- `SystemApiAutoConfiguration`
- `SystemWebAutoConfiguration`
- `SystemJobAutoConfiguration`

对应导出文件在：

`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

### 默认开关

系统模块默认是开启的：

```yaml
ic:
  system:
    enabled: true
```

常用子开关：

```yaml
ic:
  system:
    enabled: true
    jobs:
      enabled: true
    websocket:
      enabled: true
    file-storage:
      enabled: true
    monitor:
      enabled: true
      sample-interval-seconds: 5
      retention-minutes: 60
      disk-detail-enabled: true
```

这些配置分别影响：

- `ic.system.enabled`：是否启用整个 system 模块
- `ic.system.jobs.enabled`：是否启用初始化任务、清理任务等作业能力
- `ic.system.websocket.enabled`：是否注册 `/ws`
- `ic.system.file-storage.enabled`：是否启用文件存储实现
- `ic.system.monitor.enabled`：是否启用系统监控采样

## 文件存储

系统模块内置了对象存储能力，文件存储总开关是：

```yaml
ic:
  system:
    file-storage:
      enabled: true
```

实际存储类型通过业务配置决定：

```yaml
app:
  file-storage:
    type: minio
```

或：

```yaml
app:
  file-storage:
    type: oss
```

示例项目里还能看到 `fastdfs` 的业务配置，但 `ic-framework-system` 当前自动装配里明确提供的是 `minio / oss` 这两类实现；如果你有自己的文件策略，也可以按现有接口风格扩展。

## 初始化机制

系统模块启动后会由 `InitRunner` 执行三类初始化：

- 角色与角色权限：`RpInit`
- 菜单：`MenuInit`
- 岗位：`PosInit`

此外，权限本身由 `PermissionInit` 负责，它会根据 `@RequireAuth` 扫描结果把权限组和权限点初始化到数据库。

初始化的详细说明放在单独页面：

- [/docs/system/init](/docs/system/init)

system 模块这一层只需要记住两点：

- 启动时会自动触发这些初始化任务
- 初始化带 MD5 变更检测，内容无变化时不会重复执行

## 权限是怎么接进来的

`ic-framework-system` 和 `ic-framework-auth` 是配合使用的。

你在 Controller 上加 `@RequireAuth` 后：

1. 启动时会扫描接口
2. 生成权限组和权限点
3. 再通过 `roles.json` 和 `rolePermissions.json` 初始化角色及角色权限关系

一个典型接口长这样：

```java
@RestController
@RequestMapping(value = Api.API_SYS + "/role", name = "角色")
@RequireAuth(userType = UserType.SYSTEM_USER)
public class ApiSysRole extends BasicApi {
}
```

如果只是想接 token，不想做角色或权限判定，可以在 `@RequireAuth` 中配置：

```java
@RequireAuth(userType = UserType.SYSTEM_USER, onlyToken = true)
```

更细的初始化说明，直接看：

- [/docs/system/init](/docs/system/init)

## API 分层习惯

system 模块里的接口基本按照三种前缀分层：

- `Api.API_SYS`：系统后台接口
- `Api.API_PUBLIC`：公开接口，不鉴权
- `Api.API_APP`：应用端接口

因此你会在同一类资源下看到：

- `ApiSysUser`
- `ApiPublicUser`
- `ApiAppUser`

这种结构非常适合把“后台 / 公开 / App”三种访问面拆开维护。

## 业务模块怎么和 system 配合

最常见的配合关系是：

- 业务数据归你自己的模块
- 用户、角色、菜单、组织架构归 system
- 页面权限、菜单可见性、登录上下文复用 system

例如你新增一个“订单模块”，通常不会自己再建一套用户和角色体系，而是直接复用：

- 用户信息
- 角色权限
- 菜单入口
- 在线登录态

这也是为什么 system 模块值得先熟悉，它决定了你后面很多业务模块不用从零开始。

## WebSocket

当 `ic.system.websocket.enabled=true` 时，会注册：

```text
/ws
```

当前实现里，WebSocket 握手会从 query 参数里读取 `token` 并校验 JWT，所以前端通常会这样连接：

```text
/ws?token=你的token
```

## 适合怎样使用

比较推荐的用法不是直接改 system 模块源码，而是：

1. 把它当成基础系统层
2. 在业务模块里复用现成的用户、权限、菜单、组织架构能力
3. 新业务继续沿用同样的生成结构：`entity / dto / vo / api / service / wrapperbuilder / mapper`

这样后面的业务模块风格会非常统一。

## 一个最小接入思路

### 1. 引入模块并配置 MyBatis

```yaml
mybatis:
  config-location: classpath:mybatis-config.xml
```

### 2. 准备 system 所需资源

```text
resources/init/menu/
resources/init/pos/pos.json
resources/init/rp/roles.json
resources/init/rp/rolePermissions.json
```

这些文件的字段约定和示例，直接参考：

- [/docs/system/init](/docs/system/init)

### 3. 编写需要权限控制的接口

```java
@RestController
@RequestMapping(value = Api.API_SYS + "/demo", name = "演示")
@RequireAuth(userType = UserType.SYSTEM_USER)
public class ApiSysDemo extends BasicApi {
}
```

### 4. 启动后检查

- 权限组和权限点是否已生成
- 角色是否已初始化
- 菜单是否已入库
- 用户登录后是否能拿到 token / refreshToken

## 和文档里其他章节的关系

如果你已经在用 `ic-framework-system`，建议配合阅读这些章节：

- [/docs/base/api](/docs/base/api)
- [/docs/base/wrapperbuilder](/docs/base/wrapperbuilder)
- [/docs/system/init](/docs/system/init)
- [/docs/ic-mybatis/query](/docs/ic-mybatis/query)

因为 system 模块内部本身就是按这套方式写出来的，读懂这些文档后，再看 system 的源码会非常顺手。

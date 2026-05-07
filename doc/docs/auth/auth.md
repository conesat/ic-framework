# 权限体系说明（Auth 模块）

IC Framework 提供了一套轻量级、可扩展的权限认证体系，基于 JWT 实现，支持多端登录、权限颗粒度控制及用户上下文管理。

## 核心配置

权限模块的核心配置前缀为 `ic.jwt`，可以通过配置文件（如 `application.yml`）进行自定义：

```yaml
ic:
  jwt:
    # JWT 签名密钥
    secret: your-custom-secret-key
    # Token 过期时间（秒），默认 7200（2小时）
    timeout: 7200
    # 不需要进行权限校验的 URL 列表（支持通配符）
    no-filter-urls: 
      - /api/login
      - /public/**
```

## 核心注解

### 1. `@RequireAuth`
标记在 Controller 类或方法上，表示该接口需要进行 Token 校验。
- 只有携带有效 Token 的请求才能访问。

### 2. `@PermissionInit`
用于初始化权限点。系统启动时会扫描该注解并将其持久化到数据库或缓存中，便于进行权限分配。

### 3. `@NotValidateSystem`
跳过系统激活状态校验。如果某些接口在系统未激活时也需要运行，可使用此注解。

## 扩展与定制

IC Framework 提供了多个标准接口供开发者扩展，以满足不同的业务需求：

### 1. 自定义用户体系 (`IOnlineUserService`)
通过实现 `IOnlineUserService` 接口，你可以：
- 接管用户登录逻辑。
- 自定义 Token 的校验、刷新及注销行为。
- 管理用户在线状态。

### 2. 系统状态校验 (`ISystemVerifyService`)
如果你的应用需要授权激活码才能运行，可以实现该接口来自定义激活校验逻辑。

## 开发者提示

- 权限模块通过 `AuthInterceptor` 拦截器自动拦截标记了 `@RequireAuth` 的请求。
- 系统内部提供了 `JWTUtils` 工具类，用于手动创建和刷新 Token。
- 用户登录成功后的上下文信息可以通过 `JwtContext` 获取。

---

> 详细实现请参考：`cn.icframework.auth` 包下的相关源码。
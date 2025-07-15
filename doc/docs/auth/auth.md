# 权限体系说明（auth 模块）

## 1. 模块作用

- 提供统一的权限认证体系，支持注解式权限校验
- 支持用户、角色、权限分组管理
- 提供权限初始化、拦截器、ThreadLocal 上下文等能力

## 2. 主要功能

- 注解式权限校验（@RequireAuth、@PermissionInit 等）
- 权限拦截器自动拦截请求
- 用户上下文（JwtContext）管理
- 权限初始化与扩展（PermissionInit、IPermissionInitService）

## 3. 用法示例

```java
@RequireAuth
@GetMapping("/api/secure")
public String secureApi() {
    // 需要登录和权限
    return "ok";
}
```

## 4. 扩展点

- 实现 IOnlineUserService 可自定义用户体系
- 实现 ISystemVerifyService 可自定义系统校验
- 可自定义权限注解和拦截逻辑

## 5. 常见问题

- **如何自定义权限校验？**
  实现相关接口并在配置中指定即可。

---

> 详细用法请参考源码及注释。 
# 缓存体系说明（Cache 模块）

IC Framework 提供了一套统一的缓存抽象层，支持本地缓存（LoadingCache）和 Redis 缓存，并具备自动切换能力。

## 核心接口：`ICacheService`

所有缓存操作都通过 `ICacheService` 接口进行，主要方法包括：

- `get(String key)`: 获取缓存值。
- `get(String key, Function loadData)`: 获取缓存值，若不存在则通过函数加载并回写缓存。
- `set(String key, Object data, long expireTime)`: 设置缓存及过期时间。
- `exists(String key)`: 判断键是否存在。
- `remove(String key)`: 删除指定缓存。
- `clear()`: 情况当前缓存空间。

## 缓存类型与切换

系统会自动检测 Redis 的配置情况：
1. **优先使用 Redis**：如果 Spring 环境中配置了 Redis 连接，系统会自动切换到 `RedisCacheServiceImpl`。
2. **本地缓存兜底**：若未配置 Redis，系统将使用基于内存的 `LocalCacheServiceImpl`。

你可以通过 `ICacheService.useRedis()` 方法判断当前正在使用的缓存实现类型。

## 便捷工具类：`CacheUtils`

为了简化开发，系统提供了一个 `CacheUtils` 静态工具类，它包装了 `ICacheService` 的所有核心功能：

```java
// 静态调用示例
CacheUtils.set("user_ref", userInfo, 3600);
Object value = CacheUtils.get("user_ref");
```

## 开发者提示

- 缓存模块通过 `IcCacheAutoConfiguration` 实现自动扫描和注册。
- `UnifiedCacheServiceImpl` 是系统的核心入口，它负责根据环境自动分配底层实现。
- 对于本地缓存，底层使用了高效的内存管理机制。

---

> 详细实现请参考：`cn.icframework.cache` 包下的相关源码。
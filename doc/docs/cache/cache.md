# 缓存体系说明（cache 模块）

## 1. 模块作用

- 提供统一缓存接口，支持本地缓存、Redis 缓存等多种实现
- 支持缓存工具类、统一缓存服务

## 2. 主要功能

- ICacheService 缓存接口
- LocalCacheServiceImpl 本地缓存实现
- RedisCacheServiceImpl Redis 缓存实现
- UnifiedCacheServiceImpl 统一缓存服务
- CacheUtils 工具类

## 3. 用法示例

```java
@Autowired
ICacheService cacheService;

cacheService.set("key", "value");
String value = cacheService.get("key");
```

## 4. 扩展点

- 实现 ICacheService 可自定义缓存实现
- 支持多级缓存、分布式缓存扩展

## 5. 常见问题

- **如何切换缓存类型？**
  配置 `ic.cache.type` 为 local 或 redis。

---

> 详细用法请参考源码及注释。 
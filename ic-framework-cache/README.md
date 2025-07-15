# IC Framework Cache 缓存模块

## 概述

IC Framework Cache 是一个统一的缓存模块，提供简洁的缓存接口，支持Redis和本地缓存的双重实现。当有Redis配置时默认使用Redis，当没有Redis时自动使用本地缓存。

## 特性

- **统一接口**: 提供统一的缓存操作接口，无需关心底层实现
- **自动切换**: 根据Redis配置自动选择使用Redis还是本地缓存
- **容错机制**: Redis连接失败时自动降级到本地缓存
- **丰富功能**: 支持get、set、remove、expire等完整的缓存操作
- **类型安全**: 支持泛型操作，提供类型安全的缓存访问

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>cn.icframework</groupId>
    <artifactId>ic-framework-cache</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置Redis（可选）

如果配置了Redis，将自动使用Redis缓存：

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your-password
    database: 0
```

### 3. 配置本地缓存（可选）

```yaml
ic:
  cache:
    local:
      max-size: 10000        # 最大缓存条目数
      expire-after-write: 600 # 写入后过期时间（秒）
```

### 4. 使用缓存

#### 方式一：注入ICacheService

```java
@Service
public class UserService {
    
    @Autowired
    private ICacheService cacheService;
    
    public User getUserById(Long id) {
        String key = "user:" + id;
        
        // 获取缓存
        User user = (User) cacheService.get(key);
        if (user == null) {
            // 从数据库加载
            user = userRepository.findById(id);
            if (user != null) {
                // 设置缓存，过期时间30分钟
                cacheService.set(key, user, 30 * 60);
            }
        }
        return user;
    }
}
```

#### 方式二：使用CacheUtils静态方法

```java
@Service
public class ProductService {
    
    public Product getProductById(Long id) {
        String key = "product:" + id;
        
        // 使用加载函数，如果缓存不存在则自动加载
        return (Product) CacheUtils.get(key, k -> {
            return productRepository.findById(id);
        });
    }
    
    public void updateProduct(Product product) {
        // 更新数据库
        productRepository.save(product);
        
        // 删除缓存
        CacheUtils.remove("product:" + product.getId());
    }
}
```

## API 参考

### ICacheService 接口

#### 基本操作

```java
// 获取缓存值
Object get(String key);

// 获取缓存值，如果不存在则通过加载函数获取
Object get(String key, Function<String, Object> loadData);

// 设置缓存值
void set(String key, Object data);

// 设置缓存值并指定过期时间（秒）
void set(String key, Object data, long expireTime);

// 设置缓存值并指定过期时间和时间单位
void set(String key, Object data, long expireTime, TimeUnit unit);

// 删除缓存
void remove(String key);
```

#### 过期时间操作

```java
// 设置缓存过期时间（秒）
boolean expire(String key, long expireTime);

// 设置缓存过期时间
boolean expire(String key, long expireTime, TimeUnit unit);

// 获取缓存剩余过期时间（秒）
long getExpire(String key);
```

#### 其他操作

```java
// 检查缓存是否存在
boolean exists(String key);

// 清空所有缓存
void clear();

// 检查是否使用Redis
boolean useRedis();
```

### CacheUtils 工具类

CacheUtils 提供了与 ICacheService 相同的静态方法，使用更加便捷：

```java
// 基本操作
CacheUtils.get("key");
CacheUtils.set("key", "value");
CacheUtils.set("key", "value", 3600); // 1小时过期
CacheUtils.remove("key");

// 带加载函数的获取
CacheUtils.get("key", k -> loadDataFromDatabase(k));

// 过期时间操作
CacheUtils.expire("key", 1800); // 30分钟过期
long ttl = CacheUtils.getExpire("key");

// 其他操作
boolean exists = CacheUtils.exists("key");
CacheUtils.clear();
boolean useRedis = CacheUtils.useRedis();
```

## 实现原理

### 自动选择策略

1. **Redis可用时**: 优先使用Redis缓存，Redis连接失败时自动降级到本地缓存
2. **Redis不可用时**: 直接使用本地缓存（基于Caffeine实现）

### 本地缓存特性

- **最大容量**: 默认10000个条目
- **过期策略**: 写入后固定时间过期（默认10分钟）
- **内存管理**: 自动清理过期和最少使用的条目

### Redis缓存特性

- **持久化**: 数据持久化到Redis服务器
- **分布式**: 支持多实例共享缓存
- **动态过期**: 支持动态设置和修改过期时间
- **原子操作**: 支持原子性的缓存操作

## 配置说明

### Redis配置

```yaml
spring:
  redis:
    host: localhost          # Redis服务器地址
    port: 6379              # Redis端口
    password: your-password  # Redis密码（可选）
    database: 0              # Redis数据库编号
    timeout: 2000ms          # 连接超时时间
    lettuce:
      pool:
        max-active: 8        # 最大活跃连接数
        max-idle: 8          # 最大空闲连接数
        min-idle: 0          # 最小空闲连接数
        max-wait: -1ms       # 最大等待时间
```

### 本地缓存配置

```yaml
ic:
  cache:
    local:
      max-size: 10000        # 最大缓存条目数
      expire-after-write: 600 # 写入后过期时间（秒）
```

## 最佳实践

### 1. 缓存键命名规范

```java
// 使用冒号分隔的层次结构
"user:123"           // 用户信息
"user:123:profile"   // 用户资料
"product:456:stock"  // 商品库存
"order:789:items"    // 订单商品列表
```

### 2. 缓存更新策略

```java
// 更新数据库后删除缓存（Cache-Aside Pattern）
public void updateUser(User user) {
    userRepository.save(user);
    CacheUtils.remove("user:" + user.getId());
}

// 或者更新缓存
public void updateUser(User user) {
    userRepository.save(user);
    CacheUtils.set("user:" + user.getId(), user, 3600);
}
```

### 3. 缓存穿透防护

```java
public User getUserById(Long id) {
    String key = "user:" + id;
    
    return (User) CacheUtils.get(key, k -> {
        User user = userRepository.findById(id);
        if (user == null) {
            // 缓存空值，防止缓存穿透
            CacheUtils.set(k, new NullValue(), 300); // 5分钟过期
            return null;
        }
        return user;
    });
}
```

### 4. 批量操作

```java
public List<User> getUsersByIds(List<Long> ids) {
    List<User> users = new ArrayList<>();
    
    for (Long id : ids) {
        String key = "user:" + id;
        User user = (User) CacheUtils.get(key, k -> userRepository.findById(id));
        if (user != null) {
            users.add(user);
        }
    }
    
    return users;
}
```

## 故障排除

### 1. Redis连接失败

如果Redis连接失败，系统会自动降级到本地缓存，并在日志中记录警告信息。

### 2. 缓存不生效

检查以下几点：
- 确保正确注入了ICacheService或使用了CacheUtils
- 检查缓存键是否正确
- 查看日志中是否有错误信息

### 3. 内存使用过高

如果使用本地缓存且内存使用过高：
- 调整`ic.cache.local.max-size`参数
- 减少`ic.cache.local.expire-after-write`时间
- 考虑使用Redis缓存

## 版本历史

- **1.0.0**: 初始版本，支持Redis和本地缓存 
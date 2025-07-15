# IC Framework Cache 使用示例

## 基本使用

### 1. 注入缓存服务

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
    
    public void updateUser(User user) {
        // 更新数据库
        userRepository.save(user);
        
        // 删除缓存
        cacheService.remove("user:" + user.getId());
    }
}
```

### 2. 使用静态工具类

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

## 高级用法

### 1. 缓存穿透防护

```java
@Service
public class OrderService {
    
    public Order getOrderById(Long id) {
        String key = "order:" + id;
        
        return (Order) CacheUtils.get(key, k -> {
            Order order = orderRepository.findById(id);
            if (order == null) {
                // 缓存空值，防止缓存穿透
                CacheUtils.set(k, new NullValue(), 300); // 5分钟过期
                return null;
            }
            return order;
        });
    }
}
```

### 2. 批量缓存操作

```java
@Service
public class CategoryService {
    
    public List<Category> getCategoriesByIds(List<Long> ids) {
        List<Category> categories = new ArrayList<>();
        
        for (Long id : ids) {
            String key = "category:" + id;
            Category category = (Category) CacheUtils.get(key, k -> {
                return categoryRepository.findById(id);
            });
            if (category != null) {
                categories.add(category);
            }
        }
        
        return categories;
    }
}
```

### 3. 缓存预热

```java
@Service
public class ConfigService {
    
    @PostConstruct
    public void warmUpCache() {
        // 应用启动时预热缓存
        List<Config> configs = configRepository.findAll();
        for (Config config : configs) {
            String key = "config:" + config.getKey();
            CacheUtils.set(key, config.getValue(), 24 * 60 * 60); // 24小时过期
        }
    }
}
```

### 4. 缓存统计

```java
@Service
public class CacheMonitorService {
    
    public void monitorCache() {
        // 检查是否使用Redis
        boolean useRedis = CacheUtils.useRedis();
        System.out.println("Using Redis: " + useRedis);
        
        // 检查缓存是否存在
        String testKey = "monitor:test";
        CacheUtils.set(testKey, "test-value", 60);
        
        if (CacheUtils.exists(testKey)) {
            long ttl = CacheUtils.getExpire(testKey);
            System.out.println("Cache TTL: " + ttl + " seconds");
        }
        
        // 清理测试缓存
        CacheUtils.remove(testKey);
    }
}
```

## 实际应用场景

### 1. 用户会话管理

```java
@Service
public class SessionService {
    
    private static final int SESSION_TIMEOUT = 30 * 60; // 30分钟
    
    public void createSession(String sessionId, UserSession session) {
        String key = "session:" + sessionId;
        CacheUtils.set(key, session, SESSION_TIMEOUT);
    }
    
    public UserSession getSession(String sessionId) {
        String key = "session:" + sessionId;
        return (UserSession) CacheUtils.get(key);
    }
    
    public void extendSession(String sessionId) {
        String key = "session:" + sessionId;
        UserSession session = (UserSession) CacheUtils.get(key);
        if (session != null) {
            CacheUtils.set(key, session, SESSION_TIMEOUT);
        }
    }
    
    public void removeSession(String sessionId) {
        String key = "session:" + sessionId;
        CacheUtils.remove(key);
    }
}
```

### 2. 商品库存缓存

```java
@Service
public class InventoryService {
    
    public Integer getStock(Long productId) {
        String key = "stock:" + productId;
        
        return (Integer) CacheUtils.get(key, k -> {
            return inventoryRepository.findStockByProductId(productId);
        });
    }
    
    public void updateStock(Long productId, Integer quantity) {
        // 更新数据库
        inventoryRepository.updateStock(productId, quantity);
        
        // 更新缓存
        String key = "stock:" + productId;
        CacheUtils.set(key, quantity, 60 * 60); // 1小时过期
    }
    
    public void decreaseStock(Long productId, Integer quantity) {
        // 原子性减少库存
        String key = "stock:" + productId;
        Integer currentStock = (Integer) CacheUtils.get(key);
        
        if (currentStock != null && currentStock >= quantity) {
            Integer newStock = currentStock - quantity;
            CacheUtils.set(key, newStock, 60 * 60);
            
            // 异步更新数据库
            CompletableFuture.runAsync(() -> {
                inventoryRepository.updateStock(productId, newStock);
            });
        } else {
            throw new InsufficientStockException("库存不足");
        }
    }
}
```

### 3. 系统配置缓存

```java
@Service
public class SystemConfigService {
    
    public String getConfig(String configKey) {
        String key = "config:" + configKey;
        
        return (String) CacheUtils.get(key, k -> {
            return configRepository.findValueByKey(configKey);
        });
    }
    
    public void setConfig(String configKey, String value) {
        // 更新数据库
        configRepository.updateValue(configKey, value);
        
        // 更新缓存
        String key = "config:" + configKey;
        CacheUtils.set(key, value, 24 * 60 * 60); // 24小时过期
    }
    
    public void refreshConfig(String configKey) {
        String key = "config:" + configKey;
        CacheUtils.remove(key);
    }
}
```

### 4. 接口限流

```java
@Service
public class RateLimitService {
    
    public boolean isAllowed(String userId, String api, int limit, int window) {
        String key = "rate_limit:" + userId + ":" + api;
        
        Integer count = (Integer) CacheUtils.get(key, k -> 0);
        
        if (count < limit) {
            CacheUtils.set(key, count + 1, window);
            return true;
        }
        
        return false;
    }
    
    public void resetRateLimit(String userId, String api) {
        String key = "rate_limit:" + userId + ":" + api;
        CacheUtils.remove(key);
    }
}
```

## 配置示例

### 1. 开发环境配置（无Redis）

```yaml
# application-dev.yml
# 不配置Redis，使用本地缓存
ic:
  cache:
    local:
      max-size: 5000
      expire-after-write: 300
```

### 2. 生产环境配置（有Redis）

```yaml
# application-prod.yml
spring:
  redis:
    host: redis-server
    port: 6379
    password: your-password
    database: 0
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms

ic:
  cache:
    local:
      max-size: 10000
      expire-after-write: 600
```

### 3. 测试环境配置

```yaml
# application-test.yml
spring:
  redis:
    host: localhost
    port: 6379
    database: 1  # 使用不同的数据库避免冲突

ic:
  cache:
    local:
      max-size: 1000
      expire-after-write: 60
```

## 性能优化建议

### 1. 合理设置缓存过期时间

```java
// 根据数据更新频率设置不同的过期时间
public class CacheExpirationStrategy {
    
    // 用户信息：30分钟
    public static final int USER_INFO_EXPIRE = 30 * 60;
    
    // 商品信息：1小时
    public static final int PRODUCT_INFO_EXPIRE = 60 * 60;
    
    // 系统配置：24小时
    public static final int CONFIG_EXPIRE = 24 * 60 * 60;
    
    // 统计数据：5分钟
    public static final int STATISTICS_EXPIRE = 5 * 60;
}
```

### 2. 使用缓存键前缀

```java
public class CacheKeyPrefix {
    
    public static final String USER = "user:";
    public static final String PRODUCT = "product:";
    public static final String ORDER = "order:";
    public static final String CONFIG = "config:";
    public static final String SESSION = "session:";
    public static final String STOCK = "stock:";
    
    public static String userKey(Long userId) {
        return USER + userId;
    }
    
    public static String productKey(Long productId) {
        return PRODUCT + productId;
    }
}
```

### 3. 批量操作优化

```java
@Service
public class BatchCacheService {
    
    public void batchSet(Map<String, Object> data, int expireSeconds) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            CacheUtils.set(entry.getKey(), entry.getValue(), expireSeconds);
        }
    }
    
    public Map<String, Object> batchGet(List<String> keys) {
        Map<String, Object> result = new HashMap<>();
        for (String key : keys) {
            Object value = CacheUtils.get(key);
            if (value != null) {
                result.put(key, value);
            }
        }
        return result;
    }
}
```

这些示例展示了IC Framework Cache模块的各种使用场景和最佳实践。根据实际需求，可以灵活组合使用这些功能。 
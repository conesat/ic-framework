# 基础核心说明（Core 模块）

`ic-framework-core` 是整个框架的基础设施层，提供了启动器、基础抽象类、全局异常处理以及丰富的核心工具类。

## 框架启动器：`IcFrameworkStarter`

系统启动时，`IcFrameworkStarter` 会自动执行。其主要职责包括：
- 打印框架 Banner 和版本信息。
- 确认框架环境加载完成。

## 核心工具库

`cn.icframework.core.utils` 包下提供了大量开箱即用的工具类：

### 1. 安全与加密
- **`AESUtils`**: 对称加密工具。
- **`RsaUtils`**: 非对称加密工具（RSA）。
- **`MD5Util`**: 哈希抽象。

### 2. 网络与系统
- **`HttpUtils`**: 轻量级 HTTP 请求客户端。
- **`IpUtils`**: 解析客户端 IP 地址及归属地信息。
- **`SpringContextUtil`**: 静态获取 Spring Bean 的入口。

### 3. 对象与集合
- **`BeanUtils`**: 对象属性拷贝与反射操作。
- **`MapUtils`**: Map 操作增强。
- **`Assert`**: 流程断言，配合全局异常处理使用。

### 4. 文件与时间
- **`FileUtils`**: 文件读写、路径处理工具。
- **`LocalDateTimeUtils`**: Java 8 新日期时间 API 的快捷操作。

## 基础架构模型

在 `cn.icframework.core.basic` 中定义了框架的标准化开发规范：
- **API 层**: 提供基础请求处理抽象。
- **Service 层**: 业务逻辑标准化父类。
- **WrapperBuilder**: 动态 SQL 构造器的底层支撑。
- **POJO**: 统一的视图对象（VO）和数据传输对象（DTO）基类。

## 开发者提示

- 绝大部分工具类都设计为静态直调用方式，无需实例化。
- 在业务代码中，建议优先使用 `Assert` 类进行参数校验，它会自动抛出框架预定义的异常并被全局拦截器捕获。

---

> 详细实现请参考：`cn.icframework.core` 包下的相关源码。

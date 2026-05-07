# 通用工具说明（Common 模块）

`ic-framework-common` 模块汇集了多个模块共用的常量、枚举、接口定义以及底层工具类，是框架中最底层的原子依赖。

## 常用工具类

### 1. 数据转换：`ConvertUtils`
- 提供类型转换、金额格式化、单位换算等常用功能。
- 解决 Java 基础类型与业务对象之间的快速映射。

### 2. 内存缓存实现
在 `cn.icframework.common.utils.cache` 包下，框架实现了几种基础的缓存淘汰算法：
- **`LFUCache`**: 最少使用频率算法实现。
- **`LRUCache`**: 最近最久未使用算法实现。
- **`CacheUtil`**: 底层缓存操作的辅助工具。

> Note: 这些底层实现主要供 `ic-framework-cache` 模块调用，普通业务开发建议使用 `CacheUtils` 静态工具类。

## 通用常量与枚举

- **`cn.icframework.common.consts`**: 定义了全局通用的常量（如错误码、状态标识）。
- **`cn.icframework.common.enums`**: 提供业务无关的基础枚举定义。

## 接口与 Lambda 增强

- **`cn.icframework.common.interfaces`**: 定义了跨模块调用的标准契约。
- **`cn.icframework.common.lambda`**: 提供函数式接口的增强，用于处理流式操作或回调逻辑。

## 开发者提示

- `ic-framework-common` 不包含任何业务逻辑，仅保留纯度较高的基础脚手架代码。
- 引入此模块几乎不占包体积，是构建自定义 IC 模块的必备依赖。

---

> 详细实现请参考：`cn.icframework.common` 包下的相关源码。

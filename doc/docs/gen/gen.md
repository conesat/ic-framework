# 代码生成器说明（Gen 模块）

IC Framework 内置了一个强大的代码生成引擎，支持从 Java 实体类直接生成全栈代码（包括 Java 后端和 Vue 前端）。

## 核心类：`Generator`

`Generator` 类采用了流畅的链式调用（Fluent API）方式进行配置。

### 常用配置方法

- `moduleName(String)`: 设置模块名称（如 "system"）。
- `packageName(String)`: 设置基础包名（如 "cn.icframework"）。
- `modelName(String)`: 设置模型名称（首字母大写）。
- `author(String)`: 设置作者信息，默认为 "create by ic gen"。
- `tableClass(Class)`: **核心方法**。指定作为生成基准的实体类，系统将自动解析其字段、包名等信息。
- `savePath(javaPath, vuePath)`: 设置生成的代码保存到的本地绝对路径。
- `build()`: 执行生成逻辑。

## 使用示例

```java
public void generateCode() {
    new Generator()
        .author("Antigravity")
        .moduleName("order")
        .tableClass(OrderEntity.class) // 以本地实体类为模版
        .savePath("/your/project/src/main/java", "/your/project/vue/src")
        .build();
}
```

## 生成内容列表

执行 `build()` 后，系统将自动生成以下内容：

### 1. Java 后端
- **API**: 请求控制器。
- **Service & Impl**: 业务逻辑层。
- **Mapper**: 数据库访问层及对应的注解/配置文件。
- **Pojo (O/Model)**: 数据传输对象及视图对象。
- **WrapperBuilder**: 查询构造器，用于支持动态复杂查询。

### 2. Vue 前端
- **Api**: 用于对接后端接口的 JS 模块。
- **Index**: 列表展示页面。
- **Edit**: 新增/编辑弹窗或页面。
- **Router**: 对应的路由配置代码片段。

## 开发者提示

- 生成器默认会根据 `tableClass` 的注解（如 `@Table`）解析数据库映射信息。
- 生成的 `WrapperBuilder` 可以极大地简化后端的多条件查询编写。
- 如需自定义生成模板，可以修改 `ic-framework-gen-template` 模块中的 `.ftl` 或相关模版文件。

---

> 详细实现请参考：`cn.icframework.gen` 包下的相关源码。
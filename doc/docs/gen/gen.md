# 代码生成器说明（gen 模块）

## 1. 模块作用

- 提供 Java/Vue 等多端代码生成能力
- 支持模板自定义、批量生成

## 2. 主要功能

- Generator 代码生成主类
- GenUtils 工具类
- 支持 Java、Vue 等模板

## 3. 用法示例

```java
Generator generator = new Generator();
generator.generate("User", "user");
```

## 4. 扩展点

- 可自定义模板（gen-template 模块）
- 支持多端代码生成

## 5. 常见问题

- **如何自定义模板？**
  在 gen-template 模块中添加自定义模板。

---

> 详细用法请参考源码及注释。 
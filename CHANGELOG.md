# 变更日志

本文档记录了IC Framework的所有重要变更。

## [1.0.0] - 2024-12-19

### 🎉 首次发布

#### 新增功能
- **认证授权模块** (`ic-framework-auth`)
  - JWT Token认证支持
  - 基于角色的权限控制(RBAC)
  - 自动权限初始化
  - 跨域配置支持
  - 用户在线状态管理

- **通用工具模块** (`ic-framework-common`)
  - 配置属性管理
  - 常量定义
  - 工具类集合
  - 枚举类型支持
  - Lambda工具类

- **核心功能模块** (`ic-framework-core`)
  - 缓存管理（Redis + Caffeine）
  - 统一异常处理
  - 全局响应封装
  - 参数校验支持
  - 日志记录工具

- **MyBatis扩展模块** (`ic-framework-mybatis`)
  - 增强的查询构建器
  - 链式SQL查询
  - 自动分页支持
  - 复杂查询支持
  - 类型安全的查询条件

- **代码生成模块** (`ic-framework-gen`)
  - 自动生成CRUD代码
  - Vue3前端代码生成
  - 数据库表结构同步
  - 模板化代码生成
  - 批量代码生成

- **注解模块** (`ic-framework-annotation`)
  - API缓存注解
  - 权限验证注解
  - 作者信息注解
  - 自定义注解支持

- **数据库工具模块** (`ic-framework-dber`)
  - 数据库表结构分析
  - DDL自动生成
  - 数据库同步工具

- **Spring Boot Starter** (`ic-framework-spring-boot-starter`)
  - 一键启动配置
  - 自动配置所有模块
  - 零配置即可使用

#### 技术特性
- 基于Spring Boot 3.5.3
- 支持Java 21
- 模块化架构设计
- 完整的自动配置
- 统一的依赖管理
- 规范的代码结构

#### 文档
- 完整的README文档
- 各模块详细使用说明
- API文档和示例
- 最佳实践指南

#### 示例项目
- 完整的示例代码
- 配置示例
- 使用教程

## 版本说明

### 版本号规则
我们使用 [语义化版本控制](https://semver.org/lang/zh-CN/) 进行版本管理：

- **主版本号**：不兼容的API修改
- **次版本号**：向下兼容的功能性新增
- **修订号**：向下兼容的问题修正

### 发布周期
- **主版本**：重大功能更新，可能包含破坏性变更
- **次版本**：新功能添加，向下兼容
- **修订版本**：Bug修复和安全更新

## 升级指南

### 从0.x版本升级到1.0.0

#### 破坏性变更
1. **包名变更**：所有包名从 `cn.icframework` 开始
2. **配置变更**：配置文件结构有所调整
3. **API变更**：部分API接口进行了重构

#### 升级步骤
1. 更新依赖版本到1.0.0
2. 检查并更新包名引用
3. 更新配置文件结构
4. 测试所有功能

#### 兼容性说明
- 支持Spring Boot 3.5.3+
- 需要Java 21+
- 建议使用Maven 3.6+

## 贡献指南

### 如何贡献
1. Fork 本仓库
2. 创建特性分支
3. 提交更改
4. 创建Pull Request

### 提交规范
我们使用 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

- `feat:` 新功能
- `fix:` Bug修复
- `docs:` 文档更新
- `style:` 代码格式调整
- `refactor:` 代码重构
- `test:` 测试相关
- `chore:` 构建过程或辅助工具的变动

### 发布流程
1. 开发新功能
2. 编写测试
3. 更新文档
4. 创建Release
5. 发布到Maven中央仓库

## 支持

### 获取帮助
- [官方文档](https://icframework.chinahg.top)
- [GitHub Issues](https://github.com/your-org/ic-framework/issues)
- [讨论区](https://github.com/your-org/ic-framework/discussions)

### 报告问题
如果您发现了Bug或有功能建议，请：
1. 搜索现有的Issues
2. 创建新的Issue
3. 提供详细的复现步骤

### 安全漏洞
如果您发现了安全漏洞，请：
1. 不要公开报告
2. 发送邮件到 1092501244@qq.com
3. 我们会尽快响应

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

---

**注意**：本文档会随着项目的发展持续更新。建议定期查看最新版本。 
# 导入与启动

这一页只做一件事：把示例工程跑起来。

如果你是第一次接触 IC Framework，建议先跑通 `ic-framework-service`，不要一上来就啃底层源码。

## 1. 先知道你要启动什么

通常日常开发会基于 `ic-framework-service` 进行，它已经把这些东西组织好了：

- `ic-framework-system`：系统基础模块
- `ic-framework-project`：业务模块
- `_web/admin`：后台前端

接下来的步骤，默认都是基于这个集成工程。

## 2. 环境准备

建议至少准备：

- JDK 21+
- Maven 3.9+
- MySQL
- Node.js
- pnpm 或 npm

当前仓库里有 Java 25 相关配置和说明，但如果你只是先把工程跑起来，优先保证本地 Maven 与 JDK 版本能匹配项目即可。

## 3. 拉取示例工程

仓库地址：

- [ic-framework-service GitHub](https://github.com/conesat/ic-framework-service)

默认分支是 `main`。如果你要看 hotel 示例，需要切到对应分支。

## 4. 导入 IDEA 后先做一件事

`ic-mybatis` 会在：

```text
target/generated-sources/annotations
```

生成 def 文件。这个目录需要在 IDEA 里标记为 source，否则你会看到大量 `xxxDef` 找不到。

![](/public/imgs/project-setting.png)

## 5. 配置后端

### 数据库

先创建数据库，然后修改：

- `ic-framework-project/src/main/resources/application-dev.yml`
- `ic-framework-project/src/main/resources/application-prod.yml`

当前示例里能看到这些配置项：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3307/ic
    username: root
    password: your-password
```

### MyBatis 配置

项目默认会读取：

```yaml
mybatis:
  config-location: classpath:mybatis-config.xml
```

开发环境里也可能切到：

```yaml
mybatis:
  config-location: classpath:mybatis-config-dev.xml
```

### 文件存储

如果你暂时不处理上传文件，可以先把相关配置留空。  
system 模块当前最明确支持的是 `minio / oss`，示例配置里还能看到业务层的 `fastdfs` 配置。

## 6. 启动后端

运行：

```text
cn.icframework.project.ProjectApplication
```

或者在 `ic-framework-project` 目录下执行 Spring Boot 启动命令。

正常启动后，你应该重点确认这几件事：

- DDL 自动执行
- 应用启动成功
- 端口正常监听
- context-path 为 `/api`

当前示例配置里可以看到：

```yaml
server:
  port: 9998
  servlet:
    context-path: /api
```

所以后端地址通常是：

```text
http://localhost:9998/api
```

## 7. 启动前端

后台前端目录：

```text
_web/admin
```

先配置环境变量文件，例如：

- `.env`
- `.env.development`
- `.env.site`
- `.env.test`

最关键的是这几个值：

```env
VITE_BASE_URL=/
VITE_IS_REQUEST_PROXY=true
VITE_API_URL=http://localhost:9998
VITE_API_URL_PREFIX=/api
```

然后安装依赖：

```bash
pnpm i
```

启动开发服务器：

```bash
pnpm run dev
```

如果你用 npm，也可以改成对应命令。

## 8. 首次启动时会发生什么

第一次启动通常会看到这些行为：

- `dber` 根据实体创建或调整表结构
- system 模块执行初始化任务
- 权限、菜单、岗位等基础数据按配置写入

也就是说，IC Framework 不是“项目能启动但系统空白”，而是尽量把基础层一起初始化出来。

## 9. 如果启动失败，优先检查这些点

最常见的问题基本集中在这里：

1. JDK 版本不匹配
2. Maven 使用的不是预期 JDK
3. `generated-sources/annotations` 没标记 source
4. 数据库没创建或连接信息不对
5. 前端 `VITE_API_URL` 配错端口
6. `mybatis-config.xml` 没正确加载

## 10. 跑起来之后下一步看什么

后端和前端都能启动后，推荐继续看：

1. [/docs/introduction/structure](/docs/introduction/structure)
2. [/docs/start/java](/docs/start/java)
3. [/docs/system/system](/docs/system/system)

这样你就会知道：

- 业务代码应该写在哪
- system 模块已经帮你做了什么
- 后面新增一个模块应该照什么结构写

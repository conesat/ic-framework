# 前端开发结构

后台前端基于 Vue 3 + TDesign，目录在：

```text
ic-framework-service/_web/admin
```

这一页只讲“你平时写页面主要改哪里”。

## 1. 你最常接触的目录

业务开发通常只要先盯住这几块：

- `src/pages/project`：业务页面
- `src/api/project`：业务接口
- `src/api/model`：前端数据模型
- `src/router/modules`：路由配置

系统自带页面大多在：

- `src/pages/sys`
- `src/api/sys`

## 2. 一个业务模块在前端怎么落

最常见的做法是：

1. 在 `src/pages/project/模块名` 下建页面
2. 在 `src/api/project` 下补对应 API
3. 在路由里挂上页面
4. 菜单里配对应入口

也就是说，前端业务模块一般和后端模块是成对出现的。

## 3. 目录结构怎么理解

```text
src/
  api/
    common/
    model/
    project/
    sys/
  pages/
    login/
    project/
    sys/
  router/
    modules/
```

可以这样理解：

- `api`：怎么请求后端
- `pages`：页面长什么样
- `router`：页面如何进入

## 4. 环境配置

常见环境文件：

- `.env`
- `.env.development`
- `.env.site`
- `.env.test`

最关键的配置通常是：

```env
VITE_BASE_URL=/
VITE_IS_REQUEST_PROXY=true
VITE_API_URL=http://localhost:9998
VITE_API_URL_PREFIX=/api
```

真正开发时，最容易出问题的是后端端口配错，所以前后端地址最好先和启动页里的配置保持一致。

## 5. 和后端怎么配合最顺

比较推荐的配合方式是：

1. 后端先把 `Api + DTO + VO + WrapperBuilder` 定好
2. 前端再按列表页 / 编辑页 / 详情页去接
3. 菜单与路由最后一起挂

如果你已经使用代码生成器，前后端这套结构会更统一。

## 6. 写页面时优先参考哪里

最好的样板不是凭空造，而是先看：

- `src/pages/sys`
- `src/api/sys`

system 模块里已有大量标准后台页面，尤其适合参考：

- 列表页怎么查
- 编辑页怎么提交流程
- 路由和菜单怎么对应

## 7. 一句话建议

前端不要自己发明一套目录规则，沿着 `pages / api / router` 的现有结构扩展，后期维护会轻松很多。

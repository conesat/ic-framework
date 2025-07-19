# 中台设计

## 前言

中台使用 [tdesign](https://tdesign.tencent.com/starter/docs/vue-next/get-started)
作为前端框架。详细组件文档请参考 [tdesign-next](https://tdesign.tencent.com/vue-next/overview)。

中台页面位于 ic-framework-service/_web/admin

## 目录结构

>
只罗列关键文件夹。业务服务一般只关心/src/pages/project和/src/api/project。
- /src/pages/project：存放业务页面，例如作者的hotel项目就会单独放到 /src/pages/hotel。每个模块会在hotel下新增一个目录。
- /src/api/project：存放业务api，例如作者的hotel项目就会单独放到 /src/api/hotel。
- /src/api/model：如果需要定义ts数据模型，可以放这里，或者 /src/api/project
```
📁 src
    📁 api
        📁 common
        📁 model [后端返回的数据模型]
        📁 projet [业务api可以新建一个目录类似project]
        📁 sys [系统预置的api]
    📁 assets
    📁 config
    📁 constants
    📁 pages
        📁 dashboard [仪表盘]
            📁 base
            📁 detail
            📁 docs
        📁 login [登录页面]
        📁 project   [业务页面可以新建一个类似project的目录]
        📁 sys   [系统预置的页面]
            📁 chat
            📁 dept
            📁 ...
    📁 router
        📁 modules
```

### 配置文件
- ic-framework-service/_web/admin/.env: 正式环境配置
- ic-framework-service/_web/admin/.env.development: 开发环境配置
- ic-framework-service/_web/admin/.env.site: 站点环境配置【tdesign预留】
- ic-framework-service/_web/admin/.env.test: 测试环境配置

配置内容：
````
# 打包路径
VITE_BASE_URL = /
VITE_IS_REQUEST_PROXY = true
# 后台地址接口
VITE_API_URL = http://localhost:9999
# 接口前缀
VITE_API_URL_PREFIX = /api
````

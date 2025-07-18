# 集成平台

### 简介
ic-framework-service 是使用IcFramework开发的集成框架、包含鉴权、组织管理等基础功能。类似 [若依](https://gitee.com/y_project/RuoYi) 平台,
接下来的教程我们从 ic-framework-project 的使用开始。

### 目录结构

>
框架结构如下所示，system模块是系统预置的包含用户、部门、权限、消息、文件等

project是自行开发扩展的模块，当然也可以取其他名字

```
ic-framework-service
├─ic-framework-project [业务代码]
│  ├─src
│     ├─main
│        ├─java
│        └─resources
│            ├─i18n [国际化配置]
│            ├─init [初始化数据]
│            │  ├─menu [管理页面菜单配置]
│            │  ├─pos [岗位初始化]
│            │  └─rp [角色权限初始化]
│            └─sqls [sql更新脚本]
├─ic-framework-system [系统预置功能]
   ├─src
      ├─main
         ├─java
         │  └─cn.icframework.system
         │    ├─config [配置]
         │    ├─consts [常量]
         │    ├─enums [枚举]
         │    ├─module [业务模块集合]
         │    │  ├─setting [业务模块]
         │    │  │  ├─api [业务接口]
         │    │  │  ├─dao [mybatisMapper]
         │    │  │  ├─pojo [数据对象]
         │    │  │  │  ├─dto [入参]
         │    │  │  │  └─vo [出参]
         │    │  │  ├─service [业务]
         │    │  │  └─wrapperbuilder [sql构建]
         │    │  ├─...
         │    ├─runner [初始化]
         │    └─utils [工具类]
         └─resources
```

### resources/init/rp

该目录存放需要初始化的角色权限关系，下面存放两个文件role.json是初始化角色列表，rolePermissions.json是初始化角色权限关系。

````
init.rp
├─rolePermissions.json [角色权限]
└─role.json [角色]

````

### 导入项目

#### 后端

- #### clone或下载github项目
  地址：[ic-framework-service](https://github.com/conesat/ic-framework-service)
  ![](/public/imgs/service.png)

>

- ##### 设置source
  target/generated-sources/annotations下会生成IcMybatis生成的代码，需要设置为source
  （这块逻辑类似mybatis-flex：ps这是个很nice的框架ic-mybatis有许多地方都借鉴它）
  ![](/public/imgs/project-setting.png)

>

- ##### 配置数据库与oss
  oss用于存储上传文件，当然也可以改为本地存储或者fastdfs【但是作者还没完善】
  ![](/public/imgs/dev.png)

然后你就可以尝试启动project项目了

#### 前端

- ##### 配置
  添加配置
  ![](/public/imgs/web-dev.png)

- ##### 安装依赖
  终端进入/_web/admin 执行下面安装命令
  ```cmd
  npm i
  # 启动
  npm run dev

### 运行起来以后需要初始化

>
初始化需要秘钥
>
RLK2ihRbHkQlE99U3DIoQfEwlb8hR1Dz1K0icaFzIT0DXHJcZ7lw73VhyRNYmdZamcNjEx-lkTT4uc2DHxc6kvrB_Akb0DCmAk57xyCIhMbD75J94GpFp191I7HZ4U4YddHCLSpHDvYGmyGhGZnnMgQCmV1S8zY7QpV29GPA8YkLrAeW_5KlT5xPuJDNUibh_0Il>
BSE7b5GWBNSyUg6xXbx-h0Bta-gicHatNV7toED7P0IVGlCyW3OVTEJY_Q-1cYBMb6v1WJn2D8B5pGcuHeMmmEuGSenzbMCWERU2SwStPVLiqJNRDRD9WDVPBH7LDx0hVQ7OntxvoMYCj5xxtQ 
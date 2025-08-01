# JAVA设计

## 目录结构

>
框架结构如下所示，system模块是系统预置的包含用户、部门、权限、消息、文件等

project是自行开发扩展的模块，当然也可以取其他名字

```
ic-framework-system/
└── src/main/java/cn/icframework/system/
    ├── module/                    # 业务模块
    │   ├── user/                 # 用户模块
    │   │   ├── User.java         # 实体类
    │   │   ├── api/              # 控制器层
    │   │   │   ├── ApiSysUser.java
    │   │   │   ├── ApiAppUser.java
    │   │   │   └── ApiPublicUser.java
    │   │   ├── service/          # 服务层
    │   │   │   ├── UserService.java
    │   │   │   └── IUserInfoProvider.java
    │   │   ├── dao/              # 数据访问层
    │   │   │   └── UserMapper.java
    │   │   ├── pojo/             # 数据传输对象
    │   │   │   ├── dto/          # 请求对象
    │   │   │   └── vo/           # 响应对象
    │   │   └── wrapperbuilder/   # 查询构建器
    │   │       └── UserWrapperBuilder.java
    │   ├── role/                 # 角色模块
    │   ├── menu/                 # 菜单模块
    │   └── ...                   # 其他模块
    ├── common/                   # 公共组件
    ├── config/                   # 配置类
    └── utils/                    # 工具类
```

### resources/init/rp

该目录存放需要初始化的角色权限关系，下面存放两个文件role.json是初始化角色列表，rolePermissions.json是初始化角色权限关系。

````
📁init
    📁rp
        rolePermissions.json [角色权限]
        role.json [角色]

````

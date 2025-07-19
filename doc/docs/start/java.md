# JAVA设计

## 目录结构

>
框架结构如下所示，system模块是系统预置的包含用户、部门、权限、消息、文件等

project是自行开发扩展的模块，当然也可以取其他名字

```
📁ic-framework-service
    📁ic-framework-project [业务代码]
        📁src
            📁main
                📁java
                📁resources
                    📁i18n [国际化配置]
                    📁init [初始化数据]
                        📁menu [管理页面菜单配置]
                        📁pos [岗位初始化]
                        📁rp [角色权限初始化]
                    📁sqls [sql更新脚本]
    📁ic-framework-system [系统预置功能]
        📁src
            📁main
                📁java
                    📁cn.icframework.system
                        📁config [配置]
                        📁consts [常量]
                        📁enums [枚举]
                        📁module [业务模块集合]
                            📁setting [业务模块]
                                📁api [业务接口]
                                📁dao [mybatisMapper]
                                📁pojo [数据对象]
                                    📁dto [入参]
                                    📁vo [出参]
                                📁service [业务]
                                📁wrapperbuilder [sql构建]
                            📁...
                        📁runner [初始化]
                        📁utils [工具类]
                📁resources
```

### resources/init/rp

该目录存放需要初始化的角色权限关系，下面存放两个文件role.json是初始化角色列表，rolePermissions.json是初始化角色权限关系。

````
📁init
    📁rp
        rolePermissions.json [角色权限]
        role.json [角色]

````

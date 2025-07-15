# 角色权限

系统启动时会扫描 <a>@RequireAuth</a> 注解，将需要过滤权限的Controller，初始化到数据库中。
系统初始化后，可以在后台为角色添加对应的权限。也可以通过以下的方式进行初始化。

## 角色权限初始化

resources/init/rp

该目录存放需要初始化的角色权限关系，下面存放两个文件role.json是初始化角色列表，rolePermissions.json是初始化角色权限关系。

````
init.rp
├─rolePermissions.json
└─role.json

````

::: info role.json示例
sign是角色标识不能重复，userType是角色类型，name是角色名称

userType：manager是系统默认的，后台管理角色类型。其他类型需要自定义，可以参考 cn.icframework.system.consts.UserType。

***注意：sign admin已被超管占用***

````json
[
  {
    "sign": "manager",
    "userType": "manager",
    "name": "普通管理员"
  },
  {
    "sign": "R1",
    "userType": "mp",
    "name": "普通小程序用户"
  }
]
````

:::

::: info rolePermissions.json示例
sign是角色标识，permissions是授权的请求路径，

permissions.groupPath是分组路径，对应Controller的路径。需要用:代替/

permissions.paths是Controller对应方法的路径。需要用:代替/。如果是RESTful API，可以不用写路径。直接写对应的请求类型 get、post等

userType：manager是系统默认的，后台管理角色类型。其他类型需要自定义，可以参考 cn.icframework.system.consts.UserType。

***注意：sign admin已被超管占用***

````json
[
  {
    "sign": "manager",
    "permissions": [
      {
        "groupPath": ":manage:dept",
        "paths": [
          ":delete",
          ":all"
        ]
      }
    ]
  }
]
````
:::


## @RequireAuth 注解使用

以ApiManageDept为例，这是个后台的部门接口类。

添加 @RequireAuth(userType = UserType.MANAGER) 代表需要校验整个类所有接口的权限，并且用户类型是UserType.MANAGER

同时启动时系统扫描到该类，会将该类下所有接口方法，添加到权限列表中，后继即可在后台分配这些接口权限到对应角色。


```java
@RestController
@RequestMapping(value = Api.API_MANAGE + "/dept", name = "部门（管理员）")
@RequireAuth(userType = UserType.MANAGER)
@RequiredArgsConstructor
public class ApiManageDept extends BasicApi {
    
    /**
     * 获取单个详情
     *
     * @param id [Serializable] *id
     * @return Response<DeptVO>
     */
    @GetMapping(value = "/{id}", name = "获取详情")
    public Response<DeptVO> detail(@PathVariable("id") Serializable id) {
        ...
    }
    
    ...
}
```

注解还支持角色校验，以下就是指定admin角色可以访问，role参数对应的是角色的sign值。

```java
@RequireAuth(userType = UserType.MANAGER, role = "admin")
```

如果添加了role默认会判断角色与权限，只要满足一个条件就能访问。

如果你需要校验角色权限必须同时满足的话，还需要添加一个设置 mixRP = false。

```java
@RequireAuth(userType = UserType.MANAGER, role = "admin", mixRP = false)
```

如果需要校验token但是无需角色或者权限的话，可以添加一个设置 onlyToken = true。

```java
@RequireAuth(userType = UserType.MANAGER, onlyToken = true)
```
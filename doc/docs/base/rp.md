# 角色权限

## 说明
系统启动时会扫描 <a>@RequireAuth</a> 注解，将需要过滤权限的Controller，初始化到数据库中。
系统初始化后，可以在后台为角色添加对应的权限。也可以通过以下的方式进行初始化。

## 如何再次初始化
系统初始化以后会记录md5如果接口或者初始化json文件没有变化，不会再次初始。
如果再次初始画，需要删除md5目录或者对应文件。
>
![](/public/imgs/init_md5.png)

> 疑问 - 为什么没有角色权限初始化记录？
> 
> 角色权限是在后台页面配置的，为了不改动用户配置内容，一旦初始化除非角色变化，否则都不会重新初始化角色权限关系。

## 角色权限初始化

resources/init/rp

该目录存放需要初始化的角色权限关系，下面存放两个文件role.json是初始化角色列表，rolePermissions.json是初始化角色权限关系。

````
📁init
    📁rp
        rolePermissions.json
        role.json
````

### role.json

::: info 说明
- sign：角色标识不能重复
- userType：角色类型
- name：角色名称

userType：manager是系统默认的，后台管理角色类型。其他类型需要自定义，可以参考 cn.icframework.system.consts.UserType。

***注意：sign:admin已被超管占用***
:::

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


### rolePermissions.json
::: info 说明
- sign：角色标识
- permissions：授权的请求路径
- permissions.groupPath：分组路径，对应Controller的路径。需要用:代替/
- permissions.paths：Controller对应方法的路径。需要用:代替/。如果是RESTful API，可以不用写路径。直接写对应的请求类型 get、post等
- permissions.all：否授予路径下所有方法权限
:::
````json
[
  {
    "sign": "manager",
    "permissions": [
      {
        "groupPath": ":sys:dept",
        "all": true
      },
      {
        "groupPath": ":sys:pos",
        "paths": [
          ":get",
          ":delete",
          ":all"
        ]
      }
    ]
  }
]
````


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
    }
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



## 角色权限配置示例
假如你有一个ApiSysDept controller如下：
```java
/**
 * @author create by ic gen
 * @since 2023/06/21
 */
@RestController
@RequestMapping(value = Api.API_SYS + "/dept", name = "部门")
@RequireAuth(userType = UserType.SYSTEM_USER)
public class ApiSysDept extends BasicApi {

    /**
     * 获取单个详情
     */
    @GetMapping(value = "/item/{id}", name = "获取详情")
    public Response<DeptVO> detail(@PathVariable("id") Serializable id) {
    }

    /**
     * 删除
     */
    @DeleteMapping(name = "删除")
    public Response<Void> delete(@RequestParam("ids") List<Serializable> ids) {
    }

    /**
     * 编辑或者保存
     */
    @PutMapping(name = "编辑")
    public Response<Void> edit(@Validated DeptDTO form) {
    }

    /**
     * 新增
     */
    @PostMapping(name = "新增")
    public Response<Void> create(@Validated DeptDTO form) {
    }

    /**
     * 获取列表
     */
    @PostMapping(value = "/page", name = "分页查询")
    public PageResponse<DeptVO> page(HttpServletRequest request, PageRequest page) {
    }
}
```

如果想为角色sign=manager的角色授予 /sys/dept 所有接口权限，如下：
````json
[
  {
    "sign": "manager",
    "permissions": [
      {
        "groupPath": ":sys:dept",
        "all": true
      }
    ]
  }
]
````
如果只想授予detail和delete方法权限如下：
````json
[
  {
    "sign": "manager",
    "permissions": [
      {
        "groupPath": ":sys:dept",
        "paths": [
          ":item:{id}",
          ":delete"
        ]
      }
    ]
  }
]
````
# 系统初始化

`ic-framework-system` 启动时会自动做三类初始化：

- 角色与角色权限
- 菜单
- 岗位

这些初始化不是手工一条条维护，而是通过资源文件和 MD5 变更检测自动完成。

## 1. 初始化发生在什么时候

系统启动完成后，会由 `InitRunner` 触发：

- `RpInit`
- `MenuInit`
- `PosInit`

所以你第一次把项目跑起来时，通常会看到权限、菜单、岗位等基础数据被写入数据库。

## 2. 角色与角色权限

角色与角色权限由这两个文件驱动：

```text
resources/init/rp/roles.json
resources/init/rp/rolePermissions.json
```

### `roles.json`

这里定义系统里的角色基础信息：

- `sign`：角色标识
- `userType`：用户类型
- `name`：角色名称

示例：

```json
[
  {
    "sign": "manager",
    "userType": "SYSTEM_USER",
    "name": "管理员"
  }
]
```

### `rolePermissions.json`

这里定义角色拥有哪些权限。

常见写法有两种：

- 某个权限组下全部权限
- 某个权限组下指定部分权限

示例：

```json
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
```

## 3. 岗位初始化

岗位初始化文件位于：

```text
resources/init/pos/pos.json
```

岗位常见字段：

- `name`：岗位名称
- `sign`：岗位标识
- `level`：职级
- `status`：状态

示例：

```json
[
  {
    "name": "总经理",
    "sign": "P1",
    "level": 1,
    "status": 1
  }
]
```

## 4. 菜单初始化

菜单初始化读取：

```text
resources/init/menu/*.json
```

菜单定义会按照树结构写入数据库，典型字段包括：

- `path`
- `name`
- `url`
- `icon`
- `redirect`
- `children`

这部分通常和前端页面目录一起配合使用。

## 5. 为什么不会重复初始化

系统会记录 MD5。

只要资源文件内容没变，下一次启动就不会重复执行同一批初始化逻辑。  
这样可以避免你每次重启都把后台配置洗掉。

## 6. 什么时候需要重新初始化

常见场景：

- 你改了 `roles.json`
- 你改了 `rolePermissions.json`
- 你改了 `pos.json`
- 你改了 `menu/*.json`

如果要强制重新初始化，通常需要清掉对应的 MD5 记录再启动。

## 7. 开发者该怎么理解这套机制

把它理解成三件事就够了：

1. 权限点由代码扫描出来
2. 角色和岗位由资源文件定义
3. 启动时自动把它们同步到数据库

这样你新增接口、菜单、岗位时，就有一套统一的初始化入口。

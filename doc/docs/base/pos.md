# 岗位初始化

## 说明

初始化文件位于 resources/init/pos/pos.json

## 如何再次初始化

参考角色权限初始化

## 岗位初始化文件
````
📁init
    📁pos
        pos.json

````

::: info pos.json示例

name：岗位名称

sign：岗位标识不能重复

level：职级越大职级越高

status：状态1启用，0禁用

````json
[
  {
    "name": "总经理",
    "sign": "P1",
    "level": 1,
    "status": 1
  },
  {
    "name": "前台经理",
    "sign": "P2",
    "level": 2,
    "status": 1
  }
]
````
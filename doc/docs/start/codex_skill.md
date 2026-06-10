# Codex Skill 生成 CRUD

`ic-framework-crud-gen` 是给 Codex 使用的本地 skill，用来按 IC Framework 的标准结构生成 CRUD 模块。

它会先根据字段信息生成标准实体类，再调用项目内置的 `ic-framework-gen` 生成 Java 后端和 Vue 管理端代码。

## 1. 安装位置

把整个 `ic-framework-crud-gen` 目录放到 Codex 的 skills 目录下：

```text
~/.codex/skills/ic-framework-crud-gen
```

如果你的 `CODEX_HOME` 不是默认路径，则放到：

```text
$CODEX_HOME/skills/ic-framework-crud-gen
```

目录里至少应包含：

```text
SKILL.md
scripts/render_ic_entity.py
references/generator-contract.md
references/field-spec.md
```

安装完成后，重启 Codex，让它重新加载本地 skill 列表。

## 2. 对话里怎么使用

Codex 不是通过命令行调用这个 skill，也不是在项目里点菜单调用。

在聊天里明确点名即可：

```text
使用 ic-framework-crud-gen，帮我生成一个 IC Framework CRUD 模块。
```

或者：

```text
请用 ic-framework-crud-gen 处理下面这个表结构。
```

如果一开始没有提供完整字段，Codex 会继续追问模块名、实体名、表名、主键和字段列表。

## 3. 推荐输入方式

简单描述即可，例如：

```text
使用 ic-framework-crud-gen，帮我生成酒店用户的 CRUD 模块。

模块名：hotel
实体类名：HotelUser
表名：hotel_user
表注释：酒店用户
主键：id，Long，SNOWFLAKE
字段：
- name，String，名称，必填
- phone，String，手机号，必填
- vip，Boolean，是否vip，默认 false
```

也可以提供结构化参数：

```yaml
module_name: hotel
model_name: HotelUser
table_name: hotel_user
table_comment: 酒店用户
author: ic
java_src_root: /abs/project/src/main/java
vue_src_root: /abs/project/_web/admin/src
package_name: cn.icframework.hotel.module.hoteluser
id:
  name: id
  java_type: Long
  id_type: SNOWFLAKE
fields:
  - name: name
    java_type: String
    column: name
    comment: 名称
    not_null: true
  - name: phone
    java_type: String
    column: phone
    comment: 手机号
    not_null: true
```

## 4. 生成内容

执行后通常会生成：

- 实体类
- `ApiSys<Model>.java`
- `ApiPublic<Model>.java`
- `ApiApp<Model>.java`
- `Mapper`
- `Service`
- `DTO / VO / VOConverter`
- `WrapperBuilder`
- 管理端 `Api<Model>.ts`
- 管理端列表、编辑、选择页面
- 管理端路由文件

## 5. 注意事项

- 这个 skill 依赖项目内置的 `cn.icframework.gen.Generator`，不是单独手写模板。
- 如果目标文件已存在，生成器会跳过部分文件，避免覆盖已有代码。
- 生成后的页面是通用 CRUD 页面，复杂交互仍建议按业务继续微调。
- 更完整的 skill 文件和示例在项目根目录的 `skill/ic-framework-crud-gen` 下。

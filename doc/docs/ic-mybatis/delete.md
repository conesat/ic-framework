# IC-Mybatis 删除

IC-Mybatis 提供了多种物理删除方式，包括根据主键删除、批量删除以及通过 `SqlWrapper` 条件删除。

> 以下示例是集成 `BasicService` 的调用方式。

## 根据 ID 删除

### 1. 删除单条记录
```java
// 根据主键 ID 删除
userService.deleteById(1);
```

### 2. 批量删除
```java
// 根据主键 ID 集合批量删除
List<Integer> ids = Arrays.asList(1, 2, 3);
userService.deleteByIds(ids);
```

## 条件删除 (SqlWrapper)

你可以使用 `SqlWrapper` 构造复杂的删除条件：

```java
UserDef userDef = UserDef.table();

// DELETE FROM user WHERE name = '张三'
SqlWrapper deleteWrapper = DELETE()
        .FROM(userDef)
        .WHERE(userDef.name.eq("张三"));

userService.delete(deleteWrapper);
```

## 开发者提示

- 框架目前默认执行的是 **物理删除**。如果需要逻辑删除（Soft Delete），建议在业务层通过 `update` 操作实现，或者在查询时过滤状态字段。
- `deleteByIds` 方法在底层会自动处理 ID 集合的拼接逻辑。
- 使用 `SqlWrapper` 删除时，务必带上 `WHERE` 条件，否则可能会导致全表删除（框架通常会对此进行安全检查，但仍需谨慎）。

---

> 详细实现请参考：`cn.icframework.mybatis.mapper.BasicMapper` 接口。

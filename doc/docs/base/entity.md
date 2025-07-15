# 表实体

实体使用 <a>@Table</a> 标注

## 基本使用

以下是个实体示例

````java
@Getter
@Setter
@Index(name = "role_permission_idx", columns = {"role_id", "permission_id"}, unique = true)
@Table(value = "sys_role_permission", comment = "角色权限")
public class RolePermission {
    @Id(idType = IdType.SNOWFLAKE)
    @TableField
    private Long id;

    @TableField(value = "role_id", notNull = true, comment = "角色id")
    private Long roleId;

    @TableField(value = "permission_id", notNull = true, comment = "权限id")
    private Long permissionId;

    public static RolePermission def() {
        return new RolePermission();
    }
}
````

## @Table

@Table注解用于标注实体类，包含以下属性：

```java
/**
 * 数据库表
 * @author ic
 * @since 2023/5/18
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {
    /**
     * 表名称
     */
    String value();
    /**
     * 数据库的 schema
     */
    String schema() default "";
    /**
     * 默认为 驼峰属性 转换为 下划线字段
     */
    boolean camelToUnderline() default true;
    /**
     * 默认使用哪个数据源，若系统找不到该指定的数据源时，默认使用第一个数据源
     */
    String dataSource() default "";
    /**
     * 注释
     */
    String comment() default "";

    /**
     * 开启自动建表默认true
     */
    boolean autoDDL() default true;

    /**
     * 表索引
     */
    Index[] index() default {};
}
```


## @TableField

@TableField注解用于标注实体类的字段，包含以下属性：

```java
/**
 * 数据库表字段
 * @author ic
 * @since 2023/5/18
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TableField {
    /**
     * 字段名称
     */
    String value() default "";

    /**
     * insert 的时候默认值，这个值会直接被拼接到 sql 而不通过参数设置
     */
    String onInsertValue() default "";

    /**
     * update 的时候自动赋值，这个值会直接被拼接到 sql 而不通过参数设置
     */
    String onUpdateValue() default "";

    /**
     * 配置的 type如：DECIMAL(10,2)
     */
    String type() default "";

    /**
     * 注释
     */
    String comment() default "";

    /**
     * 是否是逻辑删除字段
     */
    boolean isLogicDelete() default false;

    /**
     * 是否禁止为空
     */
    boolean notNull() default false;

    /**
     * 长度 -1采用默认值
     */
    int length() default -1;

    /**
     * 小数长度 -1采用默认值
     */
    int fraction() default -1;

    /**
     * 默认值，仅用于生成sql，并不会在插入时生效
     */
    String defaultValue() default "";
}
```


## @Id

@Id注解用于标注实体类的主键字段，包含以下属性：

目前只支持单主键

```java
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Id {

    /**
     * ID 生成策略，默认为 INPUT 外部控制输入
     *
     * @return 生成策略
     */
    IdType idType() default IdType.INPUT;
}
```


## @RelationTable

@RelationTable 注解用于标注实体类的映射关系，包含以下属性：

```java
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface RelationTable {
    /**
     * 映射字段，必填
     * 注意是填写数据库字段
     */
    String column();

    /**
     * 被注解的表内需要映射字段，必填
     * 注意是填写数据库字段
     */
    String relationColumn();
}
```

以下示例中，如果使用DepUserService查询DepUser对象则会，用dep_id字段去查询Dept对象的id；

```java

@Getter
@Setter
@Index(name = "dep_user_idx", columns = {"dep_id", "user_id"}, unique = true)
@Table(value = "sys_dep_user", comment = "部门用户")
public class DepUser {
    @Id(idType = IdType.SNOWFLAKE)
    private Long id;

    @TableField(value = "dep_id", notNull = true, comment = "部门id")
    private Long depId;

    @TableField(value = "user_id", notNull = true, comment = "用户id")
    private Long userId;

    @TableField(value = "manager", notNull = true, defaultValue = "0", comment = "是否负责人")
    private Boolean manager;

    @TableField(value = "create_time", notNull = true, comment = "创建时间", onInsertValue = "now()")
    private LocalDateTime createTime;

    @RelationTable(column = "dep_id", relationColumn = "id")
    private Dept dept;

    public static DepUser def() {
        DepUser depUser = new DepUser();
        depUser.setManager(false);
        return depUser;
    }
}
```

例如
```java
depUserService.selectAll();
```
执行如下：

1. 查询sys_dep_user表 
```sql
select * from sys_dep_user;
```

2. 封装到List < DepUser > list;
3. 获取list内dep_id字段到 List < Long > depIdList;
4. 再执行
```sql
select * from sys_dep where id in (depIdList);
```
5. 最后根据映射关系，批量将查询结果封装到list内dept字段

这样每次关联查询只执行一次sql


## @Association

@Association 注解用于将查询到的结果封装到对象，包含以下属性：

这个注解对应mybatis xml的resultMap内的association。只是将查询结果封装，并不会嵌套查询

```java
/**
 * 查询结果映射到对象
 * 默认会自动映射对象，但是如果对象与前缀不一致的时候，可以通过该注解指定前缀
 * @author hzl
 * @since 2023/5/18
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Association {
    /**
     * 需要封装到这个对象的前缀,不填则用字段名作为前缀
     */
    String prefix() default "";
}
```

## @Collection

@Collection 注解用于将查询到的结果封装到对象列表，包含以下属性：

这个注解对应mybatis xml的resultMap内的collection。只是将查询结果封装，并不会嵌套查询

```java
/**
 * 查询结果映射到列表
 * @author hzl
 * @since 2023/5/18
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Collection {
    /**
     * 需要封装到这个列表的前缀，不指定的话用的是字段名
     */
    String prefix() default "";
    /**
     * 需要指定主表主键字段，用于分组至改列表
     */
    String groupMainId();
}
```
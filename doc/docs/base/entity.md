# 表实体

实体使用 <a>@Table</a> 标注

## 基本使用

以下是个实体示例

````java

@Getter
@Setter
@Index(name = "dep_user_idx", columns = {"dep_id", "user_id"}, unique = true)
@Table(value = "sys_dep_user", comment = "部门用户")
public class DepUser {
    @Id(idType = IdType.SNOWFLAKE)
    private Long id;

    @ForeignKey(references = Dept.class, onDelete = ForeignKeyAction.CASCADE)
    @TableField(value = "dep_id", notNull = true, comment = "部门id")
    private Long depId;

    @ForeignKey(references = User.class, onDelete = ForeignKeyAction.CASCADE)
    @TableField(value = "user_id", notNull = true, comment = "用户id")
    private Long userId;

    @TableField(value = "manager", notNull = true, defaultValue = "0", comment = "是否负责人")
    private Boolean manager;

    @TableField(value = "create_time", notNull = true, comment = "创建时间", onInsertValue = "now()")
    private LocalDateTime createTime;

    public static DepUser def() {
        DepUser depUser = new DepUser();
        depUser.setManager(false);
        return depUser;
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


## @ForeignKey

@ForeignKey 注解用于标注实体类的外键关系，包含以下属性：
主要用于dber初始化数据库结构

```java

/**
 * 外键注解。
 *
 * @author ic-framework
 * @since 2024/06/09
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ForeignKey {

    /**
     * 外键名称。
     *
     * @return 外键名称
     */
    String name() default "";

    /**
     * 关联表。
     *
     * @return 关联表类型
     */
    Class<?> references();

    /**
     * 关联字段。
     *
     * @return 关联字段名
     */
    String referencesColumn() default "";

    /**
     * 删除时的操作。
     *
     * @return 删除操作类型
     */
    String onDelete() default ForeignKeyAction.NONE;

    /**
     * 更新时的操作。
     *
     * @return 更新操作类型
     */
    String onUpdate() default ForeignKeyAction.NONE;
}
```



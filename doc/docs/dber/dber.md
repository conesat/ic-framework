
# 注解DDL使用

注解与IcMybatis是共用的，会自动创建表结构与索引、注释等

如果字段没有 @TableField 或 @Id 是不会创建的
```java

/**
 * @author hzl
 * @date 2023/5/31
 */
@Getter
@Setter
@Index(name = "user_idx", columns = {"username"}, unique = true)
@Table("sys_user", comment = "用户表")
public class User {

    @Id(idType = IdType.SNOWFLAKE)
    private Long id;

    /**
     * 姓名
     */
    @TableField(value = "name", length = 64, comment = "姓名")
    private String name;

    /**
     * 用户名
     */
    @TableField(value = "username", comment = "用户名", length = 64, notNull = true)
    private String username;
    
    /**
     * 部门id
     */
    @ForeignKey(references = Dept.class, onDelete = ForeignKeyAction.CASCADE) // 外键关联
    @TableField(value = "dep_id", notNull = true, comment = "部门id")
    private Long depId;
    
    /**
     * 状态 
     * 支持继承cn.icframework.common.interfaces.IEnum的枚举
     */
    @TableField(value = "status", comment = "状态")
    private Status status;
}

```
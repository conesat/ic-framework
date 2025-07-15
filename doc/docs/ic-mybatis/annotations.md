# IC-Mybatis 查询

## 关联查询

当实体中存在关联关系的时候可以使用关联查询来查询关联表
**@RelationTable**

````java

/**
 * @author hzl
 */
@Getter
@Setter
@Table("sys_user")
public class User {

    @Id(idType = IdType.Uuid)
    @TableField
    private String id;

    @TableField(comment = "姓名")
    private String name;
    
    @TableField(comment = "用户名", notNull = true)
    private String username;

    @RelationTable(column = "id", relationColumn = "user_id")
    private List<Dep> dep;
    
    @RelationTable(column = "id", relationColumn = "user_id")
    private List<RoleUser> roles;
}
````

## 结果映射

当使用 left join、right join 的时候，可以使用@Association 和 @Collection 来映射结果。
例如sql如下
```sql
select 
    user.id, 
    user.name, 
    user.username, 
    dep.id as `dep.id`, 
    dep.name as `dep.name`
from user left join dep on user.dep_id = dep.id
```

**@Association 一对一映射**
**@Collection 一对多映射**

````java

/**
 * @author create by ic gen
 * @since 2023/06/17
 */
@Getter
@Setter
public class UserVO {
    private String id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 用户名
     */
    private String username;
    /**
     * 所属部门
     */
    @Association(prefix = "dep", groupMainId = "id")
    private DeptSimpleVO dep;
    /**
     * 所属角色
     */
    @Collection(prefix = "roles", groupMainId = "id")
    private List<RoleSimpleVO> roles;
}
````


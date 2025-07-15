# IC-Mybatis 基本使用

集成框架内集成IcMybatis结构是设计好的，如下：

定义好Entity：User

```java
@Getter
@Setter
@Index(name = "user_idx", columns = {"username"}, unique = true)
@Table("sys_user")
public class User {

    @Id(idType = IdType.SNOWFLAKE)
    private Long id;

    /**
     * 姓名
     */
    @TableField(value = "name", length = 64, comment = "姓名")
    private String name;
}
```

定义好VO：UserVO

```java
@Getter
@Setter
public class UserVO {

    private Long id;

    /**
     * 姓名
     */
    private String name;
}
```

定义好DAO：UserMapper

```java
@Mapper
public interface UserMapper extends BasicMapper<User> {
}
```

定义好Service：UserService

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService extends BasicService<UserMapper, User> {
}
```

当然以上内容都是可以通过代码生成器生成的，这里不再赘述。


## 查询

### 查询单个

```java
// 无条件查询 limit 1
User user = userService.selectOne();

// 根据id查询
User user = userService.selectById(1);

// 根据条件查询 limit 1
UserDef userDef = UserDef.table();
SqlWrapper sqlWrapper = SELECT().FROM(userDef).WHERE(userDef.id.eq(1));
// 查询实体
User user = userService.selectOne(sqlWrapper);
// 映射返回类型
UserVO userVO = userService.selectOne(sqlWrapper, UserVO.class);
```

### 查询多个

```java
// 无分页查询所有
List<User> user = userService.selectAll();
List<UserVO> userVOList = userService.selectAll(UserVO.class);


// 分页查询
PageRequest page = PageRequest.of(1, 10);
UserDef userDef = UserDef.table();
SqlWrapper sqlWrapper = SELECT().FROM(userDef).WHERE(userDef.name.like("张三"));
// 查询实体
PageResponse<User> pageUser =  userService.page(sqlWrapper, page);
// 映射返回类型
PageResponse<UserVO> pageUserVO =  userService.page(sqlWrapper, page, UserVO.class);

```




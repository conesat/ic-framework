# IC-Mybatis

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

在MyBatis中，association 元素用于处理一对一关联关系。以下是一些关键点和示例：
假设有如下两个实体类

```java
public class User {
  private int id;
  private String name;
  private Profile profile;
  // getters and setters
}

public class Profile {
  private int id;
  private String bio;
  // getters and setters
}
```

在映射文件中，使用 association 标签来映射一对一关系。例如：
```xml
<resultMap id="UserResultMap" type="User">
  <id property="id" column="id"/>
  <result property="name" column="name"/>
  <association property="profile" javaType="Profile">
      <id property="id" column="profile_id"/>
      <result property="bio" column="bio"/>
  </association>
</resultMap>

<select id="selectUserById" resultMap="UserResultMap">
  SELECT u.id, u.name, p.id AS profile_id, p.bio
  FROM users u
  LEFT JOIN profiles p ON u.profile_id = p.id
  WHERE u.id = #{id}
</select>
```

在IcMybatis返回VO使用 <a>@Association</a> 标注

```java
    public class UserVO {
      private int id;
      private String name;
      // prefix="prefix" 代表字段为p.xxx的都会被映射为profileVO的属性
      @Association(prefix="p")
      private ProfileVO profile;
      // getters and setters
    }
    
    public class ProfileDTO {
      private int id;
      private String bio;
    
      // getters and setters
    }
```


```java
    // 表定义 def 是ic根据实体自动生成的，这里直接使用就行
    UserDef userDef = UserDef.table();
    ProfileDef profileDef = ProfileDef.table();
    // 授予别名 p 对应 @Association(prefix="p")。否则将以表名进行映射
    profileDef.as("p");
    SqlWrapper sqlWrapper = SELECT()
            .FROM(userDef)
            .LEFT_JOIN(profileDef).ON(userDef.profileId.eq(profileDef.id))
            .WHERE(userDef.id.eq("xxx")));
    List<UserVO> userVOs = userService.selectList(sqlWrapper, UserVO.class);
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

在MyBatis中，collection 元素用于处理一对一关联关系。以下是一些关键点和示例：
假设有如下两个实体类

```java
  public class User {
      private int id;
      private String name;
      private List<Profile> profiles;
      // getters and setters
  }

  public class Profile {
      private int id;
      private String bio;
      // getters and setters
  }
```

在映射文件中，使用 collection 标签来映射一对一关系。例如：
```xml
  <resultMap id="UserResultMap" type="User">
      <id property="id" column="id"/>
      <result property="name" column="name"/>
      <collection property="profile" javaType="java.util.List" ofType="Profile">
          <id property="id" column="profile_id"/>
          <result property="bio" column="bio"/>
      </collection>
  </resultMap>

  <select id="selectUserById" resultMap="UserResultMap">
      SELECT u.id, u.name, p.id AS profile_id, p.bio
      FROM users u
      LEFT JOIN profiles p ON u.profile_id = p.id
      WHERE u.id = #{id}
  </select>
```

在IcMybatis返回VO使用 <a>@Collection</a> 标注

```java
  public class UserVO {
      private int id;
      private String name;
      // prefix="prefix" 代表字段为p.xxx的都会被映射为profileVO的属性
      @Collection(prefix="p")
      private List<ProfileVO> profiles;
      // getters and setters
  }

  public class ProfileDTO {
      private int id;
      private String bio;
      // getters and setters
  }
```


```java
    // 表定义 def 是ic根据实体自动生成的，这里直接使用就行
    UserDef userDef = UserDef.table();
    ProfileDef profileDef = ProfileDef.table();
    // 授予别名 p 对应 @Association(prefix="p")。否则将以表名进行映射
    profileDef.as("p");
    SqlWrapper sqlWrapper = SELECT()
            .FROM(userDef)
            .LEFT_JOIN(profileDef).ON(userDef.profileId.eq(profileDef.id))
            .WHERE(userDef.id.eq("xxx")));
    List<UserVO> userVOs = userService.selectList(sqlWrapper, UserVO.class);
```

## @Joins

@Joins 注解用于查询结果join查询，需要搭配@Join使用

```java

/**
 * 表映射
 * @author hzl
 * @since 2023/5/18
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Joins {
    /**
     * 连接关系
     */
    Join[] joins();

    /**
     * 跳过该对象的关联查询
     * 默认为false，但是存在部分情况，出现循环引用，
     * 导致一直死循环，可以设置为true，将不会处理该类的内部关联
     */
    boolean skipRelation() default false;
}
```

## @Join
```java

/**
 * 关联表映射 关联
 * @author hzl
 * @since 2023/5/18
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Join {
    /**
     * 需要连接的表，如果不填就是当前注解标注的对象
     */
    Class<?> joinTable() default Void.class;

    /**
     * 指定关联实体的表，默认为当前实体，如果多级嵌套默认为上一个joinTable
     */
    Class<?> selfTable() default Void.class;

    /**
     * 关联表字段，必填
     * 注意是填写java字段
     */
    String joinTableField();

    /**
     * 用当前实体的该字段数据去和关联表column进行查询
     * 注意是填写java字段
     */
    String selfField();

    /**
     * 扩展条件
     */
    String where() default "";

    /**
     * 查询字段 不填就是所有
     */
    String[] select() default {};

    /**
     * 排序条件
     */
    String orderBy() default "";

    /**
     * 条件比较方式
     */
    String compare() default "=";

    /**
     * 跳过该对象的关联查询
     * 默认为false，但是存在部分情况，出现循环引用，导致一直死循环，
     * 可以设置为true，将不会处理该类的内部关联
     */
    boolean skipRelation() default false;
}

```

不使用结果进行映射，可以先查出主体结果再用Joins去关联填充VO
定义VO
```java

/**
 * @author hzl
 * @date 2023/5/31
 */
@Getter
@Setter
public class UserVO {

    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 用户名
     */
    private String username;
    
    /**
     * 所属角色
     * 通过join去获取所有用户的角色
     */
    @Joins(joins = {
            @Join(joinTable = UserRole.class, selfField = "id", joinTableField = "userId"),
            @Join(joinTable = Role.class, selfField = "roleId", joinTableField = "id")
    })
    private List<RoleSimpleVO> roles;
}

```

```java
UserDef userDef = UserDef.table();
SqlWrapper sqlWrapper = SELECT()
        .FROM(userDef)
        .WHERE(userDef.name.like("张三"));
// 直接查UserVO 会自动left join角色出来
List<UserVO> vos = userService.select(sqlWrapper, UserVO.class);
```

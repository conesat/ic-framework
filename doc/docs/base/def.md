# 查询结果一对一映射

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

IcMybatis查询

<a href="/docs/ic-mybatis/ic-mybatis">IcMybatis如何使用，请查看这里</a>

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



# 查询结果一对多映射

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

IcMybatis查询

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
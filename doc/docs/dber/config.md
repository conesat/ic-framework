
# DBRunner 升级脚本使用场景

添加依赖
```xml
<dependency>
    <groupId>cn.icframework</groupId>
    <artifactId>ic-framework-dber</artifactId>
    <scope>compile</scope>
</dependency>
```

### 注解

默认引入依赖后会启用dber，执行实体ddl和sql脚本，可以通过下面这个注解关闭DDL

或者配置ic.framework.dber.enable-entity-ddl=false
```java
@EnableEntityDDL(enable = false)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```


### 升级脚本
>
升级脚本分为两种

第一种：V开头，只会执行一次，服务重启不会再次执行

第二种：R开头，每次重启都会执行

V适用于：
> 1. 版本升级需要处理旧数据，如：将所有用户的name字段设置为null
> 2. 版本升级字段需要变更，如：原本user.name 要改为 user.nickname


R适用于：
> 1. 清理异常数据

注意：文件名必须是 Vx.x.x_xxx、Rx.x.xxx 如 V1.0.0_xxx.sql、R1.0.0_xxx.sql

例如：V1.0.0_add_user_nickname.sql R1.0.0_delete_user_name.sql
```
src
├─main
│  ├─resources
│     ├─sqls
│        └─v1
│            ├─V1.0.0_add_user_nickname.sql
│            └─R1.0.0_delete_user_name.sql
│        └─v1.0.1
│            ├─V1.0.1_add_user_nickname.sql
│            └─V1.0.1_add_user_nickname_2.sql
```

内容示例：

V1.0.0_add_user_nickname.sql

```sql
ALTER TABLE user CHANGE name nickname VARCHAR(255);
update `user` set nickname = "xxx";
```


配置文件：ic-mybatis.properties
配置项如下
```properties
-- 扫升级sql目录路径，如果不配置默认为/src/main/resources/sqls
processor.update-path=/src/main/resources/sqls
-- 扫初始化sql文件路径，如果不配置默认为/src/main/resources/sqls/init.sql
processor.init-path=/src/main/resources/sqls/init.sql
```

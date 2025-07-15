# IC 配置

## 基础配置

框架带有系统激活限制，凡是 <a>Api.API_MANAGE</a> 开头的地址都将被拦截，并且需要激活系统才能访问，如果某些地址不需要校验可以用以下配置。

**ic.no-filter-urls:**

````yaml
ic:
  no-filter-urls: 
    - /manage/user/{id}
    - /manage/user/list
````


## mybatis 枚举类型配置【project已配置】

系统中使用了枚举类型，如下的Status，就是个继承cn.icframework.common.interfaces.IEnum的枚举，该值需要正常入库，需要配置mybatis的枚举类型处理器。

```java
@TableField(value = "status", comment = "是否有效")
private Status status;
```

配置文件需要至于项目路径

ic-framework-service/ic-framework-project/src/main/resources/mybatis-config.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <settings>
        <setting name="defaultEnumTypeHandler" value="handler.cn.icframework.mybatis.IEnumTypeHandler"/>
    </settings>
</configuration>
```

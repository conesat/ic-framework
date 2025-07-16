# IC-Mybatis

添加依赖
```xml
<dependency>
    <groupId>cn.icframework</groupId>
    <artifactId>ic-framework-mybatis</artifactId>
</dependency>

<!--自动生成def-->
<dependency>
    <groupId>cn.icframework</groupId>
    <artifactId>ic-framework-mybatis-processor</artifactId>
</dependency>
```

## 如何配置

配置文件：mybatis-config.xml需要配置一个枚举转换器，用于处理枚举查询与入库

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


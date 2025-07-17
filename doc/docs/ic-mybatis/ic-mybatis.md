# IC-Mybatis

ic-mybatis 借鉴 [MyBatisFlex](https://mybatis-flex.com/) 设计风格，采用APT形式生成数据库定义文件，IcMybatis主要针对mysql进行设计，功能还远不及MyBatisFlex。

### 核心特点
- **复杂查询**：支持多层子sql嵌套查询 
- **仅增强Mybatis**：可以理解为IcMybatis生成的内容是mybatis的xml，最终都由Mybatis执行 
- **结果映射**：查询结果自动关联查询实体属性，减少sql编写
- **自动分页**：一次查询即可获得分页结果
- **虚拟线程**：批量入库与查询采用虚拟线程提高效率


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


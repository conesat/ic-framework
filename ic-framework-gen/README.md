# IC Framework Gen 代码生成模块

## 概述

IC Framework Gen 模块提供了强大的代码生成功能，支持自动生成CRUD代码、Vue3前端代码、数据库表结构同步等功能，大大提高了开发效率。

## 主要特性

### 🎯 智能代码生成
- 基于数据库表结构自动生成代码
- 支持多种代码模板
- 自定义代码生成规则
- 批量代码生成

### 🎨 前端代码生成
- Vue3组件代码生成
- 表单验证规则生成
- API接口代码生成
- 路由配置生成

### 🗄️ 数据库同步
- 数据库表结构分析
- 实体类自动生成
- 字段映射配置
- 数据类型转换

### 📝 模板化生成
- 可自定义代码模板
- 支持多种编程风格
- 模板变量替换
- 条件生成控制

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>cn.icframework</groupId>
    <artifactId>ic-framework-gen</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置数据库连接

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 3. 使用代码生成器

```java
@RestController
@RequestMapping("/api/gen")
public class CodeGeneratorController {
    
    @Autowired
    private Generator generator;
    
    @PostMapping("/generate")
    public Result<String> generateCode(@RequestBody GenConfig config) {
        try {
            generator.generate(config);
            return Result.success("代码生成成功");
        } catch (Exception e) {
            return Result.error("代码生成失败: " + e.getMessage());
        }
    }
}
```

## 使用示例

### 基础代码生成

```java
// 配置生成参数
GenConfig config = new GenConfig();
config.setTableName("user");
config.setPackageName("cn.icframework.system.user");
config.setAuthor("张三");
config.setOutputPath("/path/to/output");

// 生成代码
generator.generate(config);
```

### 批量代码生成

```java
// 批量生成多个表的代码
List<String> tableNames = Arrays.asList("user", "role", "permission");
for (String tableName : tableNames) {
    GenConfig config = new GenConfig();
    config.setTableName(tableName);
    config.setPackageName("cn.icframework.system." + tableName);
    generator.generate(config);
}
```

### 自定义模板生成

```java
// 使用自定义模板
GenConfig config = new GenConfig();
config.setTableName("user");
config.setTemplatePath("/custom/templates");
config.setTemplateName("custom-template");

generator.generate(config);
```

## 生成的文件结构

```
generated/
├── java/
│   └── cn/com/ic/system/user/
│       ├── entity/
│       │   └── User.java
│       ├── mapper/
│       │   └── UserMapper.java
│       ├── service/
│       │   ├── UserService.java
│       │   └── impl/
│       │       └── UserServiceImpl.java
│       ├── controller/
│       │   └── UserController.java
│       └── vo/
│           ├── UserVO.java
│           └── UserDTO.java
├── vue/
│   └── views/
│       └── user/
│           ├── index.vue
│           └── edit.vue
└── sql/
    └── user.sql
```

## 配置说明

### 生成配置

```java
public class GenConfig {
    private String tableName;           // 表名
    private String packageName;         // 包名
    private String author;              // 作者
    private String outputPath;          // 输出路径
    private String templatePath;        // 模板路径
    private String templateName;        // 模板名称
    private List<String> excludeFields; // 排除字段
    private Map<String, String> fieldMapping; // 字段映射
}
```

### 数据库配置

```yaml
ic:
  gen:
    database:
      url: jdbc:mysql://localhost:3306/ic_framework
      username: root
      password: password
      driver-class-name: com.mysql.cj.jdbc.Driver
    output:
      java-path: src/main/java
      vue-path: src/views
      sql-path: src/main/resources/sql
    template:
      java-template: default
      vue-template: tdesign
```

## 模板系统

### Java模板

```java
// 实体类模板
public class ${className} {
    <#list fields as field>
    /**
     * ${field.comment}
     */
    private ${field.type} ${field.name};
    
    </#list>
    // getter and setter methods
}
```

### Vue模板

```vue
<template>
  <div class="user-container">
    <t-card>
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        @row-click="handleRowClick"
      >
        <template #operation="{ row }">
          <t-button @click="handleEdit(row)">编辑</t-button>
          <t-button @click="handleDelete(row)">删除</t-button>
        </template>
      </t-table>
    </t-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserList, deleteUser } from '@/api/user'

const tableData = ref([])
const loading = ref(false)

const columns = [
  { key: 'name', title: '姓名' },
  { key: 'email', title: '邮箱' },
  { key: 'status', title: '状态' },
  { key: 'operation', title: '操作' }
]

const loadData = async () => {
  loading.value = true
  try {
    const data = await getUserList()
    tableData.value = data
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
```

## 高级功能

### 自定义字段映射

```java
// 配置字段映射
Map<String, String> fieldMapping = new HashMap<>();
fieldMapping.put("user_name", "userName");
fieldMapping.put("create_time", "createTime");
fieldMapping.put("update_time", "updateTime");

GenConfig config = new GenConfig();
config.setFieldMapping(fieldMapping);
```

### 条件生成

```java
// 根据条件生成不同的代码
GenConfig config = new GenConfig();
config.setTableName("user");

if (hasAuditFields(config.getTableName())) {
    config.setIncludeAudit(true);
}

if (hasSoftDelete(config.getTableName())) {
    config.setIncludeSoftDelete(true);
}

generator.generate(config);
```

### 模板变量

```java
// 自定义模板变量
Map<String, Object> variables = new HashMap<>();
variables.put("projectName", "IC Framework");
variables.put("companyName", "IC Company");
variables.put("version", "1.0.0");

GenConfig config = new GenConfig();
config.setVariables(variables);
```

## 最佳实践

### 1. 表命名规范

```sql
-- 推荐：使用下划线命名
CREATE TABLE user_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 不推荐：使用驼峰命名
CREATE TABLE userInfo (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    userName VARCHAR(50) NOT NULL
);
```

### 2. 字段命名规范

```sql
-- 推荐：使用下划线命名
user_name VARCHAR(50),
create_time DATETIME,
update_time DATETIME

-- 不推荐：使用驼峰命名
userName VARCHAR(50),
createTime DATETIME
```

### 3. 代码生成配置

```java
// 推荐：使用配置类管理生成参数
@Configuration
public class GenConfig {
    
    @Bean
    public Generator generator() {
        Generator generator = new Generator();
        generator.setDefaultPackage("cn.icframework");
        generator.setDefaultAuthor("IC Framework");
        generator.setDefaultOutputPath("generated");
        return generator;
    }
}
```

## 常见问题

### Q: 如何自定义代码模板？
A: 可以在 `templates` 目录下创建自定义模板文件，然后在配置中指定模板路径。

### Q: 如何生成特定格式的代码？
A: 可以通过修改模板文件来生成不同风格的代码，如驼峰命名、下划线命名等。

### Q: 如何处理复杂的表关系？
A: 可以使用关联表配置来生成包含关联关系的代码。

### Q: 如何批量生成多个项目的代码？
A: 可以编写脚本循环调用代码生成器，为每个项目生成相应的代码。

## 相关链接

- [框架文档](https://icframework.chinahg.top)
- [Vue3文档](https://vuejs.org/)
- [TDesign文档](https://tdesign.tencent.com/)
- [模板语法文档](doc/docs/gen/template.md) 
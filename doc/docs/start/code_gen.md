# 代码生成

## 前言

代码生成有两种方式

- 使用java编程式生成
- 使用idea插件生成

## 获取插件

- 自行clone ic-framework

  编译其中的idea-plugin: 执行gradle > Tasks > shadow > shadowJar

- 直接点击下载作者编译的插件 [idea-plugin](/public/idea-plugin-1.0-SNAPSHOT-all.jar)


## 注意
无论使用哪种方式生成都是基于实体类的，所以前提就是你必须有一个实体类。如果不用插件则需要手工编写实体。


## 用插件生成

在需要创建实体的模块包下，右键创建实体类
![](/public/imgs/gen_module.png)
>
输入实体类名称和注释
![](/public/imgs/gen_module_d.png)
>
最终你得到一个实体类。你可以对生成的字段进行补充或者修改。
![](/public/imgs/gen_java_entity.png)
>
在实体中打开generate菜单，选择生成代码

第一个model是生成所有代码，第二个model是生成def代码（一般是def是apt生成的，除非不用apt才会用到这个功能）
![](/public/imgs/gen_detail.png)
>
选择后弹出该对话框，都是默认值，当然也可以自行修改。直接ok就生成代码了
![](/public/imgs/gen_1.png)

## 使用java生成
```java

import cn.icframework.gen.Generator;
import org.junit.jupiter.api.Test;

/**
 * @author iceFire
 * @since 2023/6/5
 */
public class TestGen {
  @Test
  public void genCode() {
    Generator generator = new Generator();
    // 作者
    generator.author("iceFire");
    // 指定模块名称
    generator.moduleName("dep");
    // 指定需要生成的实体类
    generator.tableClass(cn.icframework.project.module.dep.Dept.class);
    // 指定生成的java文件路径
    generator.javaPath("D:\\product\\ic\\ic-framework-service\\ic-framework-project\\src\\main\\java");
    // 指定生成的vue文件路径
    generator.pageVueSrcPath("D:\\product\\ic\\ic-framework-service\\_web\\admin\\src");
    // 生成
    generator.build();
  }
}

```


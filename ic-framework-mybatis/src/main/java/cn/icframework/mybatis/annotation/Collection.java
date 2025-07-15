package cn.icframework.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
     * 需要封装到这个列表的前缀，不指定的话 会匹配全部字段
     * 例如select 返回如下
     * name | id | department.id | department.name
     * 张三 | 1 | 1 | A
     * 张三 | 1 | 2 | B
     * 李四 | 2 | 1 | A
     * 李四 | 2 | 3 | C
     * 返回对象需要为 {id:'',name:'',dep:[{id:'',name:''}]}
     * 这里就要在dep字段上指定 prefix 为 department
     * 这样遍历的时候 就会把department.开头的数据，封装到dep字段中
     */
    String prefix() default "";

    /**
     * 只获取这个字段数据，需要使用数据库查询结果字段名
     */
    String column() default "";

    /**
     * 指定主表主键字段
     * 例如select 返回如下
     * name | id | dep.id | dep.name
     * 张三 | 1 | 1 | A
     * 张三 | 1 | 2 | B
     * 李四 | 2 | 1 | A
     * 李四 | 2 | 3 | C
     * 返回对象需要为 {id:'',name:'',dep:[{id:'',name:''}]}
     * 这里就要指定 groupMainId 为 id
     * 这样遍历的时候 就会把id相同的数据，分组到一起
     * 如：data.stream().collect(Collectors.groupingBy(f->f.getId()));
     */
    String groupMainId();
}

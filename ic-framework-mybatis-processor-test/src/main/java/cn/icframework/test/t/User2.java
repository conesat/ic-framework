package cn.icframework.test.t;

import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.annotation.TableField;

/**
 *
 * @author hzl
 * @since 2025/6/30
 */
@Table("user")
public class User2 {

    @TableField("name")
    private String name;

}

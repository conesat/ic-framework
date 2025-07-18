package cn.icframework.auth.entity;

import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.TableField;
import cn.icframework.mybatis.consts.IdType;
import lombok.Getter;
import lombok.Setter;

/**
 * 权限实体类，描述系统中的权限信息。
 *
 * @author hzl
 * @since 2023/6/20
 */
@Getter
@Setter
public class BasicPermission {

    /**
     * 权限ID，主键。
     */
    @Id(idType = IdType.SNOWFLAKE)
    private Long id;

    /**
     * 权限路径。
     */
    @TableField(notNull = true, comment = "路径")
    private String path;

    /**
     * 权限分组ID。
     */
    @TableField(notNull = true, comment = "分组id")
    private Long groupId;

    /**
     * 用户类型。
     */
    @TableField(notNull = true, comment = "用户类型")
    private String userType;

    /**
     * 权限名称。
     */
    @TableField(notNull = true, comment = "名称")
    private String name;
}

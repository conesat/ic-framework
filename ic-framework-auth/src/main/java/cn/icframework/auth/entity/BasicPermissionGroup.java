package cn.icframework.auth.entity;

import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.TableField;
import cn.icframework.mybatis.consts.IdType;
import lombok.Getter;
import lombok.Setter;

/**
 * 权限分组实体类，描述权限分组信息。
 *
 * @author hzl
 * @since 2023/6/20
 */
@Getter
@Setter
public class BasicPermissionGroup {

    /**
     * 分组ID，主键。
     */
    @Id(idType = IdType.SNOWFLAKE)
    @TableField(value = "id", notNull = true, comment = "id")
    private Long id;

    /**
     * 分组路径。
     */
    @TableField(value = "path", notNull = true, comment = "路径")
    private String path;

    /**
     * 分组名称。
     */
    @TableField(value = "name", notNull = true, comment = "名称")
    private String name;
}

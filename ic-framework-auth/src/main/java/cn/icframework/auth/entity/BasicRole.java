package cn.icframework.auth.entity;

import cn.icframework.common.enums.Status;
import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.TableField;
import cn.icframework.mybatis.consts.IdType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 角色实体类，描述系统中的角色信息。
 *
 * @author hzl
 * @since 2023/8/10
 */
@Getter
@Setter
public class BasicRole {
    /**
     * 角色ID，主键。
     */
    @Id(idType = IdType.SNOWFLAKE)
    @TableField
    private Long id;

    /**
     * 角色标识。
     */
    @TableField(value = "sign", notNull = true, comment = "标识")
    private String sign;

    /**
     * 用户类型。
     */
    @TableField(value = "user_type", notNull = true, comment = "用户类型")
    private String userType;

    /**
     * 角色名称。
     */
    @TableField(value = "name", notNull = true, comment = "名称")
    private String name;

    /**
     * 角色状态。
     */
    @TableField(value = "status", notNull = true, comment = "状态")
    private Status status;

    /**
     * 是否为系统内置角色。
     */
    @TableField(value = "system", notNull = true, comment = "是否系统")
    private boolean system;

    /**
     * 是否为超级管理员角色。
     */
    @TableField(value = "su", notNull = true, comment = "是否超管角色")
    private boolean su;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time", notNull = true, comment = "创建时间", onInsertValue = "now()")
    private LocalDateTime createTime;
}

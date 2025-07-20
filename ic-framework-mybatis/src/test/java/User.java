import cn.icframework.mybatis.annotation.*;
import cn.icframework.mybatis.consts.IdType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.util.List;

/**
 * @author hzl
 * @since 2023/6/20 0020
 */
@Getter
@Setter
@Table(value = "user", comment = "test")
public class User {

    @Id(idType = IdType.AUTO)
    @TableField
    private Long id;

    @TableField(value = "name", comment = "名称")
    private String name;

    @LogicDelete
    private Boolean del;

    @Version
    private Long version;


    @Joins(joins = {
            @Join(joinTable = UserRole.class, joinTableField = "userId", selfField = "id"),
            @Join(joinTable = Role.class, joinTableField = "id", selfTable = UserRole.class, selfField = "roleId")
    })
    private List<Role> roles;

    public static User def() {
        return new User();
    }
}

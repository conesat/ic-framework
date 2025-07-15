import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.Join;
import cn.icframework.mybatis.annotation.Joins;
import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.annotation.TableField;
import cn.icframework.mybatis.consts.IdType;
import lombok.Getter;
import lombok.Setter;

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

    @TableField(value = "del", comment = "名称", isLogicDelete = true)
    private Boolean del;


    @Joins(joins = {
            @Join(joinTable = UserRole.class, joinTableField = "userId", selfField = "id"),
            @Join(joinTable = Role.class, joinTableField = "id", selfTable = UserRole.class, selfField = "roleId")
    })
    private List<Role> roles;

    public static User def() {
        return new User();
    }
}

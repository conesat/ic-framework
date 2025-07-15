import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.Join;
import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.annotation.TableField;
import cn.icframework.mybatis.consts.IdType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hzl
 * @since 2023/6/20 0020
 */
@Getter
@Setter
@Table(value = "user_role", comment = "user_role")
public class UserRole {

    @Id(idType = IdType.AUTO)
    @TableField
    private Long id;

    @TableField(value = "userId", comment = "名称")
    private String userId;

    @TableField(value = "roleId", comment = "名称")
    private String roleId;

    @Join(joinTableField = "id", selfField = "roleId")
    private Role role;


    public static UserRole def() {
        return new UserRole();
    }
}

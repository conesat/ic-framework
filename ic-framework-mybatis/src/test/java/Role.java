import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.annotation.TableField;
import cn.icframework.mybatis.consts.IdType;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色
 * @author hzl
 * @since 2023/6/20 0020
 */
@Getter
@Setter
@Table(value = "user", comment = "test")
public class Role {

    @Id(idType = IdType.AUTO)
    @TableField
    private Long id;

    @TableField(value = "name", comment = "名称")
    private String name;

    @TableField(value = "del", comment = "名称", isLogicDelete = true)
    private Boolean del;


    public static Role def() {
        return new Role();
    }
}

import cn.icframework.mybatis.annotation.AutoView;
import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.LogicDelete;
import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.annotation.TableField;
import cn.icframework.mybatis.consts.IdType;
import cn.icframework.mybatis.wrapper.SqlWrapper;
import lombok.Getter;
import lombok.Setter;

import static cn.icframework.mybatis.wrapper.Wrapper.SELECT;

/**
 * 角色
 *
 * @author hzl
 * @since 2023/6/20 0020
 */
@Getter
@Setter
@Table(value = "user", autoDDL = false)
public class RoleView {

    @Id(idType = IdType.AUTO)
    private Long id;

    @TableField(value = "name", comment = "名称")
    private String name;

    @LogicDelete
    private Boolean del;

    @AutoView
    public SqlWrapper autoView() {
        UserRoleDef def = UserRoleDef.table();
        return SELECT(def).FROM(def);
    }

    public static RoleView def() {
        return new RoleView();
    }
}

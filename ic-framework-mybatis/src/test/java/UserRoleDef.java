import cn.icframework.mybatis.query.QueryField;
import cn.icframework.mybatis.query.QueryTable;

/**
 * 请勿修改该文件，该文件由ic mybatis生成会被覆盖
 * @author ic generator
 * @since 2024/08/02
 */
public class UserRoleDef extends QueryTable<UserRoleDef> {
    public UserRoleDef(Class<?> tableClass) {
        super(tableClass);
    }

    public static UserRoleDef table() {
        return new UserRoleDef(UserRole.class);
    }
    @Override
    public UserRoleDef newInstance() {
        return new UserRoleDef(UserRoleDef.class);
    }

    public QueryField<UserRoleDef> id= new QueryField<>(this, "id");
    public QueryField<UserRoleDef> name= new QueryField<>(this, "name");

}

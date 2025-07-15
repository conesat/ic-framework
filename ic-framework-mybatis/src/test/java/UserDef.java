import cn.icframework.mybatis.query.QueryField;
import cn.icframework.mybatis.query.QueryTable;

/**
 * 请勿修改该文件，该文件由ic mybatis生成会被覆盖
 * @author ic generator
 * @since 2024/08/02
 */
public class UserDef extends QueryTable<UserDef> {
    public UserDef(Class<?> tableClass) {
        super(tableClass);
    }
    public static UserDef table() {
        return new UserDef(User.class);
    }

    @Override
    public UserDef newInstance() {
        return new UserDef(UserDef.class);
    }

    public QueryField<UserDef> id= new QueryField<>(this, "id");
    public QueryField<UserDef> name= new QueryField<>(this, "name");

}

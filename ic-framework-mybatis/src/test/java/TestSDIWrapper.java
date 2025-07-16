import cn.icframework.common.consts.IPage;
import cn.icframework.mybatis.consts.IcParamsConsts;
import cn.icframework.mybatis.utils.ModelClassUtils;
import cn.icframework.mybatis.wrapper.FromWrapper;
import cn.icframework.mybatis.wrapper.SqlWrapper;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static cn.icframework.mybatis.query.Checks.CHECK;
import static cn.icframework.mybatis.wrapper.FunctionWrapper.DISTINCT;
import static cn.icframework.mybatis.wrapper.FunctionWrapper.EXISTS;
import static cn.icframework.mybatis.wrapper.Wrapper.*;


/**
 * @author hzl
 * @since 2023/6/21
 */
public class TestSDIWrapper {

    @Test
    public void testSqlPars() {
        Field idField = ModelClassUtils.getIdField(User.class);
        String idFieldName = ModelClassUtils.getTableColumnName(idField);
        String s = """
                <script>
                   %sql
                   WHERE
                   <foreach collection="%arrays" index="index" item="item" open="(" separator="," close=")">
                        #{item}
                   </foreach>
                </script>
                """
                .replaceAll("%sql", DELETE_FROM(User.class).sql())
                .replaceAll("%idFieldName", idFieldName != null ? idFieldName : "")
                .replaceAll("%arrays", IcParamsConsts.PARAMETER_PRIMARY_KEYS);
        System.out.println(s);
    }

    @Test
    public void testNormal() {
        UserDef table = UserDef.table();
        SqlWrapper sqlWrapper = SELECT(table)
                .FROM(table)
                .WHERE(table.name.like("123"));
        Map<String, Object> params = sqlWrapper.getParams();
        String sql = sqlWrapper.sql();
        System.out.println(sql);
        System.out.println(params.toString());
    }

    @Test
    public void testSqlIn() {
        UserDef table = UserDef.table();
        UserDef table2 = UserDef.table();
        SqlWrapper sqlWrapper = SELECT(DISTINCT(table.id).as("w"), table.name, SELECT(table2.name).FROM(table2).WHERE(table2.name.eq(table.name)).AS("name2"))
                .FROM(table)
                .WHERE(table.name.in(SELECT(table.name).FROM(table).WHERE(table.name.eq("2").id.eq("3"))).or().name.like("2"));
        Map<String, Object> params = sqlWrapper.getParams();
        String sql = sqlWrapper.sql();
        System.out.println(sql);
        System.out.println(params.toString());
    }

    @Test
    public void testExist() {
        UserDef table = UserDef.table();
        UserRoleDef table2 = UserRoleDef.table();
        table2.as("t2");
        table.id.as("w");

        SqlWrapper sqlWrapper = SELECT_DISTINCT()
                .FROM(table)
                .WHERE(table.name.like("123"))
                .WHERE(EXISTS(SELECT().FROM(table2).WHERE(table2.id.eq(table.id).name.eq("2"))));
        Map<String, Object> params = sqlWrapper.getParams();
        String sql = sqlWrapper.sql();
        System.out.println(sql);
        System.out.println(params.toString());
    }

    @Test
    public void testInsertSelect() {
        UserDef table = UserDef.table();
        SqlWrapper where = INSERT()
                .INTO(User.class)
                .COLUMNS(User::getName, User::getDel, User::getId)
                .VALUES(
                        SELECT(AS(1, User::getName), AS(1, User::getDel), table.id)
                                .FROM(table)
                                .WHERE(table.name.eq("123"))
                );
        Map<String, Object> params = where.getParams();
        String sql = where.sql();
        System.out.println(sql);
        System.out.println(params.toString());
    }

    @Test
    public void testInsert() {
        SqlWrapper where = INSERT()
                .INTO(User.class)
                .COLUMNS(User::getName, User::getDel, User::getId)
                .VALUES("2", "2");
        Map<String, Object> params = where.getParams();
        String sql = where.sql();
        System.out.println(sql);
        System.out.println(params.toString());
    }

    @Test
    public void testDelete() {
        UserDef table = UserDef.table();
        SqlWrapper where =
                DELETE_FROM(table).WHERE(table.name.eq("123"), OR(), table.name.eq("456").id.eq("1"));
        Map<String, Object> params = where.getParams();
        String sql = where.sql();
        System.out.println(sql);
        System.out.println(params.toString());
    }

    @Test
    public void testUpdate() {
        UserDef table = UserDef.table();
        SqlWrapper where =
                UPDATE(table.id.set(null).name.isNull().name.set("123"));
        Map<String, Object> params = where.getParams();
        String sql = where.sql();
        System.out.println(sql);
        System.out.println(params.toString());
    }

    @Test
    public void testSelect() {
        UserDef table = UserDef.table();
        UserRoleDef table2 = UserRoleDef.table();
        table2.as("t2");
        table.id.as("w");

        SqlWrapper sqlWrapper = SELECT_DISTINCT(table, "1")
                .FROM(table)
                .LEFT_JOIN(table2).ON(table.id.eq(table2.id).or().name.ge("3"))
                .LEFT_JOIN(table).ON(table.name.eq(table2.id))
                .WHERE(table.name.like("123"), OR(table2.id.eq("2").name.ge("1"), OR(), table.name.le("2")))
                .ORDER_BY(table.name.asc().id.desc());
        Map<String, Object> params = sqlWrapper.getParams();
        String sql = sqlWrapper.sql();
        System.out.println(sql);
    }

    @Test
    public void testSelect1() {
        UserDef table = UserDef.table();
        UserRoleDef table2 = UserRoleDef.table();
        table2.as("t2");
        table.id.as("w");
        SqlWrapper sqlWrapper = SELECT(1, "2", AS(1, "f"))
                .FROM(table)
                .LEFT_JOIN(table2).ON(table.id.eq(table2.id))
                .WHERE(AND(table.name.eq(1).id.eq(1)));
        Map<String, Object> params = sqlWrapper.getParams();
        String sql = sqlWrapper.sql();
        System.out.println(sql);
    }

    @Test
    public void testSelectSub() {
        UserDef userDef = UserDef.table();
        UserRoleDef userRoleDef = UserRoleDef.table();

        IPage page = new IPage();
        page.setPageSize(10);
        page.setPageIndex(1);
        UserDef as = SELECT().FROM(userDef).WHERE(userDef.name.eq("123")).PAGE(page).AS(UserDef.class);
        FromWrapper sqlWrapper = SELECT().FROM(as).LEFT_JOIN(userRoleDef).ON(as.id.eq(userRoleDef.id));

        Map<String, Object> params = sqlWrapper.getParams();
        String sql = sqlWrapper.sql();
        System.out.println(sql);
        System.out.println(params);
    }

    @Test
    public void testCheck() {
        UserDef userDef = UserDef.table();
        UserRoleDef userRoleDef = UserRoleDef.table();

        IPage page = new IPage();
        page.setPageSize(10);
        page.setPageIndex(1);
        UserDef as = SELECT().FROM(userDef).WHERE(CHECK(false, userDef.name::eq, "123")).PAGE(page).AS(UserDef.class);
        FromWrapper sqlWrapper = SELECT().FROM(as).LEFT_JOIN(userRoleDef).ON(as.id.eq(userRoleDef.id));

        Map<String, Object> params = sqlWrapper.getParams();
        String sql = sqlWrapper.sql();
        System.out.println(sql);
        System.out.println(params);
    }
}

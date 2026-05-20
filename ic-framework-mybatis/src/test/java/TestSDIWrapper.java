import cn.icframework.common.consts.IPage;
import cn.icframework.mybatis.consts.IcParamsConsts;
import cn.icframework.mybatis.utils.ModelClassUtils;
import cn.icframework.mybatis.wrapper.FromWrapper;
import cn.icframework.mybatis.wrapper.SelectWrapper;
import cn.icframework.mybatis.wrapper.SqlWrapper;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static cn.icframework.mybatis.query.Checks.CHECK;
import static cn.icframework.mybatis.wrapper.FunctionWrapper.COUNT;
import static cn.icframework.mybatis.wrapper.FunctionWrapper.DISTINCT;
import static cn.icframework.mybatis.wrapper.FunctionWrapper.EXISTS;
import static cn.icframework.mybatis.wrapper.FunctionWrapper.NOT_EXISTS;
import static cn.icframework.mybatis.wrapper.Wrapper.*;


/**
 * @author hzl
 * @since 2023/6/21
 */
public class TestSDIWrapper {

    private static String normalizeSql(String sql) {
        return sql.replaceAll("\\s+", " ").trim();
    }

    private static void assertSqlEquals(String expectedSql, String actualSql) {
        assertEquals(normalizeSql(expectedSql), normalizeSql(actualSql),
                () -> "expected sql:\n" + expectedSql + "\nactual sql:\n" + actualSql);
    }

    private static void assertParamEquals(Map<String, Object> params, String key, Object expectedValue) {
        assertEquals(expectedValue, params.get(key),
                () -> "expected param " + key + "=" + expectedValue + ", actual=" + params.get(key));
    }

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
        table2 = table2.alias("t2");

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
    public void testUpdateEntity() {
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
        table2 = table2.alias("t2");

        SqlWrapper sqlWrapper = SELECT_DISTINCT(table.id.as("w"), table.name, table.version, "1")
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
        table2 = table2.alias("t2");
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

    @Test
    public void testFromSqlWrapperKeepsOuterSelectState() {
        UserDef userDef = UserDef.table();

        SqlWrapper subQuery = SELECT(userDef.id, userDef.name)
                .FROM(userDef)
                .WHERE(userDef.name.eq("alice"))
                .AS("u");

        String sql = SELECT(AS(1, "marker"))
                .FROM(subQuery)
                .WHERE("u.id = 1")
                .sql();
        String expectedSql = """
                SELECT 1 AS `marker`
                FROM (SELECT user.id, user.name
                FROM user
                WHERE (user.del <> #{logicDelete} AND (user.name = #{params.p_0}))) AS `u`
                WHERE (u.id = 1)
                """;

        assertSqlEquals(expectedSql, sql);
    }

    @Test
    public void testComplexNestedFromSqlWrapperJoinAndPaging() {
        UserDef userDef = UserDef.table();
        UserRoleDef userRoleDef = UserRoleDef.table();

        SqlWrapper pickedNameSubQuery = SELECT(userDef.name)
                .FROM(userDef)
                .WHERE(userDef.name.like("alice"))
                .AS("picked_name");
        SqlWrapper middleQuery = SELECT(AS(1, "marker"), pickedNameSubQuery, userDef.id)
                .FROM(userDef)
                .WHERE(userDef.id.in(
                        SELECT(userRoleDef.id)
                                .FROM(userRoleDef)
                                .WHERE(userRoleDef.name.eq("admin"))
                ))
                .ORDER_BY(userDef.id.asc())
                .LIMIT(0, 5)
                .AS("mid");

        IPage page = new IPage();
        page.setPageIndex(2);
        page.setPageSize(3);
        SqlWrapper outerQuery = SELECT(AS(1, "outerFlag"))
                .FROM(middleQuery)
                .LEFT_JOIN(userRoleDef.alias("ur")).ON("mid.id = ur.id")
                .WHERE("mid.marker = 1")
                .PAGE(page);

        String sql = outerQuery.sql();
        Map<String, Object> params = outerQuery.getParams();
        String expectedSql = """
                SELECT 1 AS `outerFlag`
                FROM (SELECT 1 AS `marker`, (SELECT user.name
                FROM user
                WHERE (user.del <> #{logicDelete} AND (user.name LIKE #{params.p_0}))) AS `picked_name`, user.id
                FROM user
                WHERE (user.del <> #{logicDelete} AND (user.id IN (SELECT user_role.id
                FROM user_role
                WHERE (user_role.name = #{params.p_1}))))
                ORDER BY id ASC LIMIT 0,5) AS `mid`
                LEFT OUTER JOIN user_role ur ON (mid.id = ur.id)
                WHERE (mid.marker = 1) LIMIT 3,3
                """;

        assertSqlEquals(expectedSql, sql);
        assertParamEquals(params, "p_0", "%alice%");
        assertParamEquals(params, "p_1", "admin");
    }

    @Test
    public void testNestedFromSqlWrapperTwoLevels() {
        UserDef userDef = UserDef.table();

        SqlWrapper inner = SELECT(userDef.id)
                .FROM(userDef)
                .WHERE(userDef.name.eq("alice"))
                .AS("inner_u");

        SqlWrapper middle = SELECT(AS(1, "middleFlag"))
                .FROM(inner)
                .WHERE("inner_u.id > 10")
                .AS("middle_u");

        String actualSql = SELECT(AS(1, "outerFlag"))
                .FROM(middle)
                .WHERE("middle_u.middleFlag = 1")
                .sql();
        String expectedSql = """
                SELECT 1 AS `outerFlag`
                FROM (SELECT 1 AS `middleFlag`
                FROM (SELECT user.id
                FROM user
                WHERE (user.del <> #{logicDelete} AND (user.name = #{params.p_0}))) AS `inner_u`
                WHERE (inner_u.id > 10)) AS `middle_u`
                WHERE (middle_u.middleFlag = 1)
                """;

        assertSqlEquals(expectedSql, actualSql);
    }

    @Test
    public void testFromDefWithMultiFieldMixedConditions() {
        UserDef userDef = UserDef.table();
        UserRoleDef userRoleDef = UserRoleDef.table();
        userRoleDef = userRoleDef.alias("ur");

        String actualSql = SELECT(userDef.id.as("userId"), userDef.name.as("userName"), userRoleDef.name.as("roleName"))
                .FROM(userDef)
                .LEFT_JOIN(userRoleDef).ON(userRoleDef.id.eq(userDef.id))
                .WHERE(
                        AND(
                                userDef.id.ge(10),
                                AND(),
                                userDef.name.like("alice")
                        )
                )
                .OR(
                        AND(
                                userDef.name.eq("bob"),
                                AND(),
                                userRoleDef.name.notNull()
                        )
                )
                .ORDER_BY_DESC(userDef.id)
                .ORDER_BY_ASC(userRoleDef.name)
                .LIMIT(0, 20)
                .sql();
        String expectedSql = """
                SELECT user.id AS `userId`, user.name AS `userName`, ur.name AS `roleName`
                FROM user
                LEFT OUTER JOIN user_role ur ON (ur.id = user.id)
                WHERE ((((user.id >= #{params.p_0}) AND (user.name LIKE #{params.p_1})) OR ((user.name = #{params.p_2}) AND (ur.name IS NOT NULL))) AND user.del <> #{logicDelete})
                ORDER BY id DESC, name LIMIT 0,20
                """;

        assertSqlEquals(expectedSql, actualSql);
    }

    @Test
    public void testWhereInlineOrMarkerDoesNotEmitEmptyParentheses() {
        UserDef userDef = UserDef.table();

        SqlWrapper query = SELECT(userDef.id, userDef.name)
                .FROM(userDef)
                .WHERE(
                        userDef.id.eq(1),
                        OR(),
                        userDef.name.like("alice")
                );

        String actualSql = query.sql();
        String expectedSql = """
                SELECT user.id, user.name
                FROM user
                WHERE (user.del <> #{logicDelete} AND ((user.id = #{params.p_0}) OR (user.name LIKE #{params.p_1})))
                """;

        assertSqlEquals(expectedSql, actualSql);
        assertParamEquals(query.getParams(), "p_0", 1);
        assertParamEquals(query.getParams(), "p_1", "%alice%");
    }

    @Test
    public void testWhereNestedAndOrMarkersDoNotEmitEmptyParentheses() {
        UserDef userDef = UserDef.table();
        UserRoleDef userRoleDef = UserRoleDef.table();
        userRoleDef = userRoleDef.alias("ur");

        SqlWrapper query = SELECT(userDef.id.as("userId"), userRoleDef.name.as("roleName"))
                .FROM(userDef)
                .LEFT_JOIN(userRoleDef).ON(userRoleDef.id.eq(userDef.id))
                .WHERE(
                        AND(
                                userDef.id.ge(10),
                                AND(),
                                userDef.name.like("alice")
                        ),
                        OR(),
                        AND(
                                userDef.name.eq("bob"),
                                AND(),
                                userRoleDef.name.notNull()
                        )
                );

        String actualSql = query.sql();
        String expectedSql = """
                SELECT user.id AS `userId`, ur.name AS `roleName`
                FROM user
                LEFT OUTER JOIN user_role ur ON (ur.id = user.id)
                WHERE (user.del <> #{logicDelete} AND (((user.id >= #{params.p_0}) AND (user.name LIKE #{params.p_1})) OR ((user.name = #{params.p_2}) AND (ur.name IS NOT NULL))))
                """;

        assertSqlEquals(expectedSql, actualSql);
        assertParamEquals(query.getParams(), "p_0", 10);
        assertParamEquals(query.getParams(), "p_1", "%alice%");
        assertParamEquals(query.getParams(), "p_2", "bob");
    }

    @Test
    public void testMultiFieldMixedQueryWithFromSubqueryAndFromDef() {
        UserDef userDef = UserDef.table();
        UserRoleDef joinRoleDef = UserRoleDef.table();
        joinRoleDef = joinRoleDef.alias("ur");
        UserRoleDef roleCountDef = UserRoleDef.table();
        UserRoleDef inRoleDef = UserRoleDef.table();
        UserRoleDef existsRoleDef = UserRoleDef.table();
        UserRoleDef notExistsRoleDef = UserRoleDef.table();

        SqlWrapper roleCountSubQuery = SELECT(COUNT(roleCountDef.id).as("roleCount"), roleCountDef.id)
                .FROM(roleCountDef)
                .WHERE(roleCountDef.name.like("admin"))
                .GROUP_BY(roleCountDef.id)
                .AS("role_stat");

        String actualSql = SELECT(
                userDef.id.as("userId"),
                userDef.name.as("userName"),
                roleCountSubQuery,
                COUNT(joinRoleDef.id).as("joinCount")
        )
                .FROM(userDef)
                .LEFT_JOIN(joinRoleDef).ON(joinRoleDef.id.eq(userDef.id))
                .WHERE(
                        userDef.id.in(
                                SELECT(inRoleDef.id)
                                        .FROM(inRoleDef)
                                        .WHERE(inRoleDef.name.eq("owner"))
                        ),
                        AND(),
                        EXISTS(
                                SELECT()
                                        .FROM(existsRoleDef)
                                        .WHERE(existsRoleDef.id.eq(userDef.id).name.like("team"))
                        ),
                        AND(),
                        NOT_EXISTS(
                                SELECT()
                                        .FROM(notExistsRoleDef)
                                        .WHERE(notExistsRoleDef.id.eq(userDef.id).name.eq("banned"))
                        )
                )
                .GROUP_BY(userDef.id, userDef.name)
                .ORDER_BY_DESC(COUNT(joinRoleDef.id).as("joinCount"))
                .LIMIT(5)
                .sql();
        String expectedSql = """
                SELECT user.id AS `userId`, user.name AS `userName`, (SELECT COUNT(user_role.id) AS `roleCount`, user_role.id
                FROM user_role
                WHERE (user_role.name LIKE #{params.p_0})
                GROUP BY user_role.id) AS `role_stat`, COUNT(ur.id) AS `joinCount`
                FROM user
                LEFT OUTER JOIN user_role ur ON (ur.id = user.id)
                WHERE (user.del <> #{logicDelete} AND ((user.id IN (SELECT user_role.id
                FROM user_role
                WHERE (user_role.name = #{params.p_1}))) AND (EXISTS (SELECT *
                FROM user_role
                WHERE ((user_role.id = user.id AND user_role.name LIKE #{params.p_2})))) AND (NOT EXISTS (SELECT *
                FROM user_role
                WHERE ((user_role.id = user.id AND user_role.name = #{params.p_3}))))))
                GROUP BY user.id, user.name
                ORDER BY COUNT(ur.id) DESC LIMIT 5
                """;

        assertSqlEquals(expectedSql, actualSql);
    }

    @Test
    public void testSelectDefMixedWithExtraSelects() {
        UserDef userDef = UserDef.table();
        UserRoleDef userRoleDef = UserRoleDef.table();
        userRoleDef = userRoleDef.alias("ur");

        String actualSql = SELECT(
                userDef,
                userRoleDef.name.as("roleName"),
                COUNT(userRoleDef.id).as("roleCount"),
                AS(1, "marker")
        )
                .FROM(userDef)
                .LEFT_JOIN(userRoleDef).ON(userRoleDef.id.eq(userDef.id))
                .WHERE(userDef.name.like("alice"))
                .GROUP_BY(userDef.id, userDef.name, userDef.version, userRoleDef.name)
                .ORDER_BY_DESC(COUNT(userRoleDef.id).as("roleCount"))
                .LIMIT(10)
                .sql();
        String expectedSql = """
                SELECT user.id, user.name, user.version, ur.name AS `roleName`, COUNT(ur.id) AS `roleCount`, 1 AS `marker`
                FROM user
                LEFT OUTER JOIN user_role ur ON (ur.id = user.id)
                WHERE (user.del <> #{logicDelete} AND (user.name LIKE #{params.p_0}))
                GROUP BY user.id, user.name, user.version, ur.name
                ORDER BY COUNT(ur.id) DESC LIMIT 10
                """;

        assertSqlEquals(expectedSql, actualSql);
    }

    @Test
    public void testQueryFieldAliasDoesNotLeakToDefReuse() {
        UserDef userDef = UserDef.table();

        SqlWrapper aliasQuery = SELECT(userDef.id.as("userId"), userDef.name.as("userName"))
                .FROM(userDef)
                .WHERE(userDef.name.like("alice"));
        String aliasSql = aliasQuery.sql();
        String expectedAliasSql = """
                SELECT user.id AS `userId`, user.name AS `userName`
                FROM user
                WHERE (user.del <> #{logicDelete} AND (user.name LIKE #{params.p_0}))
                """;

        SqlWrapper reusedDefQuery = SELECT(userDef)
                .FROM(userDef)
                .WHERE(userDef.id.eq(1))
                .ORDER_BY_ASC(userDef.id);
        String reusedDefSql = reusedDefQuery.sql();
        String expectedReusedDefSql = """
                SELECT user.id, user.name, user.version
                FROM user
                WHERE (user.del <> #{logicDelete} AND (user.id = #{params.p_0}))
                ORDER BY id
                """;

        assertSqlEquals(expectedAliasSql, aliasSql);
        assertParamEquals(aliasQuery.getParams(), "p_0", "%alice%");
        assertSqlEquals(expectedReusedDefSql, reusedDefSql);
        assertParamEquals(reusedDefQuery.getParams(), "p_0", 1);
    }

    @Test
    public void testQueryFieldAliasDoesNotLeakToFunctionOrOrder() {
        UserDef userDef = UserDef.table();

        SqlWrapper aliasQuery = SELECT(userDef.id.as("userId"))
                .FROM(userDef);
        String aliasSql = aliasQuery.sql();
        String expectedAliasSql = """
                SELECT user.id AS `userId`
                FROM user
                WHERE (user.del <> #{logicDelete})
                """;

        SqlWrapper functionQuery = SELECT(COUNT(userDef.id).as("idCount"))
                .FROM(userDef)
                .ORDER_BY_DESC(userDef.id);
        String functionSql = functionQuery.sql();
        String expectedFunctionSql = """
                SELECT COUNT(user.id) AS `idCount`
                FROM user
                WHERE (user.del <> #{logicDelete})
                ORDER BY id DESC
                """;

        assertSqlEquals(expectedAliasSql, aliasSql);
        assertSqlEquals(expectedFunctionSql, functionSql);
    }

    @Test
    public void testQueryTableAliasDoesNotLeakToOriginalDefReuse() {
        UserDef userDef = UserDef.table();
        UserRoleDef rawRoleDef = UserRoleDef.table();
        UserRoleDef aliasRoleDef = rawRoleDef.alias("ur");

        SqlWrapper aliasQuery = SELECT(userDef.id, aliasRoleDef.name.as("roleName"))
                .FROM(userDef)
                .LEFT_JOIN(aliasRoleDef).ON(aliasRoleDef.id.eq(userDef.id))
                .WHERE(aliasRoleDef.name.like("admin"));
        String aliasSql = aliasQuery.sql();
        String expectedAliasSql = """
                SELECT user.id, ur.name AS `roleName`
                FROM user
                LEFT OUTER JOIN user_role ur ON (ur.id = user.id)
                WHERE (user.del <> #{logicDelete} AND (ur.name LIKE #{params.p_0}))
                """;

        SqlWrapper rawReuseQuery = SELECT(rawRoleDef.id, rawRoleDef.name)
                .FROM(rawRoleDef)
                .WHERE(rawRoleDef.name.eq("owner"));
        String rawReuseSql = rawReuseQuery.sql();
        String expectedRawReuseSql = """
                SELECT user_role.id, user_role.name
                FROM user_role
                WHERE (user_role.name = #{params.p_0})
                """;

        assertSqlEquals(expectedAliasSql, aliasSql);
        assertParamEquals(aliasQuery.getParams(), "p_0", "%admin%");
        assertSqlEquals(expectedRawReuseSql, rawReuseSql);
        assertParamEquals(rawReuseQuery.getParams(), "p_0", "owner");
    }

    @Test
    public void testInSelectWrapperMergesSubQueryParams() {
        UserDef userDef = UserDef.table();
        UserRoleDef userRoleDef = UserRoleDef.table();

        SelectWrapper subQuery = SELECT(userRoleDef.id);
        subQuery.FROM(userRoleDef).WHERE(userRoleDef.name.like("owner"));

        SqlWrapper outerQuery = SELECT(userDef.id, userDef.name)
                .FROM(userDef)
                .WHERE(userDef.id.in(subQuery))
                .ORDER_BY_DESC(userDef.id);

        String actualSql = outerQuery.sql();
        String expectedSql = """
                SELECT user.id, user.name
                FROM user
                WHERE (user.del <> #{logicDelete} AND (user.id IN (SELECT user_role.id
                FROM user_role
                WHERE (user_role.name LIKE #{params.p_0}))))
                ORDER BY id DESC
                """;

        assertSqlEquals(expectedSql, actualSql);
        assertParamEquals(outerQuery.getParams(), "p_0", "%owner%");
    }

    @Test
    public void testNotInSelectWrapperMergesSubQueryParamsInMixedConditions() {
        UserDef userDef = UserDef.table();
        UserRoleDef userRoleDef = UserRoleDef.table();

        SelectWrapper subQuery = SELECT(userRoleDef.id);
        subQuery.FROM(userRoleDef).WHERE(userRoleDef.name.eq("banned"));

        SqlWrapper outerQuery = SELECT(userDef.id.as("userId"), userDef.name)
                .FROM(userDef)
                .WHERE(userDef.name.like("alice"))
                .AND(userDef.id.notIn(subQuery))
                .LIMIT(5);

        String actualSql = outerQuery.sql();
        String expectedSql = """
                SELECT user.id AS `userId`, user.name
                FROM user
                WHERE (((user.name LIKE #{params.p_0}) AND (user.id NOT IN (SELECT user_role.id
                FROM user_role
                WHERE (user_role.name = #{params.p_1})))) AND user.del <> #{logicDelete})
                LIMIT 5
                """;

        assertSqlEquals(expectedSql, actualSql);
        assertParamEquals(outerQuery.getParams(), "p_0", "%alice%");
        assertParamEquals(outerQuery.getParams(), "p_1", "banned");
    }

    @Test
    public void testAsSqlWrapperSubQueryKeepsSqlAndAlias() {
        UserDef userDef = UserDef.table();

        SqlWrapper subQuery = SELECT(userDef.name)
                .FROM(userDef)
                .WHERE(userDef.id.eq(7));

        SqlWrapper outerQuery = SELECT(userDef.id, AS(subQuery, "pickedName"))
                .FROM(userDef)
                .WHERE(userDef.name.like("alice"));

        String actualSql = outerQuery.sql();
        String expectedSql = """
                SELECT user.id, (SELECT user.name
                FROM user
                WHERE (user.del <> #{logicDelete} AND (user.id = #{params.p_0}))) AS `pickedName`
                FROM user
                WHERE (user.del <> #{logicDelete} AND (user.name LIKE #{params.p_1}))
                """;

        assertSqlEquals(expectedSql, actualSql);
        assertParamEquals(outerQuery.getParams(), "p_0", 7);
        assertParamEquals(outerQuery.getParams(), "p_1", "%alice%");
    }

    @Test
    public void testAsSelectWrapperSubQueryKeepsSqlAndAlias() {
        UserDef userDef = UserDef.table();

        SelectWrapper subQuery = SELECT(userDef.name);
        subQuery.FROM(userDef).WHERE(userDef.id.eq(9));

        SqlWrapper outerQuery = SELECT(userDef.id, AS(subQuery, "pickedName"))
                .FROM(userDef)
                .WHERE(userDef.name.like("alice"));

        String actualSql = outerQuery.sql();
        String expectedSql = """
                SELECT user.id, (SELECT user.name
                FROM user
                WHERE (user.del <> #{logicDelete} AND (user.id = #{params.p_0}))) AS `pickedName`
                FROM user
                WHERE (user.del <> #{logicDelete} AND (user.name LIKE #{params.p_1}))
                """;

        assertSqlEquals(expectedSql, actualSql);
        assertParamEquals(outerQuery.getParams(), "p_0", 9);
        assertParamEquals(outerQuery.getParams(), "p_1", "%alice%");
    }

    @Test
    public void testJoinOnLogicDeleteUsesJoinedTableWhenConditionStartsFromMainTable() {
        UserDef userDef = UserDef.table();
        UserDef joinedUserDef = UserDef.table();
        joinedUserDef = joinedUserDef.alias("joined_user");

        SqlWrapper query = SELECT(userDef.id, joinedUserDef.name.as("joinedName"))
                .FROM(userDef)
                .LEFT_JOIN(joinedUserDef).ON(userDef.id.eq(joinedUserDef.id))
                .WHERE(userDef.name.like("alice"));

        String actualSql = query.sql();
        String expectedSql = """
                SELECT user.id, joined_user.name AS `joinedName`
                FROM user
                LEFT OUTER JOIN user joined_user ON (user.id = joined_user.id AND joined_user.del <> #{logicDelete})
                WHERE (user.del <> #{logicDelete} AND (user.name LIKE #{params.p_0}))
                """;

        assertSqlEquals(expectedSql, actualSql);
        assertParamEquals(query.getParams(), "p_0", "%alice%");
    }

    @Test
    public void testJoinOnLogicDeleteUsesJoinedTableWhenConditionStartsFromJoinedTable() {
        UserDef userDef = UserDef.table();
        UserDef joinedUserDef = UserDef.table();
        joinedUserDef = joinedUserDef.alias("joined_user");

        SqlWrapper query = SELECT(userDef.id, joinedUserDef.name.as("joinedName"))
                .FROM(userDef)
                .LEFT_JOIN(joinedUserDef).ON(joinedUserDef.id.eq(userDef.id))
                .WHERE(userDef.name.like("alice"));

        String actualSql = query.sql();
        String expectedSql = """
                SELECT user.id, joined_user.name AS `joinedName`
                FROM user
                LEFT OUTER JOIN user joined_user ON (joined_user.id = user.id AND joined_user.del <> #{logicDelete})
                WHERE (user.del <> #{logicDelete} AND (user.name LIKE #{params.p_0}))
                """;

        assertSqlEquals(expectedSql, actualSql);
        assertParamEquals(query.getParams(), "p_0", "%alice%");
    }

    @Test
    public void testJoinOnLogicDeleteTracksEachJoinedTableAcrossConsecutiveJoins() {
        UserDef userDef = UserDef.table();
        UserDef firstJoinedUserDef = UserDef.table();
        firstJoinedUserDef = firstJoinedUserDef.alias("first_user");
        UserRoleDef userRoleDef = UserRoleDef.table();
        userRoleDef = userRoleDef.alias("ur");
        UserDef secondJoinedUserDef = UserDef.table();
        secondJoinedUserDef = secondJoinedUserDef.alias("second_user");

        SqlWrapper query = SELECT(
                userDef.id.as("userId"),
                firstJoinedUserDef.name.as("firstName"),
                userRoleDef.name.as("roleName"),
                secondJoinedUserDef.name.as("secondName")
        )
                .FROM(userDef)
                .LEFT_JOIN(firstJoinedUserDef).ON(userDef.id.eq(firstJoinedUserDef.id))
                .LEFT_JOIN(userRoleDef).ON(firstJoinedUserDef.id.eq(userRoleDef.id))
                .LEFT_JOIN(secondJoinedUserDef).ON(userRoleDef.id.eq(secondJoinedUserDef.id))
                .WHERE(userDef.name.like("alice"));

        String actualSql = query.sql();
        String expectedSql = """
                SELECT user.id AS `userId`, first_user.name AS `firstName`, ur.name AS `roleName`, second_user.name AS `secondName`
                FROM user
                LEFT OUTER JOIN user first_user ON (user.id = first_user.id AND first_user.del <> #{logicDelete})
                LEFT OUTER JOIN user_role ur ON (first_user.id = ur.id)
                LEFT OUTER JOIN user second_user ON (ur.id = second_user.id AND second_user.del <> #{logicDelete})
                WHERE (user.del <> #{logicDelete} AND (user.name LIKE #{params.p_0}))
                """;

        assertSqlEquals(expectedSql, actualSql);
        assertParamEquals(query.getParams(), "p_0", "%alice%");
    }
}

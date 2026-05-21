import cn.icframework.mybatis.consts.IcParamsConsts;
import cn.icframework.mybatis.provider.SqlProvider;
import cn.icframework.mybatis.wrapper.SqlWrapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static cn.icframework.mybatis.wrapper.Wrapper.SELECT;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlProviderCountTest {

    @Test
    void countWrapsOriginalQueryWithAggregateCount() {
        UserDef user = UserDef.table();
        SqlWrapper sqlWrapper = SELECT().FROM(user).WHERE(user.name.eq("missing"));
        Map<String, Object> params = new HashMap<>();
        params.put(IcParamsConsts.PARAMETER_SW, sqlWrapper);

        String countSql = SqlProvider.count(params, null);

        assertTrue(countSql.startsWith("select count(1) as `count` from ("));
        assertTrue(countSql.endsWith(") as c"));
    }
}

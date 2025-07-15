package cn.icframework.test;

import cn.icframework.mybatis.wrapper.SqlWrapper;
import cn.icframework.test.t.User;
import cn.icframework.test.t.def.UserDef;

import static cn.icframework.mybatis.wrapper.Wrapper.SELECT;

/**
 *
 * @author hzl
 * @since 2025/6/30
 */
public class Test {
    public static void main(String[] args) {
        User user = new User();
        UserDef table = UserDef.table();
        SqlWrapper offset = SELECT().FROM(table)
                .WHERE(table.name.like("123"))
                .LIMIT(10)
                .OFFSET(0);
        System.out.println(offset.sql());
    }
}

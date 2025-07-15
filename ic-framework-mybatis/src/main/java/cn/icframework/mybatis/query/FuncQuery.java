package cn.icframework.mybatis.query;

import cn.icframework.mybatis.wrapper.SqlWrapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 函数查询类。
 * 用于构建SQL函数查询。
 * @author hzl
 * @since 2023/5/18
 */
@SuppressWarnings("ALL")
@Getter
@Setter
@NoArgsConstructor
public class FuncQuery {
    /**
     * 该字段需要执行的函数
     */
    private String func;
    /**
     * 嵌套查询
     */
    private SqlWrapper sqlWrapper;
}

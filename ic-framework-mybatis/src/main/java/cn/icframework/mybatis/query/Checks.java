package cn.icframework.mybatis.query;


import java.util.function.Function;

/**
 * 条件检查，如果通过则返回内容
 */
public class Checks {
    public static <R, OUT> OUT CHECK(boolean condition, Function<? super R, ? extends OUT> function, R content) {
        if (condition) {
            return function.apply(content);
        }
        return null;
    }
}

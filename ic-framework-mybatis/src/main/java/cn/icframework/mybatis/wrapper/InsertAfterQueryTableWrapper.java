package cn.icframework.mybatis.wrapper;


import java.util.Map;

public class InsertAfterQueryTableWrapper extends SqlWrapper{
    SqlWrapper sqlWrapper;

    public InsertAfterQueryTableWrapper(SqlWrapper sqlWrapper) {
        this.sqlWrapper = sqlWrapper;
    }

    /**
     * 获取插入sql
     * @return sql
     */
    public String sql() {
        return sqlWrapper.sql();
    }

    /**
     * 获取入参
     * @return 参数map
     */
    public Map<String, Object> getParams() {
        return sqlWrapper.getParams();
    }


}

package cn.icframework.mybatis.wrapper;

public class FromWrapper extends SqlWrapper {
    SqlWrapper root;

    public FromWrapper(SqlWrapper sqlWrapper) {
        copyFromAttributes(sqlWrapper);
        this.root = sqlWrapper;
    }

}

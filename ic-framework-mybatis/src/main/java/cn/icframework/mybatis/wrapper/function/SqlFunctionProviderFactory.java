package cn.icframework.mybatis.wrapper.function;

import org.springframework.util.StringUtils;

/**
 * SQL函数提供者工厂类。
 * 用于创建不同数据库的SQL函数提供者。
 * @author hzl
 */
public class SqlFunctionProviderFactory {
    public static SqlFunctionProvider getProvider(String dbType) {
        if (!StringUtils.hasText(dbType)) {
            return new MysqlFunctionProvider();
        }
        switch (dbType.toLowerCase()) {
            case "mysql":
                return new MysqlFunctionProvider();
            case "oracle":
                return new OracleFunctionProvider();
            case "pgsql":
            case "postgresql":
                return new PgsqlFunctionProvider();
            case "sqlserver":
                return new SqlserverFunctionProvider();
            default:
                return new MysqlFunctionProvider();
        }
    }
}

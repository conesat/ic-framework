package cn.icframework.mybatis.config;

import cn.icframework.mybatis.wrapper.FunctionWrapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * SQL函数提供者配置类
 * <p>
 * 该配置类负责在应用启动时根据配置的数据库类型，自动设置相应的SQL函数提供者。
 * 支持多种数据库类型，包括MySQL、Oracle、PostgreSQL、SQL Server等。
 * 
 * 主要功能：
 * <ul>
 * <li>从配置文件中读取数据库类型配置</li>
 * <li>在应用启动时自动初始化SQL函数提供者</li>
 * <li>确保FunctionWrapper使用正确的数据库特定的SQL函数实现</li>
 * </ul>
 * 
 * 配置说明：
 * <ul>
 * <li>通过spring.datasource.db-type配置项指定数据库类型</li>
 * <li>支持的数据库类型：mysql、oracle、pgsql/postgresql、sqlserver</li>
 * <li>默认为mysql数据库类型</li>
 * </ul>
 * 
 * 使用示例：
 * <pre>
 * # application.yml
 * spring:
 *   datasource:
 *     db-type: mysql  # 或 oracle、pgsql、sqlserver
 * </pre>
 *
 * @author hzl
 * @since 2025/7/4
 * @see FunctionWrapper FunctionWrapper用于构建SQL查询条件
 * @see cn.icframework.mybatis.wrapper.function.SqlFunctionProvider SqlFunctionProvider定义了不同数据库的SQL函数实现
 * @see cn.icframework.mybatis.wrapper.function.SqlFunctionProviderFactory SqlFunctionProviderFactory用于创建具体的SQL函数提供者实例
 */
@Configuration
public class FunctionProviderConfig {
    
    /**
     * 数据库类型配置
     * <p>
     * 从配置文件中读取数据库类型，用于确定使用哪种SQL函数提供者。
     * 支持的值：mysql、oracle、pgsql、postgresql、sqlserver
     * 默认值为mysql
     * 
     */
    @Value("${spring.datasource.db-type:mysql}")
    private String dbType;

    /**
     * 初始化SQL函数提供者
     * <p>
     * 在Spring容器启动完成后，根据配置的数据库类型自动设置相应的SQL函数提供者。
     * 该方法会被Spring自动调用，无需手动调用。
     * 
     * 执行流程：
     * <ol>
     * <li>读取配置的数据库类型</li>
     * <li>通过SqlFunctionProviderFactory获取对应的函数提供者</li>
     * <li>设置FunctionWrapper的静态provider字段</li>
     * </ol>
     * 
     * 支持的数据库类型映射：
     * <ul>
     * <li>mysql → MysqlFunctionProvider</li>
     * <li>oracle → OracleFunctionProvider</li>
     * <li>pgsql/postgresql → PgsqlFunctionProvider</li>
     * <li>sqlserver → SqlserverFunctionProvider</li>
     * <li>其他/未配置 → 默认使用MysqlFunctionProvider</li>
     * </ul>
     */
    @PostConstruct
    public void init() {
        FunctionWrapper.autoSetProvider(dbType);
    }
}

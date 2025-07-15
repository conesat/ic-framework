package cn.icframework.mybatis.config;

import cn.icframework.common.consts.IPage;
import cn.icframework.mybatis.cache.CountMsCache;
import cn.icframework.mybatis.utils.MSUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * MyBatis 分页拦截器
 * 
 * 该拦截器用于自动处理分页查询，主要功能包括：
 * 1. 自动执行 COUNT 查询获取总记录数
 * 2. 自动为原始 SQL 添加 LIMIT 子句进行分页
 * 3. 缓存 COUNT 查询的 MappedStatement 对象以提高性能
 * 
 * @author hzl
 * @since 2023/5/23 0023
 */
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class PageInterceptor implements Interceptor {

    /**
     * 缓存 COUNT 查询的 MappedStatement 对象
     * key: MappedStatement ID + "_COUNT"
     * value: 对应的 COUNT 查询 MappedStatement
     */
    protected static CountMsCache<String, MappedStatement> msCountMap = null;

    /**
     * 拦截查询方法，处理分页逻辑
     * 
     * @param invocation 方法调用信息
     * @return 查询结果
     * @throws Throwable 执行异常
     */
    @SuppressWarnings("rawtypes")
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Executor executor = (Executor) invocation.getTarget();
        
        // 只处理 4 个参数的 query 方法（不包含 CacheKey 和 BoundSql）
        if (args.length == 4) {
            RowBounds rowBounds = (RowBounds) args[2];
            Object parameter = args[1];
            
            // 如果参数直接是 IPage 对象
            if (parameter != null && parameter.getClass().isAssignableFrom(IPage.class)) {
                pageCount((IPage) parameter, ms, parameter, executor, rowBounds);
            } 
            // 如果参数是 MapperMethod.ParamMap（多参数情况）
            else if (parameter != null && parameter.getClass().isAssignableFrom(MapperMethod.ParamMap.class)) {
                MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) parameter;
                for (Object key : paramMap.keySet()) {
                    Object object = paramMap.get(key);
                    if (!object.getClass().isAssignableFrom(IPage.class)) {
                        continue;
                    }
                    
                    // 执行 COUNT 查询获取总记录数
                    long total = pageCount((IPage) object, ms, parameter, executor, rowBounds);
                    if (total == 0) {
                        // 如果没有数据，直接返回空列表
                        return Collections.emptyList();
                    } else {
                        // 执行分页查询
                        return page((IPage) object, ms, parameter, executor, rowBounds);
                    }
                }
            }
        }
        
        // 如果不是分页查询，直接执行原始方法
        return invocation.proceed();
    }

    /**
     * 执行 COUNT 查询获取总记录数
     * 
     * @param object IPage 分页对象
     * @param ms 原始查询的 MappedStatement
     * @param parameter 查询参数
     * @param executor MyBatis 执行器
     * @param rowBounds 行边界
     * @return 总记录数
     * @throws SQLException SQL 异常
     */
    private static long pageCount(IPage object, MappedStatement ms, Object parameter, Executor executor, RowBounds rowBounds) throws SQLException {
        // 获取原始 SQL
        BoundSql boundSql = ms.getBoundSql(parameter);
        
        // 构造 COUNT 查询 SQL：select count(1) from (原始SQL) c
        BoundSql countBoundSql = new BoundSql(ms.getConfiguration(), 
            String.format("select count(1) from (%s) c", boundSql.getSql()), 
            boundSql.getParameterMappings(), parameter);
        
        // 创建缓存键
        CacheKey cacheKey = executor.createCacheKey(ms, parameter, rowBounds, countBoundSql);
        String countMsId = ms.getId() + "_COUNT";
        
        // 从缓存中获取 COUNT 查询的 MappedStatement
        MappedStatement countMs = null;
        if (msCountMap != null) {
            countMs = msCountMap.get(countMsId);
        }
        
        // 如果缓存中没有，则自动创建
        if (countMs == null) {
            // 根据当前的 ms 创建一个返回值为 Long 类型的 ms
            countMs = MSUtils.newCountMappedStatement(ms, countMsId);
            if (msCountMap != null) {
                msCountMap.put(countMsId, countMs);
            }
        }
        
        // 执行 COUNT 查询
        List<Number> countResultList = executor.query(countMs, parameter, RowBounds.DEFAULT, null, cacheKey, countBoundSql);
        
        if (countResultList == null || countResultList.isEmpty()) {
            // 没有数据的情况
            object.setTotal(0);
            object.setPages(0);
            return 0;
        } else {
            // 设置总记录数和总页数
            long total = countResultList.get(0).longValue();
            object.setTotal(total);
            object.setPages(total / object.getPageSize());
            return total;
        }
    }

    /**
     * 执行分页查询
     * 
     * @param object IPage 分页对象
     * @param ms 原始查询的 MappedStatement
     * @param parameter 查询参数
     * @param executor MyBatis 执行器
     * @param rowBounds 行边界
     * @return 分页查询结果
     * @throws SQLException SQL 异常
     */
    private static List<Object> page(IPage object, MappedStatement ms, Object parameter, Executor executor, RowBounds rowBounds) throws SQLException {
        // 获取原始 SQL
        BoundSql boundSql = ms.getBoundSql(parameter);
        
        // 构造分页 SQL：原始SQL LIMIT offset,pageSize
        int offset = (object.getPageIndex() - 1) * object.getPageSize();
        BoundSql pageBoundSql = new BoundSql(ms.getConfiguration(), 
            String.format("%s LIMIT %d,%d", boundSql.getSql(), offset, object.getPageSize()), 
            boundSql.getParameterMappings(), parameter);
        
        // 创建缓存键
        CacheKey cacheKey = executor.createCacheKey(ms, parameter, rowBounds, pageBoundSql);
        
        // 执行分页查询
        return executor.query(ms, parameter, RowBounds.DEFAULT, null, cacheKey, pageBoundSql);
    }

    /**
     * 设置拦截器属性
     * 
     * @param properties 配置属性
     */
    @Override
    public void setProperties(Properties properties) {
        // 初始化 COUNT MappedStatement 缓存
        msCountMap = new CountMsCache<>(properties, "ms");
    }
}
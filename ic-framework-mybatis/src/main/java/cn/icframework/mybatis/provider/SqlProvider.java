package cn.icframework.mybatis.provider;

import cn.icframework.mybatis.consts.IcParamsConsts;
import cn.icframework.mybatis.consts.StatementType;
import cn.icframework.mybatis.mapper.BasicMapper;
import cn.icframework.mybatis.parse.DeleteSqlParse;
import cn.icframework.mybatis.parse.InsertSqlParse;
import cn.icframework.mybatis.parse.SelectSqlParse;
import cn.icframework.mybatis.parse.UpdateSqlParse;
import cn.icframework.mybatis.wrapper.SqlWrapper;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * SqlProvider 是 MyBatis 的 SQL 构建提供者，负责根据不同的参数和上下文动态生成 SQL 语句。
 * 主要用于配合 Mapper 注解实现通用的增删改查等操作，支持批量插入、条件更新、逻辑删除、动态查询等。
 * <p>
 * 该类中的方法大多为静态方法，接收参数 Map 和 ProviderContext，
 * 通过参数解析和封装，调用 SqlParse 或 SqlWrapper 相关方法生成最终 SQL。
 * 
 * <p>
 * 注意：所有方法均假定参数已按约定封装，部分方法会自动注入逻辑删除相关参数。
 * 
 *
 * @author hzl
 * @since 2023/5/17
 */
public class SqlProvider {

    private static final InsertSqlParse insertSqlParse = new InsertSqlParse();
    private static final UpdateSqlParse updateSqlParse = new UpdateSqlParse();
    private static final SelectSqlParse selectSqlParse = new SelectSqlParse();
    private static final DeleteSqlParse deleteSqlParse = new DeleteSqlParse();

    /**
     * 构建批量插入的 SQL 语句。
     * <p>
     * 适用于批量插入实体对象列表，通常用于 {@link BasicMapper#insertBatch(List)}。
     * 
     *
     * @param params  参数 Map，需包含 key=IcParamsConsts.PARAMETER_ENTITY，value=实体对象 List
     * @param context MyBatis ProviderContext
     * @return 批量插入 SQL 语句
     * @throws IllegalArgumentException 如果实体列表为空
     */
    public static String insertBatch(Map<String, Object> params, ProviderContext context) {
        List<?> entities = (List<?>) params.get(IcParamsConsts.PARAMETER_ENTITY);
        Assert.notEmpty(entities, "插入实体参数不能为空");
        return insertSqlParse.parseInsertBatch(entities);
    }

    /**
     * 构建单条插入的 SQL 语句。
     * <p>
     * 适用于插入单个实体对象，通常用于 {@link BasicMapper#insert(Object)}。
     * 
     *
     * @param params  参数 Map，需包含 key=IcParamsConsts.PARAMETER_ENTITY，value=实体对象
     * @param context MyBatis ProviderContext
     * @return 插入 SQL 语句
     * @throws IllegalArgumentException 如果实体对象为 null
     */
    public static String insert(Map<String, Object> params, ProviderContext context) {
        Object entity = params.get(IcParamsConsts.PARAMETER_ENTITY);
        Assert.notNull(entity, "插入实体参数不能为空");
        return insertSqlParse.parseInsert(entity);
    }

    /**
     * 通过 SqlWrapper 构建插入 SQL 语句。
     * <p>
     * 支持自定义 SQL 构建，适用于复杂插入场景，通常用于 {@link BasicMapper#insertBySqlWrapper(SqlWrapper)}。
     * 方法内部会自动注入逻辑删除字段。
     * 
     *
     * @param params  参数 Map，需包含 key=IcParamsConsts.PARAMETER_SW，value=SqlWrapper
     * @param context MyBatis ProviderContext
     * @return 插入 SQL 语句
     * @throws IllegalArgumentException 如果 SqlWrapper 为 null
     */
    public static String insertBySqlWrapper(Map<String, Object> params, ProviderContext context) {
        // 不管用不用先放逻辑删除字段进去
        params.put(IcParamsConsts.PARAMETER_LOGIC_DELETE, IcParamsConsts.VALUE_LOGIC_DELETED);
        SqlWrapper sqlWrapper = (SqlWrapper) params.get(IcParamsConsts.PARAMETER_SW);
        Assert.notNull(sqlWrapper, "插入实体参数不能为空");
        String sql = sqlWrapper.sql();
        sqlWrapper.mixParams(params);
        return sql;
    }


    /**
     * 构建根据主键更新的 SQL 语句（忽略 null 字段）。
     * <p>
     * 适用于根据主键更新实体，忽略为 null 的字段，通常用于 {@link BasicMapper#updateById(Object)}。
     * 
     *
     * @param params  参数 Map，需包含 key=IcParamsConsts.PARAMETER_ENTITY，value=实体对象
     * @param context MyBatis ProviderContext
     * @return 更新 SQL 语句
     * @throws IllegalArgumentException 如果实体对象为 null
     */
    public static String updateById(Map<String, Object> params, ProviderContext context) {
        Object entity = params.get(IcParamsConsts.PARAMETER_ENTITY);
        Assert.notNull(entity, "更新实体参数不能为空");
        return updateSqlParse.parseUpdateById(entity, false);
    }

    /**
     * 构建根据主键更新的 SQL 语句（包含 null 字段）。
     * <p>
     * 适用于根据主键更新实体，null 字段也会被更新，通常用于 {@link BasicMapper#updateByIdWithNull(Object)}。
     * 
     *
     * @param params  参数 Map，需包含 key=IcParamsConsts.PARAMETER_ENTITY，value=实体对象
     * @param context MyBatis ProviderContext
     * @return 更新 SQL 语句
     * @throws IllegalArgumentException 如果实体对象为 null
     */
    public static String updateByIdWithNull(Map<String, Object> params, ProviderContext context) {
        Object entity = params.get(IcParamsConsts.PARAMETER_ENTITY);
        Assert.notNull(entity, "更新实体参数不能为空");
        return updateSqlParse.parseUpdateById(entity, true);
    }

    /**
     * 构建根据条件更新的 SQL 语句（忽略 null 字段）。
     * <p>
     * 适用于根据条件批量更新，忽略为 null 的字段，通常用于 {@link BasicMapper#update(Object, SqlWrapper)}。
     * 方法内部会自动注入逻辑删除字段。
     * 
     *
     * @param params  参数 Map，需包含 key=IcParamsConsts.PARAMETER_ENTITY，value=实体对象
     * @param context MyBatis ProviderContext
     * @return 更新 SQL 语句
     * @throws IllegalArgumentException 如果实体对象为 null
     */
    public static String update(Map<String, Object> params, ProviderContext context) {
        Object entity = params.get(IcParamsConsts.PARAMETER_ENTITY);
        // 不管用不用先放逻辑删除字段进去
        params.put(IcParamsConsts.PARAMETER_LOGIC_DELETE, IcParamsConsts.VALUE_LOGIC_DELETED);
        Assert.notNull(entity, "更新实体参数不能为空");
        return updateSqlParse.parseUpdate(entity, params, false);
    }

    /**
     * 通过 SqlWrapper 构建条件更新 SQL 语句（忽略 null 字段）。
     * <p>
     * 适用于自定义条件更新，通常用于 {@link BasicMapper#update(Object, SqlWrapper)}。
     * 方法内部会自动注入逻辑删除字段。
     * 
     *
     * @param params  参数 Map，需包含 key=IcParamsConsts.PARAMETER_SW，value=SqlWrapper
     * @param context MyBatis ProviderContext
     * @return 更新 SQL 语句
     * @throws IllegalArgumentException 如果 SqlWrapper 为 null
     */
    public static String updateByWrapper(Map<String, Object> params, ProviderContext context) {
        // 不管用不用先放逻辑删除字段进去
        params.put(IcParamsConsts.PARAMETER_LOGIC_DELETE, IcParamsConsts.VALUE_LOGIC_DELETED);
        SqlWrapper sql = (SqlWrapper) params.get(IcParamsConsts.PARAMETER_SW);
        sql.setStatementType(StatementType.UPDATE);
        Assert.notNull(sql, "UpdateWrapper不能为空");
        sql.mixParams(params);
        return sql.sql();
    }

    /**
     * 构建根据条件更新的 SQL 语句（包含 null 字段）。
     * <p>
     * 适用于根据条件批量更新，null 字段也会被更新，通常用于 {@link BasicMapper#updateWithNull(Object, SqlWrapper)}。
     * 方法内部会自动注入逻辑删除字段。
     * 
     *
     * @param params  参数 Map，需包含 key=IcParamsConsts.PARAMETER_ENTITY，value=实体对象
     * @param context MyBatis ProviderContext
     * @return 更新 SQL 语句
     * @throws IllegalArgumentException 如果实体对象为 null
     */
    public static String updateWithNull(Map<String, Object> params, ProviderContext context) {
        Object entity = params.get(IcParamsConsts.PARAMETER_ENTITY);
        // 不管用不用先放逻辑删除字段进去
        params.put(IcParamsConsts.PARAMETER_LOGIC_DELETE, IcParamsConsts.VALUE_LOGIC_DELETED);
        Assert.notNull(entity, "更新实体参数不能为空");
        return updateSqlParse.parseUpdate(entity, params, true);
    }


    /**
     * 构建根据主键查询单条记录的 SQL 语句。
     * <p>
     * 适用于根据主键查询单条记录，通常用于 {@link BasicMapper#selectById(Serializable)}。
     * 方法内部会自动注入逻辑删除字段。
     * 
     *
     * @param params  参数 Map
     * @param context MyBatis ProviderContext
     * @return 查询 SQL 语句
     */
    public static String getById(Map<String, Object> params, ProviderContext context) {
        // 不管用不用先放逻辑删除字段进去
        params.put(IcParamsConsts.PARAMETER_LOGIC_DELETE, IcParamsConsts.VALUE_LOGIC_DELETED);
        return selectSqlParse.parseSelectOneById(context.getMapperType());
    }

    /**
     * 构建根据主键集合批量查询的 SQL 语句。
     * <p>
     * 适用于根据主键集合批量查询，通常用于 {@link BasicMapper#selectByIds(Collection)}。
     * 方法内部会自动注入逻辑删除字段。
     * 
     *
     * @param params  参数 Map
     * @param context MyBatis ProviderContext
     * @return 查询 SQL 语句
     */
    public static String selectByIds(Map<String, Object> params, ProviderContext context) {
        // 不管用不用先放逻辑删除字段进去
        params.put(IcParamsConsts.PARAMETER_LOGIC_DELETE, IcParamsConsts.VALUE_LOGIC_DELETED);
        return selectSqlParse.parseSelectByIds(context.getMapperType());
    }

//    /**
//     * selectOneById 的 sql 构建
//     *
//     * @param params
//     * @param context
//     * @return sql
//     * @see BasicMapper#selectOneByEntityId(Object)
//     */
//    public static String selectOneByEntityId(Map<String, Object> params, ProviderContext context) {
//        // 不管用不用先放逻辑删除字段进去
//        params.put(IcParamsConsts.PARAMETER_LOGIC_DELETE, IcParamsConsts.VALUE_LOGIC_DELETED);
//        Object entity = params.get(IcParamsConsts.PARAMETER_ENTITY);
//        return SqlParse.parseSelectOneByEntityId(entity, params);
//    }

    /**
     * 构建根据主键集合批量删除的 SQL 语句。
     * <p>
     * 适用于根据主键集合批量删除，通常用于 {@link BasicMapper#deleteByIds(Collection)}。
     * 方法内部会自动注入逻辑删除字段。
     * 
     *
     * @param params  参数 Map
     * @param context MyBatis ProviderContext
     * @return 删除 SQL 语句
     */
    public static String deleteByIds(Map<String, Object> params, ProviderContext context) {
        // 不管用不用先放逻辑删除字段进去
        params.put(IcParamsConsts.PARAMETER_LOGIC_DELETE, IcParamsConsts.VALUE_LOGIC_DELETED);
        return deleteSqlParse.parseDeleteByIds(context.getMapperType());
    }

    /**
     * 构建根据主键删除的 SQL 语句。
     * <p>
     * 适用于根据主键删除单条记录，通常用于 {@link BasicMapper#deleteById(Serializable)}。
     * 方法内部会自动注入逻辑删除字段。
     * 
     *
     * @param params  参数 Map
     * @param context MyBatis ProviderContext
     * @return 删除 SQL 语句
     */
    public static String deleteById(Map<String, Object> params, ProviderContext context) {
        // 不管用不用先放逻辑删除字段进去
        params.put(IcParamsConsts.PARAMETER_LOGIC_DELETE, IcParamsConsts.VALUE_LOGIC_DELETED);
        return deleteSqlParse.parseDeleteById(context.getMapperType());
    }

//    /**
//     * selectOneById 的 sql 构建
//     *
//     * @param params
//     * @param context
//     * @return sql
//     * @see BasicMapper#deleteByEntityId(Object)
//     */
//    public static String deleteByEntityId(Map<String, Object> params, ProviderContext context) {
//        Object entity = params.get(IcParamsConsts.PARAMETER_ENTITY);
//        return SqlParse.parseDeleteByEntityId(entity);
//    }

    /**
     * 构建根据条件统计数量的 SQL 语句。
     * <p>
     * 适用于根据条件统计记录数，通常用于 {@link BasicMapper#count(SqlWrapper)}。
     * 
     *
     * @param params  参数 Map，需包含 key=IcParamsConsts.PARAMETER_SW，value=SqlWrapper
     * @param context MyBatis ProviderContext
     * @return 统计 SQL 语句
     * @throws IllegalArgumentException 如果 SqlWrapper 为 null
     */
    public static String count(Map<String, Object> params, ProviderContext context) {
        SqlWrapper sqlWrapper = getSelectWrapper(params);
        return sqlWrapper.countSql();
    }

    /**
     * 构建分页统计数量的 SQL 语句。
     * <p>
     * 适用于分页场景下的总数统计，通常用于 {@link BasicMapper#countPage(SqlWrapper)}。
     * 
     *
     * @param params  参数 Map，需包含 key=IcParamsConsts.PARAMETER_SW，value=SqlWrapper
     * @param context MyBatis ProviderContext
     * @return 分页统计 SQL 语句
     * @throws IllegalArgumentException 如果 SqlWrapper 为 null
     */
    public static String countPage(Map<String, Object> params, ProviderContext context) {
        SqlWrapper sqlWrapper = getSelectWrapper(params);
        return String.format("select count(1) as `count` from (%s) as c", sqlWrapper.countSql());
    }


    /**
     * 构建根据条件查询多条记录的 SQL 语句。
     * <p>
     * 适用于根据条件查询多条记录，通常用于 {@link BasicMapper#selectMap(SqlWrapper)}。
     * 
     *
     * @param params  参数 Map，需包含 key=IcParamsConsts.PARAMETER_SW，value=SqlWrapper
     * @param context MyBatis ProviderContext
     * @return 查询 SQL 语句
     * @throws IllegalArgumentException 如果 SqlWrapper 为 null
     */
    public static String selectMap(Map<String, Object> params, ProviderContext context) {
        return getSelectWrapper(params).sql();
    }

    /**
     * 构建根据条件查询单条记录的 SQL 语句。
     * <p>
     * 适用于根据条件查询单条记录，通常用于 {@link BasicMapper#selectOneMap(SqlWrapper)}。
     * 
     *
     * @param params  参数 Map，需包含 key=IcParamsConsts.PARAMETER_SW，value=SqlWrapper
     * @param context MyBatis ProviderContext
     * @return 查询 SQL 语句
     * @throws IllegalArgumentException 如果 SqlWrapper 为 null
     */
    public static String selectOneMap(Map<String, Object> params, ProviderContext context) {
        SqlWrapper sqlWrapper = getSelectWrapper(params);
        sqlWrapper.LIMIT(1);
        return sqlWrapper.sql();
    }


    /**
     * 构建根据条件删除的 SQL 语句。
     * <p>
     * 适用于根据条件批量删除，通常用于 {@link BasicMapper#delete(SqlWrapper)}。
     * 方法内部会自动注入逻辑删除字段。
     * 
     *
     * @param params  参数 Map，需包含 key=IcParamsConsts.PARAMETER_SW，value=SqlWrapper
     * @param context MyBatis ProviderContext
     * @return 删除 SQL 语句
     * @throws IllegalArgumentException 如果 SqlWrapper 为 null
     */
    public static String delete(Map<String, Object> params, ProviderContext context) {
        // 不管用不用先放逻辑删除字段进去
        params.put(IcParamsConsts.PARAMETER_LOGIC_DELETE, IcParamsConsts.VALUE_LOGIC_DELETED);
        SqlWrapper sqlWrapper = (SqlWrapper) params.get(IcParamsConsts.PARAMETER_SW);
        sqlWrapper.setStatementType(StatementType.DELETE);
        Assert.notNull(sqlWrapper, "SqlWrapper 参数不能为空");
        sqlWrapper.setStatementType(StatementType.DELETE);
        sqlWrapper.mixParams(params);
        return sqlWrapper.sql();
    }

    /**
     * 获取查询构建器 SqlWrapper，并自动注入逻辑删除参数。
     * <p>
     * 用于 select/count 等方法内部统一获取并配置 SqlWrapper。
     * 
     *
     * @param params  参数 Map，需包含 key=IcParamsConsts.PARAMETER_SW，value=SqlWrapper
     * @return 配置好的 SqlWrapper
     * @throws IllegalArgumentException 如果 SqlWrapper 为 null
     */
    private static SqlWrapper getSelectWrapper(Map<String, Object> params) {
        // 不管用不用先放逻辑删除字段进去
        params.put(IcParamsConsts.PARAMETER_LOGIC_DELETE, IcParamsConsts.VALUE_LOGIC_DELETED);
        SqlWrapper sqlWrapper = (SqlWrapper) params.get(IcParamsConsts.PARAMETER_SW);
        Assert.notNull(sqlWrapper, "查询条件不能为空");
        sqlWrapper.setStatementType(StatementType.SELECT);
        sqlWrapper.mixParams(params);
        return sqlWrapper;
    }
}

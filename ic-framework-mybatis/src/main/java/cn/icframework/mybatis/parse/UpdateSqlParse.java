package cn.icframework.mybatis.parse;

import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.consts.IcParamsConsts;
import cn.icframework.mybatis.consts.StatementType;
import cn.icframework.mybatis.utils.ModelClassUtils;
import cn.icframework.mybatis.wrapper.UpdateWrapper;

import java.util.Map;

import static cn.icframework.mybatis.wrapper.Wrapper.UPDATE;

/**
 * UpdateSqlParse 用于解析和生成 UPDATE 相关的 SQL 语句实现。
 * 主要负责根据实体对象和参数动态构建更新语句。
 * 实现了 ISqlParse 接口，仅支持与更新相关的方法，其他方法抛出不支持异常。
 */
public class UpdateSqlParse implements ISqlParse {
    /**
     * 解析根据主键更新实体的 SQL 语句。
     *
     * @param entity   需要更新的实体对象，必须包含主键信息
     * @param withNull 是否包含为 null 的字段（true: null 字段也会被 set，false: 跳过 null 字段）
     * @return 构建好的 UPDATE SQL 语句
     */
    @Override
    public String parseUpdateById(Object entity, boolean withNull) {
        String tableName = ModelClassUtils.getTableName(entity.getClass());
        UpdateWrapper updateWrapper = UPDATE(tableName);
        Table table = entity.getClass().getAnnotation(Table.class);
        ModelClassUtils.handleTableFieldInfo(entity.getClass(), field -> {
            SqlParseUtils.handlerUpdateSet(entity, withNull, field, updateWrapper);
            ModelClassUtils.mutilateWhereKey(table, field, updateWrapper);
            return true;
        });
        return updateWrapper.sql();
    }

    /**
     * 解析根据条件更新实体的 SQL 语句。
     *
     * @param entity   需要更新的实体对象
     * @param params   额外参数，通常包含 UpdateWrapper（PARAMETER_SW），可为空
     * @param withNull 是否包含为 null 的字段（true: null 字段也会被 set，false: 跳过 null 字段）
     * @return 构建好的 UPDATE SQL 语句
     */
    @Override
    public String parseUpdate(Object entity, Map<String, Object> params, boolean withNull) {
        UpdateWrapper sql = (UpdateWrapper) params.get(IcParamsConsts.PARAMETER_SW);
        if (sql == null) {
            sql = UPDATE(ModelClassUtils.getTableName(entity.getClass()));
        }
        sql.setStatementType(StatementType.UPDATE);
        UpdateWrapper finalSql = sql;
        ModelClassUtils.handleTableFieldInfo(entity.getClass(), field -> {
            SqlParseUtils.handlerUpdateSet(entity, withNull, field, finalSql);
            return true;
        });
        sql.mixParams(params);
        return sql.sql();
    }

    /**
     * 不支持的操作：插入单个实体。
     *
     * @param entity 实体对象
     * @return 无
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseInsert(Object entity) {
        throw new UnsupportedOperationException();
    }

    /**
     * 不支持的操作：批量插入实体。
     *
     * @param entities 实体对象列表
     * @return 无
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseInsertBatch(java.util.List<?> entities) {
        throw new UnsupportedOperationException();
    }

    /**
     * 不支持的操作：根据主键查询单个实体。
     *
     * @param mapperType Mapper 类型
     * @return 无
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseSelectOneById(Class<?> mapperType) {
        throw new UnsupportedOperationException();
    }

    /**
     * 不支持的操作：根据主键集合查询实体列表。
     *
     * @param mapperType Mapper 类型
     * @return 无
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseSelectByIds(Class<?> mapperType) {
        throw new UnsupportedOperationException();
    }

    /**
     * 不支持的操作：根据主键集合删除实体。
     *
     * @param mapperType Mapper 类型
     * @return 无
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseDeleteByIds(Class<?> mapperType) {
        throw new UnsupportedOperationException();
    }

    /**
     * 不支持的操作：根据主键删除实体。
     *
     * @param mapperType Mapper 类型
     * @return 无
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseDeleteById(Class<?> mapperType) {
        throw new UnsupportedOperationException();
    }
} 
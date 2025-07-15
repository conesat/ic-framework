package cn.icframework.mybatis.parse;

import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.consts.IcParamsConsts;
import cn.icframework.mybatis.utils.ModelClassUtils;

import java.lang.reflect.Field;

import static cn.icframework.mybatis.wrapper.Wrapper.DELETE_FROM;

/**
 * DeleteSqlParse 用于生成删除相关的 SQL 语句解析实现类，实现 ISqlParse 接口。
 * <p>
 * 该类主要负责根据不同的删除场景（如根据主键ID删除、批量删除等）生成对应的SQL语句。
 * 其它非删除相关方法均抛出不支持操作异常。
 * 
 */
public class DeleteSqlParse implements ISqlParse {
    /**
     * 解析批量根据主键ID删除的SQL语句。
     * <p>
     * 该方法用于生成批量删除的SQL脚本，通常用于根据主键ID集合批量删除数据。
     * 生成的SQL包含where条件，主键以IN的方式拼接。
     * 
     *
     * @param mapperType Mapper接口的Class类型，用于获取对应的实体类Class。
     * @return 返回批量删除的SQL脚本字符串，包含where条件。
     */
    @Override
    public String parseDeleteByIds(Class<?> mapperType) {
        // 获取实体类Class
        Class<?> entityClass = SqlParseUtils.getEntityClass(mapperType);
        // 获取主键IN条件
        String whereIdIns = SqlParseUtils.getWhereIdIn(entityClass);
        // 组装批量删除SQL
        return String.format("""
                <script>
                   %s
                </script>
                """, DELETE_FROM(true, entityClass).WHERE(whereIdIns).sql());
    }

    /**
     * 解析根据主键ID删除的SQL语句。
     * <p>
     * 该方法用于生成单条删除的SQL语句，通常用于根据主键ID删除单条数据。
     * 
     *
     * @param mapperType Mapper接口的Class类型，用于获取对应的实体类Class。
     * @return 返回单条删除的SQL字符串。
     */
    @Override
    public String parseDeleteById(Class<?> mapperType) {
        // 获取实体类Class
        Class<?> entityClass = SqlParseUtils.getEntityClass(mapperType);
        Table table = entityClass.getAnnotation(Table.class);
        // 获取主键字段
        Field idField = ModelClassUtils.getIdField(entityClass);
        // 获取主键字段对应的表字段名
        String idFieldName = ModelClassUtils.getTableColumnName(table, idField);
        // 组装单条删除SQL
        return DELETE_FROM(entityClass).WHERE(String.format("`%s` = #{%s}", idFieldName, IcParamsConsts.PARAMETER_PRIMARY_KEY)).sql();
    }

    // 其它方法暂不实现，抛出不支持异常

    /**
     * 插入操作暂不支持。
     * <p>
     * 调用该方法会抛出 {@link UnsupportedOperationException} 异常。
     * 
     *
     * @param entity 实体对象。
     * @return 无返回值。
     * @throws UnsupportedOperationException 始终抛出该异常。
     */
    @Override
    public String parseInsert(Object entity) {
        throw new UnsupportedOperationException();
    }

    /**
     * 批量插入操作暂不支持。
     * <p>
     * 调用该方法会抛出 {@link UnsupportedOperationException} 异常。
     * 
     *
     * @param entities 实体对象集合。
     * @return 无返回值。
     * @throws UnsupportedOperationException 始终抛出该异常。
     */
    @Override
    public String parseInsertBatch(java.util.List<?> entities) {
        throw new UnsupportedOperationException();
    }

    /**
     * 根据ID更新操作暂不支持。
     * <p>
     * 调用该方法会抛出 {@link UnsupportedOperationException} 异常。
     * 
     *
     * @param entity 实体对象。
     * @param withNull 是否包含null字段。
     * @return 无返回值。
     * @throws UnsupportedOperationException 始终抛出该异常。
     */
    @Override
    public String parseUpdateById(Object entity, boolean withNull) {
        throw new UnsupportedOperationException();
    }

    /**
     * 条件更新操作暂不支持。
     * <p>
     * 调用该方法会抛出 {@link UnsupportedOperationException} 异常。
     * 
     *
     * @param entity 实体对象。
     * @param params 额外参数。
     * @param withNull 是否包含null字段。
     * @return 无返回值。
     * @throws UnsupportedOperationException 始终抛出该异常。
     */
    @Override
    public String parseUpdate(Object entity, java.util.Map<String, Object> params, boolean withNull) {
        throw new UnsupportedOperationException();
    }

    /**
     * 根据ID查询单条操作暂不支持。
     * <p>
     * 调用该方法会抛出 {@link UnsupportedOperationException} 异常。
     * 
     *
     * @param mapperType Mapper接口的Class类型。
     * @return 无返回值。
     * @throws UnsupportedOperationException 始终抛出该异常。
     */
    @Override
    public String parseSelectOneById(Class<?> mapperType) {
        throw new UnsupportedOperationException();
    }

    /**
     * 根据ID批量查询操作暂不支持。
     * <p>
     * 调用该方法会抛出 {@link UnsupportedOperationException} 异常。
     * 
     *
     * @param mapperType Mapper接口的Class类型。
     * @return 无返回值。
     * @throws UnsupportedOperationException 始终抛出该异常。
     */
    @Override
    public String parseSelectByIds(Class<?> mapperType) {
        throw new UnsupportedOperationException();
    }
} 
package cn.icframework.mybatis.parse;

import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.wrapper.SqlWrapper;
import cn.icframework.mybatis.consts.CompareEnum;
import cn.icframework.mybatis.utils.ModelClassUtils;
import cn.icframework.mybatis.consts.IcParamsConsts;

import java.lang.reflect.Field;

import static cn.icframework.mybatis.wrapper.Wrapper.SELECT;
import static cn.icframework.mybatis.wrapper.Wrapper.SELECT_FROM;

/**
 * SelectSqlParse 用于实现基于实体的 select 相关 SQL 解析。
 * 该类实现了 ISqlParse 接口，仅支持 select 相关方法，其他方法抛出不支持异常。
 * 主要用于根据 mapper 类型自动生成 select 语句。
 */
public class SelectSqlParse implements ISqlParse {
    /**
     * 解析生成根据主键查询单条记录的 SQL 语句。
     *
     * @param mapperType Mapper 接口的 Class 类型
     * @return 查询单条记录的 SQL 语句
     */
    @Override
    public String parseSelectOneById(Class<?> mapperType) {
        Class<?> entityClass = SqlParseUtils.getEntityClass(mapperType);
        Table table = entityClass.getAnnotation(Table.class);
        SqlWrapper sql = SELECT().FROM_ENTITY(entityClass);
        Field idField = ModelClassUtils.getIdField(entityClass);
        sql.WHERE(ModelClassUtils.getTableColumnName(table, idField) + CompareEnum.EQ.getKey(sql.isCoverXml()) + String.format("#{%s}", IcParamsConsts.PARAMETER_PRIMARY_KEY));
        sql.LIMIT(1);
        return sql.sql();
    }

    /**
     * 根据主键集合批量查询记录。
     *
     * @param mapperType Mapper接口的Class类型
     * @return 批量查询记录的 SQL 语句（MyBatis {@code <script>} 包裹）
     */
    @Override
    public String parseSelectByIds(Class<?> mapperType) {
        Class<?> entityClass = SqlParseUtils.getEntityClass(mapperType);
        String whereIdIns = SqlParseUtils.getWhereIdIn(entityClass);
        return String.format("""
                <script>
                   %s
                </script>
                """, SELECT_FROM(true, entityClass).WHERE(whereIdIns).sql());
    }

    /**
     * 插入单条记录（SelectSqlParse 不支持该操作）。
     *
     * @param entity 实体对象
     * @return 不返回，直接抛出异常
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseInsert(Object entity) {
        throw new UnsupportedOperationException();
    }

    /**
     * 批量插入记录（SelectSqlParse 不支持该操作）。
     *
     * @param entities 实体对象集合
     * @return 不返回，直接抛出异常
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseInsertBatch(java.util.List<?> entities) {
        throw new UnsupportedOperationException();
    }

    /**
     * 根据主键更新记录（SelectSqlParse 不支持该操作）。
     *
     * @param entity   实体对象
     * @param withNull 是否包含 null 字段
     * @return 不返回，直接抛出异常
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseUpdateById(Object entity, boolean withNull) {
        throw new UnsupportedOperationException();
    }

    /**
     * 根据条件更新记录（SelectSqlParse 不支持该操作）。
     *
     * @param entity   实体对象
     * @param params   额外参数
     * @param withNull 是否包含 null 字段
     * @return 不返回，直接抛出异常
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseUpdate(Object entity, java.util.Map<String, Object> params, boolean withNull) {
        throw new UnsupportedOperationException();
    }

    /**
     * 根据主键集合批量删除记录（SelectSqlParse 不支持该操作）。
     *
     * @param mapperType Mapper 接口的 Class 类型
     * @return 不返回，直接抛出异常
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseDeleteByIds(Class<?> mapperType) {
        throw new UnsupportedOperationException();
    }

    /**
     * 根据主键删除单条记录（SelectSqlParse 不支持该操作）。
     *
     * @param mapperType Mapper 接口的 Class 类型
     * @return 不返回，直接抛出异常
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String parseDeleteById(Class<?> mapperType) {
        throw new UnsupportedOperationException();
    }
} 
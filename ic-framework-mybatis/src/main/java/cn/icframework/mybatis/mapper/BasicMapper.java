package cn.icframework.mybatis.mapper;

import cn.icframework.common.consts.IPage;
import cn.icframework.mybatis.consts.IcParamsConsts;
import cn.icframework.mybatis.provider.SqlProvider;
import cn.icframework.mybatis.wrapper.SqlWrapper;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.icframework.mybatis.mapper.MapTransfer.getMapperTypeClass;
import static cn.icframework.mybatis.mapper.MapTransfer.handleResultMap;
import static cn.icframework.mybatis.wrapper.Wrapper.SELECT;

/**
 * 基础Mapper接口。
 * 提供通用的数据库操作方法。
 * <p>
 * 该接口定义了常用的CRUD操作方法，包括：
 * - 插入操作（单条、批量）
 * - 更新操作（根据ID、条件更新）
 * - 删除操作（根据ID、条件删除）
 * - 查询操作（根据ID、条件查询）
 * - 分页查询
 * </p>
 *
 * @param <T> 实体类型
 * @author hzl
 */
public interface BasicMapper<T> {

    /**
     * 批量插入实体数据。
     *
     * @param entityList 实体对象集合
     * @return 插入成功地记录数
     * @see SqlProvider#insertBatch(Map, ProviderContext)
     */
    @InsertProvider(type = SqlProvider.class, method = "insertBatch")
    int insertBatch(@Param(IcParamsConsts.PARAMETER_ENTITY) List<? extends T> entityList);

    /**
     * 批量插入实体数据。
     *
     * @param entityList 实体对象集合
     * @param size       批次大小
     * @return 插入成功地记录数
     * @see SqlProvider#insertBatch(Map, ProviderContext)
     */
    default int insertBatch(@Param(IcParamsConsts.PARAMETER_ENTITY) List<? extends T> entityList,
                            Integer size) {
        if (entityList == null || entityList.isEmpty()) {
            return 0;
        }
        if (size == null) {
            // 默认批次大小为1000 如果小于1000分两次就好
            size = entityList.size() > 1000 ? 1000 : entityList.size() / 2;
        }
        if (size <= 0) {
            return 0;
        }
        int total = 0;
        List<List<? extends T>> batches = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i += size) {
            batches.add(entityList.subList(i, Math.min(i + size, entityList.size())));
        }
        for (List<? extends T> batch : batches) {
            total += this.insertBatch(batch);
        }
        return total;
    }

    /**
     * 批量插入实体数据。
     *
     * @param entityList 实体对象集合
     * @param size       批次大小
     * @return 插入成功地记录数
     * @see SqlProvider#insertBatch(Map, ProviderContext)
     */
    default int insertBatchSkipError(@Param(IcParamsConsts.PARAMETER_ENTITY) List<? extends T> entityList,
                                     Integer size) {
        if (entityList == null || entityList.isEmpty()) {
            return 0;
        }
        if (size == null) {
            // 默认批次大小为1000 如果小于1000分两次就好
            size = entityList.size() > 1000 ? 1000 : entityList.size() / 2;
        }
        if (size <= 0) {
            return 0;
        }
        int total = 0;
        List<List<? extends T>> batches = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i += size) {
            batches.add(entityList.subList(i, Math.min(i + size, entityList.size())));
        }
        List<Thread> threads = new ArrayList<>();
        List<Integer> results = Collections.synchronizedList(new ArrayList<>());
        for (List<? extends T> batch : batches) {
            Thread t = Thread.ofVirtual().start(() -> {
                try {
                    int r = this.insertBatch(batch);
                    results.add(r);
                } catch (Throwable ignored) {
                }
            });
            threads.add(t);
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        for (int r : results) {
            total += r;
        }
        return total;
    }

    /**
     * 插入单个实体数据。
     *
     * @param entity 实体对象
     * @return 插入成功地记录数
     * @see SqlProvider#insert(Map, ProviderContext)
     */
    @InsertProvider(type = SqlProvider.class, method = "insert")
    int insert(@Param(IcParamsConsts.PARAMETER_ENTITY) T entity);

    /**
     * 通过条件构造器插入数据。
     *
     * @param sqlWrapper SQL 条件构造器
     * @return 插入成功地记录数
     * @see SqlProvider#insertBySqlWrapper(Map, ProviderContext)
     */
    @InsertProvider(type = SqlProvider.class, method = "insertBySqlWrapper")
    int insertBySqlWrapper(@Param(IcParamsConsts.PARAMETER_SW) SqlWrapper sqlWrapper);

    /**
     * 根据主键删除数据。
     *
     * @param id 主键
     * @return 删除成功地记录数
     * @see SqlProvider#deleteById(Map, ProviderContext)
     */
    @DeleteProvider(type = SqlProvider.class, method = "deleteById")
    int deleteById(@Param(IcParamsConsts.PARAMETER_PRIMARY_KEY) Serializable id);

    /**
     * 根据条件删除数据。
     *
     * @param sqlWrapper SQL 条件构造器
     * @return 删除成功地记录数
     * @see SqlProvider#delete(Map, ProviderContext)
     */
    @UpdateProvider(type = SqlProvider.class, method = "delete")
    int delete(@Param(IcParamsConsts.PARAMETER_SW) SqlWrapper sqlWrapper);

    /**
     * 根据主键集合批量删除数据。
     *
     * @param ids 主键集合
     * @return 删除成功地记录数
     * @see SqlProvider#deleteByIds(Map, ProviderContext)
     */
    @UpdateProvider(type = SqlProvider.class, method = "deleteByIds")
    int deleteByIds(@Param(IcParamsConsts.PARAMETER_PRIMARY_KEYS) Collection<? extends Serializable> ids);

    /**
     * 根据主键更新实体数据（不包含 null 字段）。
     *
     * @param entity 实体对象
     * @return 更新成功地记录数
     * @see SqlProvider#updateById(Map, ProviderContext)
     */
    @InsertProvider(type = SqlProvider.class, method = "updateById")
    int updateById(@Param(IcParamsConsts.PARAMETER_ENTITY) T entity);

    /**
     * 根据主键更新实体数据（包含 null 字段）。
     *
     * @param entity 实体对象
     * @return 更新成功地记录数
     * @see SqlProvider#updateByIdWithNull(Map, ProviderContext)
     */
    @InsertProvider(type = SqlProvider.class, method = "updateByIdWithNull")
    int updateByIdWithNull(@Param(IcParamsConsts.PARAMETER_ENTITY) T entity);

    /**
     * 根据条件更新实体数据（不包含 null 字段）。
     *
     * @param entity     实体对象
     * @param sqlWrapper SQL 条件构造器
     * @return 更新成功地记录数
     * @see SqlProvider#update(Map, ProviderContext)
     */
    @InsertProvider(type = SqlProvider.class, method = "update")
    int update(@Param(IcParamsConsts.PARAMETER_ENTITY) T entity,
               @Param(IcParamsConsts.PARAMETER_SW) SqlWrapper sqlWrapper);

    /**
     * 根据条件更新数据（不包含 null 字段，仅根据 Wrapper）。
     *
     * @param updateWrapper SQL 条件构造器
     * @return 更新成功地记录数
     * @see SqlProvider#updateByWrapper(Map, ProviderContext)
     */
    @InsertProvider(type = SqlProvider.class, method = "updateByWrapper")
    int updateWrapper(@Param(IcParamsConsts.PARAMETER_SW) SqlWrapper updateWrapper);

    /**
     * 根据条件更新实体数据（包含 null 字段）。
     *
     * @param entity     实体对象
     * @param sqlWrapper SQL 条件构造器
     * @return 更新成功地记录数
     * @see SqlProvider#updateWithNull(Map, ProviderContext)
     */
    @InsertProvider(type = SqlProvider.class, method = "updateWithNull")
    int updateWithNull(@Param(IcParamsConsts.PARAMETER_ENTITY) T entity,
                       @Param(IcParamsConsts.PARAMETER_SW) SqlWrapper sqlWrapper);
    // ----------------------------查询 start------------------------------------

    /**
     * 根据条件统计记录数。
     *
     * @param sqlWrapper SQL 条件构造器
     * @return 满足条件的记录数
     * @see SqlProvider#count(Map, ProviderContext)
     */
    @SelectProvider(type = SqlProvider.class, method = "count")
    long count(@Param(IcParamsConsts.PARAMETER_SW) SqlWrapper sqlWrapper);

    /**
     * 根据条件统计分页总数。
     *
     * @param sqlWrapper SQL 条件构造器
     * @return 满足条件的总记录数
     * @see SqlProvider#countPage(Map, ProviderContext)
     */
    @SelectProvider(type = SqlProvider.class, method = "countPage")
    long countPage(@Param(IcParamsConsts.PARAMETER_SW) SqlWrapper sqlWrapper);

    /**
     * 根据主键查询实体对象。
     *
     * @param id 主键
     * @return 实体对象
     */
    default T selectById(@Param(IcParamsConsts.PARAMETER_PRIMARY_KEY) Serializable id) {
        Class<T> table = getMapperTypeClass(this);
        Map<String, Object> map = this.selectMapById(id);
        return handleResultMap(table, this, map);
    }

    /**
     * 根据主键查询实体对象，并转换为指定类型。
     *
     * @param id     主键
     * @param asType 目标类型
     * @return 指定类型对象
     */
    default <R> R selectById(@Param(IcParamsConsts.PARAMETER_PRIMARY_KEY) Serializable id, Class<R> asType) {
        Map<String, Object> map = this.selectMapById(id);
        return handleResultMap(asType, this, map);
    }

    /**
     * 根据主键查询数据，返回 Map 结构。
     *
     * @param id 主键
     * @return 字段-值 Map
     * @see SqlProvider#getById(Map, ProviderContext)
     */
    @SelectProvider(type = SqlProvider.class, method = "getById")
    Map<String, Object> selectMapById(@Param(IcParamsConsts.PARAMETER_PRIMARY_KEY) Serializable id);

    /**
     * 根据条件查询单个实体对象。
     *
     * @param sqlWrapper SQL 条件构造器
     * @return 实体对象
     */
    default T selectOne(@Param(IcParamsConsts.PARAMETER_SW) SqlWrapper sqlWrapper) {
        Class<T> table = getMapperTypeClass(this);
        if (sqlWrapper == null) {
            sqlWrapper = SELECT().FROM_ENTITY(table);
        }
        Map<String, Object> map = this.selectOneMap(sqlWrapper);
        return handleResultMap(table, this, map);
    }

    /**
     * 根据条件查询单个对象并转换为指定类型。
     *
     * @param sqlWrapper SQL 条件构造器
     * @param type       目标类型
     * @param <R>        返回类型
     * @return 指定类型对象
     */
    default <R> R selectOneAs(SqlWrapper sqlWrapper, Class<R> type) {
        Map<String, Object> map = this.selectOneMap(sqlWrapper);
        return handleResultMap(type, this, map);
    }

    /**
     * 根据条件查询实体列表。
     *
     * @param sqlWrapper SQL 条件构造器
     * @return 实体对象列表
     */
    default List<T> select(@Param(IcParamsConsts.PARAMETER_SW) SqlWrapper sqlWrapper) {
        Class<T> table = getMapperTypeClass(this);
        return selectAs(sqlWrapper, table);
    }

    /**
     * 根据条件查询列表并转换为指定类型。
     *
     * @param sqlWrapper SQL 条件构造器
     * @param type       目标类型
     * @param <R>        返回类型
     * @return 指定类型对象列表
     */
    default <R> List<R> selectAs(SqlWrapper sqlWrapper, Class<R> type) {
        return selectAs(sqlWrapper, type, false);
    }

    /**
     * 根据条件查询列表并转换为指定类型，可选择是否跳过嵌套子查询。
     *
     * @param sqlWrapper   SQL 条件构造器
     * @param type         目标类型
     * @param skipRelation 是否跳过嵌套子查询
     * @param <R>          返回类型
     * @return 指定类型对象列表
     */
    default <R> List<R> selectAs(SqlWrapper sqlWrapper, Class<R> type, boolean skipRelation) {
        fitCountPage(sqlWrapper);
        List<Map<String, Object>> maps = this.selectMap(sqlWrapper);
        return handleResultMap(type, this, maps, skipRelation);
    }

    /**
     * 根据主键集合批量查询实体对象。
     *
     * @param ids 主键集合
     * @return 实体对象列表
     */
    default List<T> selectByIds(@Param(IcParamsConsts.PARAMETER_PRIMARY_KEYS) Collection<? extends Serializable> ids) {
        Class<T> table = getMapperTypeClass(this);
        List<Map<String, Object>> map = this.selectMapByIds(ids);
        return handleResultMap(table, this, map);
    }

    /**
     * 分页查询实体对象列表。
     *
     * @param sqlWrapper SQL 条件构造器
     * @param page       分页参数
     * @return 实体对象列表
     */
    default List<T> page(SqlWrapper sqlWrapper, IPage page) {
        Class<T> table = getMapperTypeClass(this);
        sqlWrapper.PAGE(page);
        return this.selectAs(sqlWrapper, table);
    }

    /**
     * 分页查询并转换为指定类型对象列表。
     *
     * @param sqlWrapper SQL 条件构造器
     * @param page       分页参数
     * @param type       目标类型
     * @param <R>        返回类型
     * @return 指定类型对象列表
     */
    default <R> List<R> pageAs(SqlWrapper sqlWrapper,
                               IPage page,
                               Class<R> type) {
        sqlWrapper.PAGE(page);
        return this.selectAs(sqlWrapper, type);
    }

    /**
     * 分页查询，返回 Map 结构列表。
     *
     * @param sw SQL 条件构造器
     * @return 字段-值 Map 列表
     * @see SqlProvider#selectMap(Map, ProviderContext)
     */
    @SelectProvider(type = SqlProvider.class, method = "selectMap")
    List<Map<String, Object>> selectMap(@Param(IcParamsConsts.PARAMETER_SW) SqlWrapper sw);

    /**
     * 根据主键集合批量查询，返回 Map 结构列表。
     *
     * @param ids 主键集合
     * @return 字段-值 Map 列表
     * @see SqlProvider#selectByIds(Map, ProviderContext)
     */
    @SelectProvider(type = SqlProvider.class, method = "selectByIds")
    List<Map<String, Object>> selectMapByIds(
            @Param(IcParamsConsts.PARAMETER_PRIMARY_KEYS) Collection<? extends Serializable> ids);

    /**
     * 查询单个，返回 Map 结构。
     *
     * @param sw SQL 条件构造器
     * @return 字段-值 Map
     * @see SqlProvider#selectOneMap(Map, ProviderContext)
     */
    @SelectProvider(type = SqlProvider.class, method = "selectOneMap")
    Map<String, Object> selectOneMap(@Param(IcParamsConsts.PARAMETER_SW) SqlWrapper sw);

    // ----------------------------查询 end------------------------------------

    private void fitCountPage(SqlWrapper sqlWrapper) {
        SqlWrapper.PageWrapper pageWrapper = sqlWrapper.getPageWrapper();
        if (pageWrapper != null) {
            long count = this.countPage(pageWrapper.getSqlWrapper());
            pageWrapper.getPage().setTotal(count);
            pageWrapper.getPage().setPages((long) Math.ceil(count * 1.0 / pageWrapper.getPage().getPageSize()));
        }
    }
}

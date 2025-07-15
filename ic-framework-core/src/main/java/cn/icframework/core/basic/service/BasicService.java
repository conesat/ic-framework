package cn.icframework.core.basic.service;

import cn.icframework.core.common.bean.PageRequest;
import cn.icframework.core.common.bean.PageResponse;
import cn.icframework.mybatis.consts.StatementType;
import cn.icframework.mybatis.mapper.BasicMapper;
import cn.icframework.mybatis.query.QueryTable;
import cn.icframework.mybatis.utils.ModelClassUtils;
import cn.icframework.mybatis.wrapper.SqlWrapper;
import cn.icframework.mybatis.wrapper.Wrapper;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static cn.icframework.mybatis.wrapper.FunctionWrapper.COUNT;


/**
 * 基础服务类
 * <p>
 * 提供通用的增删改查、分页、批量操作等服务层能力。
 *
 * @param <M> Mapper类型，需继承BasicMapper
 * @param <T> 实体类型
 * @author hzl
 * @since 2021-06-16  10:22:00
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public abstract class BasicService<M extends BasicMapper<T>, T> extends Wrapper {
    /**
     * 默认构造方法
     */
    public BasicService() {
    }

    @Autowired
    SqlSessionFactory sqlSessionFactory;
    @Autowired
    private M basicMapper;


    /**
     * 根据实体类型获取对应Dao
     *
     * @return Mapper对象
     */
    private BasicMapper<T> getBasicMapper() {
        return basicMapper;
    }

    /**
     * 前置操作，删除/插入/更新前触发
     *
     * @param sqlCommandType 操作类型
     * @param entity         操作前实体
     */
    public void before(SqlCommandType sqlCommandType, T entity) {
    }

    /**
     * 后置操作，删除/插入/更新后触发
     *
     * @param sqlCommandType 操作类型
     * @param entity         操作后实体
     */
    public void after(SqlCommandType sqlCommandType, T entity) {
    }


    /**
     * 统计数量
     *
     * @param sqlWrapper sql条件
     * @return 数量
     */
    public long count(@NotNull SqlWrapper sqlWrapper) {
        return getBasicMapper().count(sqlWrapper);
    }

    /**
     * 统计数量
     *
     * @param queryTable 查询表
     * @return 数量
     */
    public long count(@NotNull QueryTable<?> queryTable) {
        return getBasicMapper().count(SELECT(COUNT()).FROM(queryTable).WHERE(queryTable));
    }

    /**
     * 统计当前实体全部数量不带条件
     *
     * @return 全部数量
     */
    public long count() {
        return getBasicMapper().count(SELECT(COUNT()).FROM_ENTITY(getEntityClass()));
    }

    /**
     * 按条件查询第一个实体
     *
     * @param sqlWrapper 查询条件
     * @return 实体
     */
    public T selectOne(@NonNull SqlWrapper sqlWrapper) {
        return getBasicMapper().selectOne(sqlWrapper);
    }

    /**
     * 按条件查询第一个实体
     *
     * @param queryTable 查询表
     * @return 实体
     */
    public T selectOne(@NonNull QueryTable<?> queryTable) {
        return getBasicMapper().selectOne(SELECT().FROM(queryTable).WHERE(queryTable));
    }

    /**
     * 获取第一个实体 不带条件
     *
     * @return 实体
     */
    public T selectOne() {
        return getBasicMapper().selectOne(null);
    }

    /**
     * 按指定类型返回
     *
     * @param <R>        返回类型
     * @param sqlWrapper 条件
     * @param asType     返回类型Class
     * @return 对应类型
     */
    public <R> R selectOne(@NonNull SqlWrapper sqlWrapper, @NotNull Class<R> asType) {
        return getBasicMapper().selectOneAs(sqlWrapper, asType);
    }

    /**
     * 按指定类型返回
     *
     * @param <R>        返回类型
     * @param queryTable 查询表
     * @param asType     返回类型Class
     * @return 对应类型
     */
    public <R> R selectOne(@NonNull QueryTable<?> queryTable, @NotNull Class<R> asType) {
        return getBasicMapper().selectOneAs(SELECT().FROM(queryTable).WHERE(queryTable), asType);
    }

    /**
     * 根据id查询一个实体
     *
     * @param id id
     * @return 实体
     */
    public T selectById(@NotNull Serializable id) {
        if (id == null) {
            return null;
        }
        return getBasicMapper().selectById(id);
    }


    /**
     * 根据id查询一个实体并指定返回类型
     *
     * @param <R>    返回类型
     * @param id     id
     * @param asType 返回类型Class
     * @return 实体
     */
    public <R> R selectById(@NotNull Serializable id, @NotNull Class<R> asType) {
        if (id == null) {
            return null;
        }
        return getBasicMapper().selectById(id, asType);
    }

    /**
     * 根据条件查询实体列表
     *
     * @param sqlWrapper 条件
     * @return 实体列表
     */
    public List<T> select(@NotNull SqlWrapper sqlWrapper) {
        return getBasicMapper().select(sqlWrapper);
    }

    /**
     * 根据条件查询实体列表
     *
     * @param queryTable 查询表
     * @return 实体列表
     */
    public List<T> select(@NotNull QueryTable<?> queryTable) {
        return getBasicMapper().select(SELECT().FROM(queryTable).WHERE(queryTable));
    }

    /**
     * 查询所有实体
     *
     * @return 实体列表
     */
    public List<T> selectAll() {
        Class<?> entity = getEntityClass();
        return getBasicMapper().select(SELECT().FROM_ENTITY(entity));
    }


    /**
     * 返回指定类型列表
     *
     * @param <R>        指定类型
     * @param sqlWrapper 条件
     * @param asType     指定类型Class
     * @return 指定类型列表
     */
    public <R> List<R> select(@NotNull SqlWrapper sqlWrapper, @NotNull Class<R> asType) {
        return getBasicMapper().selectAs(sqlWrapper, asType);
    }

    /**
     * 返回指定类型列表
     *
     * @param <R>        指定类型
     * @param queryTable 查询表
     * @param asType     指定类型Class
     * @return 指定类型列表
     */
    public <R> List<R> select(@NotNull QueryTable<?> queryTable, @NotNull Class<R> asType) {
        return getBasicMapper().selectAs(SELECT().FROM(queryTable).WHERE(queryTable), asType);
    }

    /**
     * 查询所有实体并指定类型
     *
     * @param <R>    指定类型
     * @param asType 指定类型Class
     * @return 指定类型列表
     */
    public <R> List<R> selectAll(@NotNull Class<R> asType) {
        return getBasicMapper().selectAs(SELECT().FROM_ENTITY(getEntityClass()), asType);
    }

    /**
     * 根据id获取列表
     *
     * @param ids 查询id数组
     * @return 实体列表
     */
    public List<T> selectByIds(@NotNull Collection<? extends Serializable> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>(0);
        }
        return getBasicMapper().selectByIds(ids);
    }

    /**
     * 分页查询
     *
     * @param pageRequest 分页信息
     * @param sqlWrapper  条件
     * @return 分页实体列表
     */
    public PageResponse<T> page(@NotNull SqlWrapper sqlWrapper, @NotNull PageRequest pageRequest) {
        List<T> paginate = getBasicMapper().page(sqlWrapper, pageRequest);
        return pageRequest.toResponse(paginate);
    }

    /**
     * 分页查询
     *
     * @param queryTable  查询表
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    public PageResponse<T> page(@NotNull QueryTable<?> queryTable, @NotNull PageRequest pageRequest) {
        List<T> paginate = getBasicMapper().page(SELECT().FROM_ENTITY(getEntityClass()).WHERE(queryTable), pageRequest);
        return pageRequest.toResponse(paginate);
    }

    /**
     * 分页不带任何条件
     *
     * @param pageRequest 分页信息
     * @return 分页实体列表
     */
    public PageResponse<T> page(@NotNull PageRequest pageRequest) {
        List<T> paginate = getBasicMapper().page(SELECT().FROM_ENTITY(getEntityClass()), pageRequest);
        return pageRequest.toResponse(paginate);
    }

    /**
     * 分页查询并指定返回类型
     *
     * @param <R>         返回类型
     * @param sqlWrapper  条件
     * @param pageRequest 分页参数
     * @param asType      返回类型Class
     * @return 分页结果
     */
    public <R> PageResponse<R> page(@NotNull SqlWrapper sqlWrapper, @NotNull PageRequest pageRequest, @NotNull Class<R> asType) {
        List<R> paginate = getBasicMapper().pageAs(sqlWrapper, pageRequest, asType);
        return pageRequest.toResponse(paginate);
    }

    /**
     * 分页查询并指定返回类型
     *
     * @param <R>         返回类型
     * @param queryTable  查询表
     * @param pageRequest 分页参数
     * @param asType      返回类型Class
     * @return 分页结果
     */
    public <R> PageResponse<R> page(@NotNull QueryTable<?> queryTable, @NotNull PageRequest pageRequest, @NotNull Class<R> asType) {
        List<R> paginate = getBasicMapper().pageAs(SELECT().FROM().WHERE(queryTable), pageRequest, asType);
        return pageRequest.toResponse(paginate);
    }

    /**
     * 插入一个实体
     *
     * @param entity 实体
     * @return 插入成功数量
     */
    public int insert(@NotNull T entity) {
        handleBefore(SqlCommandType.INSERT, entity);
        int b = getBasicMapper().insert(entity);
        handleAfter(SqlCommandType.INSERT, entity);
        return b;
    }

    /**
     * 插入数据
     *
     * @param sqlWrapper SQL包装器
     * @return 插入数量
     */
    public int insert(@NotNull SqlWrapper sqlWrapper) {
        return getBasicMapper().insertBySqlWrapper(sqlWrapper);
    }


    /**
     * 分批 批量插入
     * 跳过before与after
     * 默认分2批-多线程分批，分的批次多速度就会快
     *
     * @param entityList 实体列表
     * @return 插入成功数量
     */
    public int insertBatch(@NotNull List<T> entityList) {
        return insertBatch(entityList, null);
    }

    /**
     * 分批 批量插入
     * 跳过before与after
     * 默认分2批-多线程分批，分的批次多速度就会快
     *
     * @param entityList 实体列表
     * @param skipError 是否忽略异常
     * @return 插入成功数量
     */
    public int insertBatch(@NotNull List<T> entityList, boolean skipError) {
        return insertBatch(entityList, null, skipError, true, true);
    }

    /**
     * 分批 批量插入
     * 跳过before与after
     *
     * @param entityList 实体列表
     * @param size       分批大小
     * @return 插入成功数量
     */
    public int insertBatch(@NotNull List<T> entityList, Integer size) {
        return insertBatch(entityList, size, true, true);
    }

    /**
     * 批量插入
     *
     * @param entityList 实体列表
     * @param size       批量大小
     * @param skipBefore 是否跳过前置处理
     * @param skipAfter  是否跳过后置处理
     * @return 插入数量
     */
    public int insertBatch(@NotNull List<T> entityList, Integer size, boolean skipBefore, boolean skipAfter) {
        return insertBatch(entityList, size, false, skipBefore, skipAfter);
    }

    /**
     * 批量插入
     * 默认分2批-多线程分批，分的批次多速度就会快
     *
     * @param entityList 实体列表
     * @param skipBefore 是否跳过前置处理（最好跳过。否则会遍历每个实体进行处理）
     * @param skipAfter  是否跳过后置处理（最好跳过。否则会遍历每个实体进行处理）
     * @return 插入成功数量
     */
    public int insertBatch(@NotNull List<T> entityList, boolean skipBefore, boolean skipAfter) {
        return insertBatch(entityList, null, false, skipBefore, skipAfter);
    }

    /**
     * 批量插入
     *
     * @param entityList 实体列表
     * @param size       批量大小
     * @param skipError  是否跳过异常
     * @param skipBefore 是否跳过前置处理
     * @param skipAfter  是否跳过后置处理
     * @return 插入数量
     */
    public int insertBatch(@NotNull List<T> entityList, Integer size, boolean skipError, boolean skipBefore, boolean skipAfter) {
        if (CollectionUtils.isEmpty(entityList)) {
            return 0;
        }
        if (!skipBefore && isNeedBefore(null)) {
            handleBefore(SqlCommandType.INSERT, entityList);
        }
        int b;
        if (skipError) {
            b = getBasicMapper().insertBatchSkipError(entityList, size);
        } else {
            b = getBasicMapper().insertBatch(entityList, size);
        }
        if (!skipAfter && isNeedAfter(null)) {
            handleAfter(SqlCommandType.INSERT, entityList);
        }
        return b;
    }

    /**
     * 根据id删除实体
     *
     * @param id id
     * @return 成功数量
     */
    public int deleteById(@NotNull Serializable id) {
        T t = getBasicMapper().selectById(id);
        handleBefore(SqlCommandType.DELETE, t);
        int b = getBasicMapper().deleteById(id);
        handleAfter(SqlCommandType.DELETE, t);
        return b;
    }

    /**
     * 根据条件删除实体
     *
     * @param sqlWrapper 条件
     * @param skipBefore 是否跳过前置处理（最好跳过。否则会遍历每个实体进行处理）
     * @param skipAfter  是否跳过后置处理（最好跳过。否则会遍历每个实体进行处理）
     * @return 删除成功数量
     */
    public int delete(@NotNull SqlWrapper sqlWrapper, boolean skipBefore, boolean skipAfter) {
        List<T> ts = null;
        boolean needBefore = isNeedBefore(null);
        boolean needAfter = isNeedAfter(null);
        if ((!skipBefore && needBefore) || (!skipAfter && needAfter)) {
            ts = getBasicMapper().select(sqlWrapper);
        }
        if (ts != null && needBefore) {
            handleBefore(SqlCommandType.DELETE, ts);
        }
        int remove = getBasicMapper().delete(sqlWrapper);
        if (ts != null && needAfter) {
            handleAfter(SqlCommandType.DELETE, ts);
        }
        return remove;
    }

    /**
     * 删除数据
     *
     * @param queryTable 查询表
     * @param skipBefore 是否跳过前置处理
     * @param skipAfter  是否跳过后置处理
     * @return 删除数量
     */
    public int delete(@NotNull QueryTable<?> queryTable, boolean skipBefore, boolean skipAfter) {
        return delete(DELETE().FROM(queryTable).WHERE(queryTable), skipBefore, skipAfter);
    }

    /**
     * 根据条件删除，不跳过前后置处理
     *
     * @param sqlWrapper 条件
     * @return 成功数量
     */
    public int delete(@NotNull SqlWrapper sqlWrapper) {
        return delete(sqlWrapper, false, false);
    }

    /**
     * 删除数据
     *
     * @param queryTable 查询表
     * @return 删除数量
     */
    public int delete(@NotNull QueryTable<?> queryTable) {
        return delete(queryTable, false, false);
    }

    /**
     * 根据id列表删除实体 不跳过前后置处理
     *
     * @param idList id列表
     * @return 删除成功数量
     */
    public int deleteByIds(@NotNull Collection<? extends Serializable> idList) {
        return this.deleteByIds(idList, false, false);
    }


    /**
     * 根据id列表删除实体
     *
     * @param idList     id列表
     * @param skipBefore 是否跳过前置处理（最好跳过。否则会遍历每个实体进行处理）
     * @param skipAfter  是否跳过后置处理（最好跳过。否则会遍历每个实体进行处理）
     * @return 删除成功数量
     */
    public int deleteByIds(@NotNull Collection<? extends Serializable> idList, boolean skipBefore, boolean skipAfter) {
        List<T> ts = null;
        boolean needBefore = isNeedBefore(null);
        boolean needAfter = isNeedAfter(null);
        if ((!skipBefore && needBefore) || (!skipAfter && needAfter)) {
            ts = getBasicMapper().selectByIds(idList);
        }
        if (ts != null && needBefore) {
            for (T t : ts) {
                before(SqlCommandType.DELETE, t);
            }
        }
        int remove = getBasicMapper().deleteByIds(idList);
        if (ts != null && needAfter) {
            for (T t : ts) {
                after(SqlCommandType.DELETE, t);
            }
        }
        return remove;
    }

    /**
     * 根据条件更新实体
     * 不会执行before after
     *
     * @param updateWrapper 更新wrapper
     * @return 更新成功数量
     */
    public int update(@NotNull SqlWrapper updateWrapper) {
        return getBasicMapper().updateWrapper(updateWrapper);
    }

    /**
     * 根据条件更新实体
     * 不会执行before after
     *
     * @param queryTable 更新wrapper
     * @return 更新成功数量
     */
    public int update(@NotNull QueryTable<?> queryTable) {
        SqlWrapper updateWrapper = UPDATE(queryTable).SET(queryTable).WHERE(queryTable);
        return getBasicMapper().updateWrapper(updateWrapper);
    }


    /**
     * 更新实体
     *
     * @param entity        实体
     * @param updateWrapper 更新条件
     * @return 更新数量
     */
    public int update(@NotNull T entity, @NotNull SqlWrapper updateWrapper) {
        return update(entity, updateWrapper, false, false);
    }

    /**
     * 更新实体
     *
     * @param entity        实体
     * @param updateWrapper 更新条件
     * @param skipBefore    是否跳过前置处理
     * @param skipAfter     是否跳过后置处理
     * @return 更新数量
     */
    public int update(@NotNull T entity, @NotNull SqlWrapper updateWrapper, boolean skipBefore, boolean skipAfter) {
        List<T> ts = null;
        boolean needBefore = isNeedBefore(null);
        boolean needAfter = isNeedAfter(null);
        if ((!skipBefore && needBefore) || (!skipAfter && needAfter)) {
            updateWrapper.setStatementType(StatementType.SELECT);
            ts = getBasicMapper().select(updateWrapper);
        }
        if (ts != null && needBefore) {
            for (T t : ts) {
                before(SqlCommandType.UPDATE, t);
            }
        }
        int update = getBasicMapper().update(entity, updateWrapper);
        if (ts != null && needAfter) {
            for (T t : ts) {
                after(SqlCommandType.UPDATE, t);
            }
        }
        return update;
    }

    /**
     * 根据id更新实体 不更新null字段
     *
     * @param entity 实体
     * @return 更新成功数量
     */
    public int updateById(@NotNull T entity) {
        return updateById(entity, false);
    }

    /**
     * 根据id更新实体
     *
     * @param entity   实体
     * @param withNull 是否更新null字段
     * @return 成功数量
     */
    public int updateById(@NotNull T entity, boolean withNull) {
        return updateById(entity, withNull, false, false);
    }


    /**
     * 根据id更新实体
     *
     * @param entity     实体
     * @param withNull   是否更新null字段
     * @param skipBefore 是否跳过前置处理
     * @param skipAfter  是否跳过后置处理
     * @return 成功数量
     */
    public int updateById(@NotNull T entity, boolean withNull, boolean skipBefore, boolean skipAfter) {
        if (!skipBefore) {
            handleBefore(SqlCommandType.UPDATE, entity);
        }
        int b;
        if (withNull) {
            b = getBasicMapper().updateByIdWithNull(entity);
        } else {
            b = getBasicMapper().updateById(entity);
        }
        if (!skipAfter) {
            handleAfter(SqlCommandType.UPDATE, entity);
        }
        return b;
    }

    /**
     * 根据id批量更新实体
     * 不更新null字段
     *
     * @param entityList 实体列表
     * @param size       批量大小
     * @return 是否成功
     */
    public boolean updateBatchById(@NotNull List<T> entityList, int size) {
        return updateBatchById(entityList, size, false);
    }

    /**
     * 批量更新
     *
     * @param entityList 实体列表
     * @param size       批量大小
     * @param withNull   是否包含null字段
     * @return 是否成功
     */
    public boolean updateBatchById(@NotNull List<T> entityList, int size, boolean withNull) {
        if (CollectionUtils.isEmpty(entityList)) {
            return true;
        }
        if (size <= 0) {
            size = 1000;
        }
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
            for (int index = 0; index < entityList.size(); index++) {
                updateById(entityList.get(index), withNull);
                if (index != 0 && index % size == 0) {
                    sqlSession.commit();
                }
            }
            sqlSession.commit();
            return true;
        }
    }


    /**
     * 插入或者更新，不会更新null字段
     *
     * @param entity 实体
     * @return 成功数量
     */
    public int insertOrUpdate(@NotNull T entity) {
        return insertOrUpdate(entity, false);
    }

    /**
     * 插入或者更新
     *
     * @param entity   实体
     * @param withNull 是否更新null字段
     * @return 成功数量
     */
    public int insertOrUpdate(@NotNull T entity, boolean withNull) {
        Object idValue = ModelClassUtils.getIdValue(entity);
        int b = 0;
        if (idValue != null) {
            handleBefore(SqlCommandType.UPDATE, entity);
            if (withNull) {
                b = getBasicMapper().updateByIdWithNull(entity);
            } else {
                b = getBasicMapper().updateById(entity);
            }
            handleAfter(SqlCommandType.UPDATE, entity);
        } else {
            handleBefore(SqlCommandType.INSERT, entity);
            b = getBasicMapper().insert(entity);
            handleAfter(SqlCommandType.INSERT, entity);
        }
        return b;
    }


//    ----------------------------------------------------------------


    /**
     * 内置前置处理 批量
     *
     * @param sqlCommandType
     * @param entityList
     */
    private void handleBefore(SqlCommandType sqlCommandType, List<T> entityList) {
        if (entityList.isEmpty() || !isNeedBefore(entityList.get(0))) {
            return;
        }
        for (T t : entityList) {
            before(sqlCommandType, t);
        }
    }

    /**
     * 内置前置处理 单个
     *
     * @param sqlCommandType
     * @param t
     */
    private void handleBefore(SqlCommandType sqlCommandType, T t) {
        if (isNeedBefore(t)) {
            before(sqlCommandType, t);
        }
    }

    /**
     * 内置前置处理 批量
     *
     * @param sqlCommandType
     * @param entityList
     */
    private void handleAfter(SqlCommandType sqlCommandType, List<T> entityList) {
        if (entityList.isEmpty() || !isNeedAfter(entityList.get(0))) {
            return;
        }
        for (T t : entityList) {
            after(sqlCommandType, t);
        }
    }

    /**
     * 内置后置处理 单个
     *
     * @param sqlCommandType
     * @param t
     */
    private void handleAfter(SqlCommandType sqlCommandType, T t) {
        if (isNeedAfter(t)) {
            after(sqlCommandType, t);
        }
    }

    private boolean isNeedBefore(T entity) {
        Class<?> c;
        if (entity == null) {
            c = getEntityClass();
        } else {
            c = entity.getClass();
        }
        Class<?> currentClass = getClass(); // 获取当前类的Class对象
        try {
            currentClass.getMethod("before", SqlCommandType.class, c);
            return true;
        } catch (NoSuchMethodException ignored) {

        }
        return false;
    }

    private boolean isNeedAfter(T entity) {
        Class<?> c;
        if (entity == null) {
            c = getEntityClass();
        } else {
            c = entity.getClass();
        }
        Class<?> currentClass = getClass(); // 获取当前类的Class对象
        try {
            currentClass.getMethod("after", SqlCommandType.class, c);
            return true;
        } catch (NoSuchMethodException ignored) {

        }
        return false;
    }


    private Class<?> getEntityClass() {
        return (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }
}

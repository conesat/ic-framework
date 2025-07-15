package cn.icframework.mybatis.mapper;

import cn.icframework.mybatis.annotation.Association;
import cn.icframework.mybatis.annotation.Collection;
import cn.icframework.mybatis.annotation.Join;
import cn.icframework.mybatis.annotation.Joins;
import cn.icframework.mybatis.annotation.Table;
import cn.icframework.common.utils.cache.CacheUtil;
import cn.icframework.common.utils.cache.LRUCache;
import cn.icframework.mybatis.helper.RelationHelper;
import cn.icframework.mybatis.query.QueryField;
import cn.icframework.mybatis.query.QueryTable;
import cn.icframework.mybatis.utils.FieldUtils;
import cn.icframework.mybatis.utils.ModelClassUtils;
import cn.icframework.mybatis.utils.ReflectUtils;
import cn.icframework.mybatis.utils.TypeConvertUtils;
import cn.icframework.mybatis.wrapper.FromWrapper;
import cn.icframework.mybatis.wrapper.SqlWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cn.icframework.mybatis.utils.ModelClassUtils.isDefaultType;
import static cn.icframework.mybatis.wrapper.Wrapper.SELECT;

/**
 * Map转换工具类。
 * 提供实体对象与Map之间的转换功能。
 *
 * @author hzl
 */
@Slf4j
public class MapTransfer {
    // 缓存最多一百个对象的属性到Map的映射关系，提升对象与Map互转的性能
    static LRUCache<Class<?>, Map<String, Method>> lruMapTransferCache = CacheUtil.newLRUCache(100);
    // set/get方法前缀长度
    private static final int METHOD_PREFIX_LEN = 3;
    // 关联字段前缀
    private static final String RELATION_FIELD_PREFIX = "ic_relation_field_";

    // Java 21+ 虚拟线程池，适合高并发异步任务
    private static final ExecutorService RELATION_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * 获取Mapper的实体类型Class
     *
     * @param basicMapper Mapper对象
     * @return 实体类型Class
     */
    @SuppressWarnings("unchecked")
    static <T> Class<T> getMapperTypeClass(BasicMapper<T> basicMapper) {
        return (Class<T>) ((ParameterizedType) (basicMapper.getClass().getInterfaces()[0]).getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }


    static <R, T> R handleResultMap(Class<R> rType, BasicMapper<T> basicMapper, Map<String, Object> map) {
        List<R> rs = handleResultMap(rType, basicMapper, Collections.singletonList(map));
        if (rs.isEmpty()) {
            return null;
        }
        return rs.getFirst();
    }

    /**
     * 将单个Map转换为指定类型对象
     *
     * @param rType       返回类型
     * @param basicMapper Mapper对象
     * @param maps        查询结果Map
     * @return 转换后的对象
     */
    public static <R, T> List<R> handleResultMap(Class<R> rType, BasicMapper<T> basicMapper, List<Map<String, Object>> maps) {
        return handleResultMap(rType, basicMapper, maps, false);
    }

    /**
     * 注解解析上下文，封装所有注解相关信息
     */
    private static class AnnotationContext {
        Map<String, RelationResult> collectionGroupIdMap;
        Map<String, RelationResult> associationMap;
        Map<String, RelationGroup> relationTableMap;
        Field[] fields;
    }

    /**
     * 主流程重构：注解解析、数据映射、关联处理分离
     */
    public static <R, T> List<R> handleResultMap(Class<R> rType, BasicMapper<T> basicMapper, List<Map<String, Object>> maps, boolean skipRelation) {
        // 1. 解析注解
        AnnotationContext context = parseFieldAnnotations(rType, skipRelation);
        // 2. 分批并发处理
        int batchSize = 100;
        List<CompletableFuture<List<R>>> futures = new ArrayList<>();
        for (int i = 0; i < maps.size(); i += batchSize) {
            int to = Math.min(i + batchSize, maps.size());
            List<Map<String, Object>> subList = maps.subList(i, to);
            CompletableFuture<List<R>> future = CompletableFuture.supplyAsync(() -> {
                List<R> batchResult = new ArrayList<>();
                if (context.collectionGroupIdMap == null) {
                    for (Map<String, Object> map : subList) {
                        R r = mapToEntity(rType, basicMapper, map, context);
                        batchResult.add(r);
                    }
                } else {
                    Map<Object, Map<String, List<Object>>> collectionGroupMap = new HashMap<>();
                    for (Map<String, Object> map : subList) {
                        R r = mapToCollectionEntity(rType, basicMapper, map, context, collectionGroupMap);
                        if (r != null) {
                            batchResult.add(r);
                        }
                    }
                }
                return batchResult;
            }, RELATION_EXECUTOR);
            futures.add(future);
        }
        // 3. 汇总所有批次结果
        List<R> rs = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(java.util.stream.Collectors.toList());
        // 4. 关联关系批量处理
        handleRelation(basicMapper, context.relationTableMap, rs);
        return rs;
    }

    /**
     * 普通字段和关联对象的映射
     */
    private static <R, T> R mapToEntity(Class<R> rType, BasicMapper<T> basicMapper, Map<String, Object> map, AnnotationContext context) {
        return handleResultMap(rType, basicMapper, map, context.associationMap, context.relationTableMap);
    }

    /**
     * 集合注解的映射
     */
    private static <R, T> R mapToCollectionEntity(Class<R> rType, BasicMapper<T> basicMapper, Map<String, Object> map, AnnotationContext context, Map<Object, Map<String, List<Object>>> collectionGroupMap) {
        return handleResultMap(rType, basicMapper, map, context.collectionGroupIdMap, collectionGroupMap, context.relationTableMap);
    }

    /**
     * 字段注解解析，返回注解上下文
     */
    private static <R> AnnotationContext parseFieldAnnotations(Class<R> rType, boolean skipRelation) {
        AnnotationContext context = new AnnotationContext();
        Field[] fields = rType.getDeclaredFields();
        context.fields = fields;
        context.collectionGroupIdMap = new HashMap<>();
        context.associationMap = new HashMap<>();
        context.relationTableMap = new HashMap<>();
        for (Field field : fields) {
            if (parseCollectionAnnotation(rType, field, context)) continue;
            if (parseAssociationAnnotation(field, context)) continue;
            if (RelationHelper.getRelationQuery() && !skipRelation) {
                parseJoinAnnotations(rType, field, context);
            }
        }
        // 兼容旧逻辑：如无内容则置为null
        if (context.collectionGroupIdMap.isEmpty()) context.collectionGroupIdMap = null;
        if (context.associationMap.isEmpty()) context.associationMap = null;
        if (context.relationTableMap.isEmpty()) context.relationTableMap = null;
        return context;
    }

    /**
     * 解析Collection注解，返回是否命中
     */
    private static <R> boolean parseCollectionAnnotation(Class<R> rType, Field field, AnnotationContext context) {
        Collection collection = field.getDeclaredAnnotation(Collection.class);
        if (collection == null) return false;
        boolean isList = List.class.isAssignableFrom(field.getType());
        Assert.isTrue(isList, rType.getName() + "::" + field.getName() + " 解析异常：Collection注解只适用List");
        boolean autoFit = !StringUtils.hasLength(collection.prefix());
        String prefix = autoFit ? field.getName() : collection.prefix();
        Class<?> fieldType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        context.collectionGroupIdMap.put(prefix, new RelationResult(
                collection.groupMainId(), field.getName(), fieldType, autoFit, collection.column()));
        return true;
    }

    /**
     * 解析Association注解，返回是否命中
     */
    private static boolean parseAssociationAnnotation(Field field, AnnotationContext context) {
        Association association = field.getDeclaredAnnotation(Association.class);
        if (association == null) return false;
        String prefix = StringUtils.hasLength(association.prefix()) ? association.prefix() : field.getName();
        context.associationMap.put(prefix, new RelationResult(null, field.getName(), field.getType(), false, null));
        return true;
    }

    /**
     * 解析Join/Joins注解
     */
    private static <R> void parseJoinAnnotations(Class<R> rType, Field field, AnnotationContext context) {
        boolean skipChildRelation = false;
        List<Join> joinList = new ArrayList<>();
        Joins joins = field.getDeclaredAnnotation(Joins.class);
        if (joins != null) {
            joinList.addAll(List.of(joins.joins()));
            skipChildRelation = joins.skipRelation();
        }
        Join joinAnnotation = field.getDeclaredAnnotation(Join.class);
        if (joinAnnotation != null) {
            if (joins == null) {
                skipChildRelation = joinAnnotation.skipRelation();
            }
            joinList.add(joinAnnotation);
        }
        if (joinList.isEmpty()) return;
        List<RelationJoinInfo> relationJoinInfos = new ArrayList<>();
        Class<?> fieldType = field.getType();
        if (List.class.isAssignableFrom(fieldType)) {
            fieldType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        }
        for (int i = 0; i < joinList.size(); i++) {
            Join join = joinList.get(i);
            Assert.isTrue(StringUtils.hasLength(join.selfField()) && StringUtils.hasLength(join.joinTableField()),
                    rType.getName() + field.getName() + "映射字段不能为空");
            RelationJoinInfo relationJoinInfo = new RelationJoinInfo();
            if (join.selfTable() != Void.class) {
                relationJoinInfo.setSelfTable(join.selfTable());
            } else if (i == 0) {
                relationJoinInfo.setSelfTable(rType);
            } else {
                relationJoinInfo.setSelfTable(relationJoinInfos.getLast().getJoinTable());
            }
            if (join.joinTable() == Void.class) {
                if (i == joinList.size() - 1) {
                    Assert.isTrue(fieldType.isAnnotationPresent(Table.class),
                            rType.getName() + ":" + field.getName() + " 非实体对象，最后一个Join的joinTable参数必填");
                    relationJoinInfo.setJoinTable(fieldType);
                } else {
                    throw new RuntimeException(rType.getName() + ":" + field.getName() + " Join的joinTable参数不能为空");
                }
            } else {
                relationJoinInfo.setJoinTable(join.joinTable());
            }
            relationJoinInfo.setSelfField(join.selfField());
            relationJoinInfo.setJoinTableField(join.joinTableField());
            relationJoinInfo.setWhere(join.where());
            relationJoinInfo.setSelect(join.select());
            relationJoinInfo.setOrderBy(join.orderBy());
            relationJoinInfos.add(relationJoinInfo);
        }
        context.relationTableMap = fitRelationJoin(rType, field, context.relationTableMap, relationJoinInfos, skipChildRelation);
    }

    /**
     * 处理嵌套查询
     * 假设查询得到user数组 list= [{id:1,name:2},{id:2,name:3}]
     * 关联查询需要获取user的roles，和用户部门dep
     * 1. 获取user的id，userIdList = list.map(User::getId).collect(Collectors.toSet())
     * 2. 用userIdList查询userRole：
     * select r.*,ur.userId from user_role ur left join role r on r.id = ur.roleId where ur.userId in (userIdList)
     * 获取到roleList = [{id:1,name:"管理员",userId:1},{id:1,name:"普通用户",userId:2}]
     * 3. 用userIdList查询userDep：
     * select d.*,ud.userId from user_dep ud left join dep d on d.id = ud.depId where ud.userId in (userIdList)
     * 获取到depList = [{id:1,name:"技术部",userId:1},{id:2,name:"运营部",userId:2}]
     * 4. 将depList和roleList根据userId分组，得到map
     * Map<Long, List<Map<String,Object>>> roleGroupMap = roleList.stream().collect(Collectors.groupingBy(map->map.get("userId")));
     * Map<Long, List<Map<String,Object>>> depGroupMap = depList.stream().collect(Collectors.groupingBy(map->map.get("userId")));
     * 4. 遍历rs，给每个user对象设置roles和dep
     * for(R r:rs){
     * r.setRoles(roleGroupMap.get(r.userId));
     * r.setDeps(depGroupMap.get(r.userId));
     * }
     */
    private static <R, T> void handleRelation(BasicMapper<T> basicMapper, Map<String, RelationGroup> relationTableMap, List<R> rs) {
        if (relationTableMap == null) return;
        // 1. 异步批量查询
        doRelationAsyncQuery(basicMapper, relationTableMap);
        // 2. 结果分发赋值
        assignRelationResultToEntity(rs, relationTableMap);
    }

    /**
     * 异步批量查询所有关联关系
     */
    private static <T> void doRelationAsyncQuery(BasicMapper<T> basicMapper, Map<String, RelationGroup> relationTableMap) {
        if (relationTableMap == null || relationTableMap.isEmpty()) return;

        for (RelationGroup relationGroup : relationTableMap.values()) {
            if (relationGroup.getColumnDataList().isEmpty()) continue;

            // 构建所有异步任务
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (Relation relation : relationGroup.getRelationList()) {
                futures.add(buildRelationQueryTask(basicMapper, relationGroup, relation));
            }

            // 等待所有任务完成
            try {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            } catch (Exception e) {
                log.error("关联查询批量处理异常: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 构建单个关联查询的异步任务
     */
    private static <T> CompletableFuture<Void> buildRelationQueryTask(BasicMapper<T> basicMapper, RelationGroup relationGroup, Relation relation) {
        return CompletableFuture.runAsync(() -> {
            try {
                RelationJoinInfo relationJoinInfoFirst = relation.relationJoinInfos.getFirst();
                Class<?> joinTable = relationJoinInfoFirst.getJoinTable();
                Table table = joinTable.getAnnotation(Table.class);
                Field field = ModelClassUtils.getDeclaredField(joinTable, relationJoinInfoFirst.getJoinTableField());
                QueryTable<?> beforeQueryTable = new QueryTable<>(relationJoinInfoFirst.getJoinTable());
                QueryField<?> whereQueryField = new QueryField<>(new QueryTable<>(relationJoinInfoFirst.getJoinTable()),
                        ModelClassUtils.getTableColumnName(table, field));
                whereQueryField.as(RELATION_FIELD_PREFIX + relationJoinInfoFirst.selfField);
                RelationJoinInfo lastJoinInfo = relation.relationJoinInfos.getLast();
                Class<?> resultType = lastJoinInfo.getJoinTable();
                FromWrapper fromWrapper;
                if (lastJoinInfo.select.length > 0) {
                    Object[] selects = new Object[lastJoinInfo.select.length + 1];
                    System.arraycopy(lastJoinInfo.select, 0, selects, 0, lastJoinInfo.select.length);
                    selects[selects.length - 1] = whereQueryField;
                    fromWrapper = SELECT(selects).FROM(beforeQueryTable);
                } else {
                    fromWrapper = SELECT(resultType, whereQueryField).FROM(beforeQueryTable);
                }
                List<String> wheres = new ArrayList<>();
                List<String> orderBys = new ArrayList<>();
                if (org.springframework.util.StringUtils.hasLength(relationJoinInfoFirst.where)) {
                    wheres.add(relationJoinInfoFirst.where);
                }
                if (org.springframework.util.StringUtils.hasLength(relationJoinInfoFirst.orderBy)) {
                    orderBys.add(relationJoinInfoFirst.orderBy);
                }
                for (int i = 1; i < relation.relationJoinInfos.size(); i++) {
                    RelationJoinInfo relationJoinInfo = relation.relationJoinInfos.get(i);
                    Class<?> entityType = relationJoinInfo.getJoinTable();
                    Table jTable = entityType.getAnnotation(Table.class);
                    QueryTable<?> queryTable = new QueryTable<>(entityType);
                    Field joinField = ModelClassUtils.getDeclaredField(entityType, relationJoinInfo.getJoinTableField());
                    if (joinField == null) {
                        throw new RuntimeException(resultType.getName() + "Join注解有误" + entityType.getName() + "类不存在" + relationJoinInfo.getJoinTableField() + "字段，请填写java类字段");
                    }
                    QueryField<?> queryField = new QueryField<>(queryTable, ModelClassUtils.getTableColumnName(jTable, joinField));
                    Field selfField = ModelClassUtils.getDeclaredField(relationJoinInfo.getSelfTable(), relationJoinInfo.getSelfField());
                    Table sTable = relationJoinInfo.getSelfTable().getAnnotation(Table.class);
                    if (selfField == null) {
                        throw new RuntimeException(resultType.getName() + "Join注解有误" + relationJoinInfo.getSelfTable().getName() + "类不存在" + relationJoinInfo.getSelfField() + "字段，请填写java类字段");
                    }
                    QueryField<?> joinQueryField = new QueryField<>(beforeQueryTable, ModelClassUtils.getTableColumnName(sTable, selfField));
                    fromWrapper.LEFT_JOIN(queryTable).ON(queryField.eq(joinQueryField));
                    if (org.springframework.util.StringUtils.hasLength(relationJoinInfo.where)) {
                        wheres.add(relationJoinInfo.where);
                    }
                    if (org.springframework.util.StringUtils.hasLength(relationJoinInfo.orderBy)) {
                        orderBys.add(relationJoinInfo.orderBy);
                    }
                    beforeQueryTable = queryTable;
                }
                SqlWrapper sql = fromWrapper.WHERE(whereQueryField.in(relationGroup.getColumnDataList().toArray()));
                if (!wheres.isEmpty()) {
                    sql.WHERE(wheres.toArray(new String[0]));
                }
                if (!orderBys.isEmpty()) {
                    sql.ORDER_BY(orderBys.toArray(new String[0]));
                }
                List<Map<String, Object>> maps = basicMapper.selectMap(sql);
                if (maps != null) {
                    relation.dataMap = new HashMap<>();
                    Map<Object, List<Map<String, Object>>> listMap = maps.stream().collect(java.util.stream.Collectors.groupingBy(data ->
                            data.get(whereQueryField.getAsName())));
                    listMap.forEach((mapKey, value) -> relation.dataMap.put(mapKey, handleResultMap(relation.type, basicMapper, value, relation.isSkipRelation())));
                }
            } catch (Exception ex) {
                log.error("关联查询异步任务异常: {}", ex.getMessage(), ex);
                throw new RuntimeException(ex);
            }
        }, RELATION_EXECUTOR);
    }

    /**
     * 将查询结果分发赋值到实体对象
     */
    private static <R> void assignRelationResultToEntity(List<R> rs, Map<String, RelationGroup> relationTableMap) {
        if (rs == null || rs.isEmpty() || relationTableMap == null) return;

        // 预处理：合并所有Relation为一个列表，减少循环层级
        List<Relation> allRelations = new ArrayList<>();
        for (RelationGroup group : relationTableMap.values()) {
            allRelations.addAll(group.getRelationList());
        }

        for (R r : rs) {
            if (r == null) continue;
            for (Relation relation : allRelations) {
                if (relation.getDataMap() == null) continue;
                Method fieldSetMethod = relation.getFieldSetMethod();
                Method relationColumnFieldGetMethod = relation.getRelationColumnFieldGetMethod();
                try {
                    Object keyObj = relationColumnFieldGetMethod.invoke(r);
                    if (relation.isList) {
                        fieldSetMethod.invoke(r, relation.getDataMap().get(keyObj));
                    } else if (relation.getDataMap().get(keyObj) != null &&
                            !relation.getDataMap().get(keyObj).isEmpty()) {
                        fieldSetMethod.invoke(r, relation.getDataMap().get(keyObj).getFirst());
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 构建并记录字段与关联关系的映射（Relation/RelationGroup）
     *
     * @param rType             返回类型
     * @param field             当前字段
     * @param relationTableMap  关联关系映射
     * @param relationJoinInfos 关联链路信息
     * @param skipRelation      是否跳过子查询
     * @return 更新后的关联关系映射
     */
    private static <R> Map<String, RelationGroup> fitRelationJoin(Class<R> rType, Field field,
                                                                  Map<String, RelationGroup> relationTableMap,
                                                                  List<RelationJoinInfo> relationJoinInfos,
                                                                  boolean skipRelation) {
        if (CollectionUtils.isEmpty(relationJoinInfos)) {
            return relationTableMap;
        }
        // 实体关联
        if (relationTableMap == null) {
            relationTableMap = new HashMap<>();
        }

        boolean isList = false;
        Class<?> type = field.getType();
        if (field.getType().isAssignableFrom(List.class)) {
            type = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            isList = true;
        }
        String selfField = relationJoinInfos.getFirst().getSelfField();
        try {
            // 保存映射关系
            Relation relation = new Relation(relationJoinInfos,
                    rType.getMethod("set" + StringUtils.capitalize(field.getName()), field.getType()),
                    rType.getMethod("get" + StringUtils.capitalize(selfField)),
                    type,
                    isList,
                    skipRelation);
            // 映射关系的可以是当前实体 需要获取数据的字段名
            // 有时候一个字段可能需要映射多个
            // 例如：user{id:'',dep:{userId:''},role:{userId:''}}
            // 其中dep需要使用user的id去映射dep，role也需要用user的id去映射。所以这里就会集合为<id,[dep,role]>
            RelationGroup relationGroup = relationTableMap.get(selfField);
            if (relationGroup == null) {
                relationGroup = new RelationGroup();
                relationTableMap.put(selfField, relationGroup);
            }
            relationGroup.getRelationList().add(relation);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return relationTableMap;
    }


    @SuppressWarnings("unchecked")
    static <R, T> R handleResultMap(Class<R> rType,
                                    BasicMapper<T> basicMapper,
                                    Map<String, Object> map,
                                    Map<String, RelationResult> associationMap,
                                    Map<String, RelationGroup> relationMap) {
        if (isDefaultType(rType)) {
            if (map == null) {
                return null;
            }
            Object object = map.get(map.keySet().iterator().next());
            if (object == null) {
                return null;
            }
            if (object instanceof Long && rType == Integer.class) {
                long value = (long) object;
                if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
                    int intValue = (int) value;
                    return (R) Integer.valueOf(intValue);
                } else {
                    throw new IllegalArgumentException("Value is out of int range");
                }
            }
            return (R) object;
        }
        R r = ModelClassUtils.constructor(rType);
        if (map == null) {
            return null;
        }
        Map<String, Method> setMethodMap = getMapTransfer(rType);

        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            Method setMethod = setMethodMap.get(key);

            if (setMethod == null) {
                continue;
            }

            // 判断嵌套的是不是一个对象
            if (map.get(key).getClass().isAssignableFrom(HashMap.class)) {
                if (associationMap != null && associationMap.containsKey(key)) {
                    Object object = handleResultMap(associationMap.get(key).getType(), basicMapper, (Map<String, Object>) map.get(key));
                    ReflectUtils.setValue(r, setMethod, object);
                    continue;
                }
            }

            Object v = map.get(key);

            if (relationMap != null) {
                // 将需要关联查询的内容保存起来，后面批量查询
                if (relationMap.get(key) != null) {
                    // 如果填写的是java字段，直接获取
                    relationMap.get(key).getColumnDataList().add(v);
                } else {
                    // 有的key是数据库字段，需要转换
                    String uncapitalizeKey = StringUtils.uncapitalize(setMethod.getName().substring(METHOD_PREFIX_LEN));
                    if (relationMap.get(uncapitalizeKey) != null) {
                        relationMap.get(uncapitalizeKey).getColumnDataList().add(v);
                    }
                }
            }
            if (v.getClass().isAssignableFrom(Map.class)) {
                r = handleResultMap(rType, basicMapper, map, associationMap, relationMap);
            } else {
                ReflectUtils.setValue(r, setMethod, v);
            }
        }
        return r;
    }

    private static <R> Map<String, Method> getMapTransfer(Class<R> rType) {
        Map<String, Method> setMethodMap = lruMapTransferCache.get(rType);
        if (setMethodMap != null) {
            return setMethodMap;
        }
        Map<String, Method> finalSetMethodMap = new HashMap<>();
        Table table = rType.getAnnotation(Table.class);
        ModelClassUtils.handleAllDeclaredFields(rType, (field) -> {
            Method setMethod = ModelClassUtils.getSetMethod(rType, field.getName());
            String tableFieldName = ModelClassUtils.getTableColumnName(table, field);
            if (tableFieldName != null) {
                finalSetMethodMap.put(ModelClassUtils.getTableName(rType) + "." + tableFieldName, setMethod);
                finalSetMethodMap.put(tableFieldName, setMethod);
            } else {
                // 下划线也需要自动映射
                finalSetMethodMap.put(FieldUtils.luCaseToUnderLine(field.getName()), setMethod);
            }
            finalSetMethodMap.put(field.getName(), setMethod);
            return true;
        });
        lruMapTransferCache.put(rType, finalSetMethodMap);
        return finalSetMethodMap;
    }

    /**
     * 处理集合注解的结果映射，将同一主键下的集合字段合并到List中
     *
     * @param rType              返回类型
     * @param map                查询结果Map
     * @param collectionMap      集合注解映射
     * @param collectionGroupMap 分组主键到集合字段的映射
     * @param relationMap        关联查询映射
     * @return 转换后的对象
     */
    @SuppressWarnings("unchecked")
    static <R, T> R handleResultMap(
            Class<R> rType,
            BasicMapper<T> basicMapper,
            Map<String, Object> map,
            Map<String, RelationResult> collectionMap,
            Map<Object, Map<String, List<Object>>> collectionGroupMap,
            Map<String, RelationGroup> relationMap) {

        if (isDefaultType(rType)) {
            return (R) map.get(map.keySet().iterator().next());
        }
        if (map == null) {
            return null;
        }
        if (collectionMap == null || collectionMap.isEmpty()) {
            // 没有集合注解，直接走普通字段处理
            return handleFields(rType, map, null, null, relationMap);
        }

        R r = ModelClassUtils.constructor(rType);
        boolean keepDoing = true;

        // 1. 处理集合字段
        for (Map.Entry<String, RelationResult> entry : collectionMap.entrySet()) {
            String key = entry.getKey();
            RelationResult relationResult = entry.getValue();
            Method setMethod = ModelClassUtils.getSetMethod(rType, relationResult.getFieldName());
            if (setMethod == null) continue;

            Object groupKey = map.get(relationResult.getGroupId());
            if (groupKey == null) {
                groupKey = map.get(FieldUtils.luCaseToUnderLine(relationResult.getGroupId()));
            }
            Map<String, List<Object>> listMap = collectionGroupMap.computeIfAbsent(groupKey, k -> new HashMap<>());
            List<Object> objectList = listMap.computeIfAbsent(key, k -> {
                List<Object> l = new ArrayList<>();
                ReflectUtils.setValue(r, setMethod, l);
                return l;
            });

            if (!objectList.isEmpty()) {
                keepDoing = false; // 已处理过
            }

            Object value = map.get(key);
            if (value != null) {
                if (value instanceof Map<?, ?>) {
                    value = handleResultMap(relationResult.getType(), basicMapper, (Map<String, Object>) value);
                }
                objectList.add(value);
            } else if (relationResult.isAutoFit()) {
                if (StringUtils.hasLength(relationResult.getColumn())) {
                    objectList.add(TypeConvertUtils.coverType(relationResult.getType(), map.get(relationResult.getColumn())));
                } else {
                    Object obj = handleResultMap(relationResult.getType(), basicMapper, map);
                    objectList.add(obj);
                }
            }
        }

        // 2. 如果是子集，直接返回null，避免重复
        if (!keepDoing) {
            return null;
        }

        // 3. 处理普通字段和关联字段
        handleFields(r, rType, map, collectionMap, collectionGroupMap, relationMap);

        return r;
    }

    /**
     * 统一处理普通字段和关联字段（可选跳过集合字段）
     */
    @SuppressWarnings("unchecked")
    private static <R> void handleFields(
            R r,
            Class<R> rType,
            Map<String, Object> map,
            Map<String, RelationResult> collectionMap,
            Map<Object, Map<String, List<Object>>> collectionGroupMap,
            Map<String, RelationGroup> relationMap) {
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            // 跳过集合字段
            if (collectionMap != null && collectionMap.containsKey(key)) {
                continue;
            }
            Method setMethod = ModelClassUtils.getSetMethod(rType, key);
            if (setMethod == null && key.contains("_")) {
                setMethod = ModelClassUtils.getSetMethod(rType, FieldUtils.underLineToLUCase(key));
            }
            if (setMethod == null) {
                continue;
            }
            Object v = map.get(key);

            // 关联字段收集
            if (relationMap != null) {
                RelationGroup group = relationMap.get(key);
                if (group != null) {
                    group.getColumnDataList().add(v);
                } else {
                    String uncapitalizeKey = StringUtils.uncapitalize(setMethod.getName().substring(METHOD_PREFIX_LEN));
                    group = relationMap.get(uncapitalizeKey);
                    if (group != null) {
                        group.getColumnDataList().add(v);
                    }
                }
            }

            if (v instanceof Map) {
                ReflectUtils.setValue(r, setMethod, handleFields((Class<Object>) ModelClassUtils.getDeclaredField(rType, key).getType(), (Map<String, Object>) v, collectionMap, collectionGroupMap, relationMap));
            } else {
                ReflectUtils.setValue(r, setMethod, v);
            }
        }
    }

    /**
     * handleFields 的重载：用于无集合注解时直接返回对象
     */
    private static <R> R handleFields(
            Class<R> rType,
            Map<String, Object> map,
            Map<String, RelationResult> collectionMap,
            Map<Object, Map<String, List<Object>>> collectionGroupMap,
            Map<String, RelationGroup> relationMap) {
        R r = ModelClassUtils.constructor(rType);
        handleFields(r, rType, map, collectionMap, collectionGroupMap, relationMap);
        return r;
    }

    /**
     * 结果映射关系，描述集合/对象字段的注解信息
     */
    @Getter
    @Setter
    @AllArgsConstructor
    static class RelationResult {
        /**
         * 关联分组 对应Collection groupMainId
         *
         * @see Collection
         */
        private String groupId;
        /**
         * 对应返回类里面注解的字段名称
         */
        private String fieldName;
        /**
         * 注解标注的字段类型
         */
        private Class<?> type;
        /**
         * 自动匹配所有
         */
        private boolean autoFit;
        /**
         * 只匹配这个字段
         */
        private String column;
    }

    @Getter
    @Setter
    static class Relation {
        /**
         * 关联设置注解
         */
        private List<RelationJoinInfo> relationJoinInfos;
        /**
         * 需要将结果设置到这个字段
         */
        private Method fieldSetMethod;
        /**
         * 关联的数据字段从这个属性获取
         */
        private Method relationColumnFieldGetMethod;
        /**
         * 是否返回数组
         */
        private boolean isList;
        /**
         * 返回对象类型
         */
        private Class<?> type;
        /**
         * 映射结果
         */
        private Map<Object, List<?>> dataMap;
        /**
         * 跳过该对象的关联查询
         * 默认为false，但是存在部分情况，出现循环引用，导致一直死循环，可以设置为true，将不会处理该类的内部关联
         */
        private boolean skipRelation;

        /**
         * 提供关联数据的字段
         */
        public Relation(List<RelationJoinInfo> relationJoinInfos,
                        Method fieldSetMethod,
                        Method relationColumnFieldGetMethod,
                        Class<?> type,
                        boolean isList,
                        boolean skipRelation) {
            this.relationJoinInfos = relationJoinInfos;
            this.fieldSetMethod = fieldSetMethod;
            this.relationColumnFieldGetMethod = relationColumnFieldGetMethod;
            this.type = type;
            this.isList = isList;
            this.skipRelation = skipRelation;
        }
    }


    @Getter
    @Setter
    static class RelationJoinInfo {
        /**
         * 需要连接的表，如果不填就是当前注解标注的对象
         */
        private Class<?> joinTable;

        /**
         * 指定关联实体的表，默认为当前实体，如果多级嵌套默认为上一个joinTable
         */
        private Class<?> selfTable;

        /**
         * 关联表字段，必填
         * 注意是填写java字段
         */
        private String joinTableField;

        /**
         * 用当前实体的该字段数据去和关联表column进行查询
         * 注意是填写java字段
         */
        private String selfField;

        /**
         * 扩展条件
         */
        private String where;

        /**
         * 查询字段
         */
        private String[] select;

        /**
         * 排序条件
         */
        private String orderBy;

        /**
         * 条件比较方式
         */
        private String compare;
    }

    @Getter
    @Setter
    static class RelationGroup {
        /**
         * 关联关系
         */
        private List<Relation> relationList = new ArrayList<>();
        /**
         * 关联需要使用的数据
         */
        private Set<Object> columnDataList = new HashSet<>();
    }

}

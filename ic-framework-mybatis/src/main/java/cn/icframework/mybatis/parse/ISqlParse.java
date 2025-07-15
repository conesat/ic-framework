package cn.icframework.mybatis.parse;

import java.util.List;
import java.util.Map;

/**
 * SQL 解析接口，定义了针对实体对象的常用 SQL 语句生成方法。
 * <p>
 * 该接口主要用于根据实体对象及相关参数，动态生成常用的增删改查 SQL 语句，
 * 以便于 MyBatis 等 ORM 框架进行数据库操作。
 * 
 */
public interface ISqlParse {
    /**
     * 生成单条插入 SQL 语句。
     * <p>
     * 根据传入的实体对象，自动生成对应的 INSERT SQL 语句。
     * 
     * @param entity 实体对象，包含需要插入的数据字段
     * @return 生成的 INSERT SQL 语句字符串
     */
    String parseInsert(Object entity);

    /**
     * 生成批量插入 SQL 语句。
     * <p>
     * 根据传入的实体对象集合，自动生成批量插入的 INSERT SQL 语句。
     * 
     * @param entities 实体对象集合，每个对象代表一条待插入的数据
     * @return 生成的批量 INSERT SQL 语句字符串
     */
    String parseInsertBatch(List<?> entities);

    /**
     * 生成根据主键更新 SQL 语句。
     * <p>
     * 根据实体对象的主键，生成对应的 UPDATE SQL 语句。
     * 
     * @param entity 实体对象，包含主键信息和待更新字段
     * @param withNull 是否包含值为 null 的字段（true: 包含，false: 不包含）
     * @return 生成的 UPDATE SQL 语句字符串
     */
    String parseUpdateById(Object entity, boolean withNull);

    /**
     * 生成根据条件更新 SQL 语句。
     * <p>
     * 根据实体对象和条件参数，生成对应的 UPDATE SQL 语句。
     * 
     * @param entity 实体对象，包含待更新字段
     * @param params 条件参数，指定 WHERE 子句的条件
     * @param withNull 是否包含值为 null 的字段（true: 包含，false: 不包含）
     * @return 生成的 UPDATE SQL 语句字符串
     */
    String parseUpdate(Object entity, Map<String, Object> params, boolean withNull);

    /**
     * 生成根据主键查询单条记录的 SQL 语句。
     * <p>
     * 根据 Mapper 类型，生成根据主键查询单条记录的 SELECT SQL 语句。
     * 
     * @param mapperType Mapper 接口的 Class 类型
     * @return 生成的 SELECT SQL 语句字符串
     */
    String parseSelectOneById(Class<?> mapperType);

    /**
     * 生成根据主键集合批量查询的 SQL 语句。
     * <p>
     * 根据 Mapper 类型，生成根据主键集合批量查询的 SELECT SQL 语句。
     * 
     * @param mapperType Mapper 接口的 Class 类型
     * @return 生成的批量 SELECT SQL 语句字符串
     */
    String parseSelectByIds(Class<?> mapperType);

    /**
     * 生成根据主键集合批量删除的 SQL 语句。
     * <p>
     * 根据 Mapper 类型，生成根据主键集合批量删除的 DELETE SQL 语句。
     * 
     * @param mapperType Mapper 接口的 Class 类型
     * @return 生成的批量 DELETE SQL 语句字符串
     */
    String parseDeleteByIds(Class<?> mapperType);

    /**
     * 生成根据主键删除单条记录的 SQL 语句。
     * <p>
     * 根据 Mapper 类型，生成根据主键删除单条记录的 DELETE SQL 语句。
     * 
     * @param mapperType Mapper 接口的 Class 类型
     * @return 生成的 DELETE SQL 语句字符串
     */
    String parseDeleteById(Class<?> mapperType);
} 
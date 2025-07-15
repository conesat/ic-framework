package cn.icframework.mybatis.consts;

/**
 * IC MyBatis 参数常量定义。
 * 定义MyBatis中使用的各种参数名称常量。
 * @author hzl
 */
public interface IcParamsConsts {
    /**
     * 返回实体key
     */
    String PARAMETER_RETURN_ENTITY = "returnEntity";
    /**
     * 实体key
     */
    String PARAMETER_ENTITY = "entity";
    /**
     * 主键key
     */
    String PARAMETER_PRIMARY_KEY = "primaryKey";
    /**
     * 主键key s
     */
    String PARAMETER_PRIMARY_KEYS = "primaryKeys";
    /**
     * sql 包装器key
     */
    String PARAMETER_SW = "sw";
    /**
     * page key
     */
    String PARAMETER_PAGE = "page";
    /**
     * 删除字段key
     */
    String PARAMETER_LOGIC_DELETE = "logicDelete";
    /**
     * 逻辑删除字段取值
     */
    String PARAMETER_LOGIC_DELETE_GET = "#{logicDelete}";
    /**
     * 忽略字段key
     */
    String PARAMETER_IGNORE_NULL_FIELDS = "ignoreNullFields";
    /**
     * 属性前缀
     */
    String PARAMETER_PREFIX = "p_";
    /**
     * sql 构造器构造的参数都放在这个key中
     */
    String PARAMETER_S = "params";


    /**
     * 默认删除属性 的值
     */
    int VALUE_LOGIC_DELETED = 1;


    // 方法分割线  -----------------------------------

    /**
     * 构造参数
     *
     * @param key 参数名
     * @return 拼接后的参数key，格式为 p_key
     */
    static String PARAM_KEY(String key) {
        return PARAMETER_PREFIX + key;
    }

    /**
     * 取值 构造 返回
     *
     * @param key 参数名
     * @return MyBatis取值表达式，格式为 #{params.key}
     */
    static String GET_PARAM_S(String key) {
        return String.format("#{%s.%s}", PARAMETER_S, key);
    }
}

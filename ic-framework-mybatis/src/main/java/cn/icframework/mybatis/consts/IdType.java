package cn.icframework.mybatis.consts;

/**
 * ID生成策略枚举。
 *
 * @author ic-framework
 * @since 2024/06/09
 */
public enum IdType {

    /**
     * 自增的方式
     */
    AUTO,

    /**
     * 通过 Uuid 生成器生成
     */
    UUID,

    /**
     * 其他方式，比如在代码层用户手动设置
     */
    INPUT,

    /**
     * 通过 雪花 生成器生成
     */
    SNOWFLAKE,
}

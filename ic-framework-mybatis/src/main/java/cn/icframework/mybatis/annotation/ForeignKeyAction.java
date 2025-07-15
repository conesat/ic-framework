package cn.icframework.mybatis.annotation;

/**
 * 外键操作类型常量定义。
 *
 * @author ic-framework
 * @since 2024/06/09
 */
public interface ForeignKeyAction {
    /**
     * 默认值
     */
    String NONE = "NONE";
    /**
     * 级联
     */
    String CASCADE = "CASCADE";
    /**
     * 限制
     */
    String RESTRICT = "RESTRICT";
    /**
     * 设置为默认
     */
    String SET_DEFAULT = "SET DEFAULT";
    /**
     * 设置为空
     */
    String SET_NULL = "SET NULL";
}

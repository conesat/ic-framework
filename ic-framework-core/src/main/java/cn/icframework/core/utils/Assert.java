package cn.icframework.core.utils;

import cn.icframework.core.common.exception.MessageException;
import org.apache.commons.lang3.StringUtils;


/**
 * 断言工具类
 *
 * 提供常用的断言方法。
 */
public class Assert {
    /**
     * 默认构造方法
     */
    public Assert() {}

    /**
     * 判断字符串是否非空，否则抛出异常。
     * @param charSequence 字符序列
     * @param message 异常信息
     */
    public static void isNotEmpty(CharSequence charSequence, String message) {
        if (StringUtils.isEmpty(charSequence)) {
            throw new MessageException(message);
        }
    }

    /**
     * 判断两个对象不相等，否则抛出异常。
     * @param o1 对象1
     * @param o2 对象2
     * @param message 异常信息
     */
    public static void isNotEquals(Object o1, Object o2, String message) {
        if (o1.equals(o2)) {
            throw new MessageException(message);
        }
    }

    /**
     * 判断两个对象相等，否则抛出异常。
     * @param o1 对象1
     * @param o2 对象2
     * @param message 异常信息
     */
    public static void isEquals(Object o1, Object o2, String message) {
        if (o1 == null && o2 != null) {
            throw new MessageException(message);
        } else if (o1 != null && !o1.equals(o2)) {
            throw new MessageException(message);
        }
    }

    /**
     * 判断表达式为 false，否则抛出异常。
     * @param expression 布尔表达式
     * @param message 异常信息
     */
    public static void isFalse(Boolean expression, String message) {
        if (expression != null && expression) {
            throw new MessageException(message);
        }
    }
    /**
     * 判断表达式为 false，否则抛出指定异常。
     * @param expression 布尔表达式
     * @param e 运行时异常
     */
    public static void isFalse(Boolean expression, RuntimeException e) {
        if (expression != null && expression) {
            throw e;
        }
    }

    /**
     * 判断数字不为 0，否则抛出异常。
     * @param expression 数值
     * @param message 异常信息
     */
    public static void isNot0(Number expression, String message) {
        if (expression == null || expression.longValue() == 0) {
            throw new MessageException(message);
        }
    }
    /**
     * 判断数字为 0，否则抛出异常。
     * @param expression 数值
     * @param message 异常信息
     */
    public static void is0(Number expression, String message) {
        if (expression == null || expression.longValue() != 0) {
            throw new MessageException(message);
        }
    }

    /**
     * 判断表达式为 true，否则抛出异常。
     * @param expression 布尔表达式
     * @param message 异常信息
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new MessageException(message);
        }
    }
    /**
     * 判断表达式为 true，否则抛出指定异常。
     * @param expression 布尔表达式
     * @param e 运行时异常
     */
    public static void isTrue(boolean expression, RuntimeException e) {
        if (!expression) {
            throw e;
        }
    }

    /**
     * 判断对象为 null，否则抛出异常。
     * @param o 对象
     * @param message 异常信息
     */
    public static void isNull(Object o, String message) {
        if (o != null) {
            throw new MessageException(message);
        }
    }

    /**
     * 判断对象不为 null，否则抛出异常。
     * @param o 对象
     * @param message 异常信息
     */
    public static void isNotNull(Object o, String message) {
        if (o == null) {
            throw new MessageException(message);
        }
    }
}

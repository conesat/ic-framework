package cn.icframework.common.utils;


import org.springframework.util.StringUtils;

/**
 * 类型转换工具类
 *
 * 提供常用的对象与基本类型之间的转换方法。
 *
 * @author hzl
 * @since 2022/6/16 14:01
 */
public class ConvertUtils {
    /**
     * 转换为Boolean类型
     * @param o 输入对象
     * @param def 默认值
     * @return Boolean值
     */
    public static Boolean toBoolean(Object o, Boolean def) {
        if (o == null) {
            return def;
        }
        Class<?> aClass = o.getClass();
        if (aClass == Boolean.class) {
            return (Boolean) o;
        }

        if (!StringUtils.hasLength(o.toString())) {
            return def;
        }
        if (o.equals("true")) {
            return true;
        } else if (o.equals("false")) {
            return false;
        }
        try {
            return Double.parseDouble(o.toString()) != 0;
        } catch (Exception ignored) {
        }
        return def;
    }

    /**
     * 转换为Integer类型，默认值为null
     * @param o 输入对象
     * @return Integer值
     */
    public static Integer toInteger(Object o) {
        return toInteger(o, null);
    }

    /**
     * 转换为Integer类型
     * @param o 输入对象
     * @param def 默认值
     * @return Integer值
     */
    public static Integer toInteger(Object o, Integer def) {
        if (o == null) {
            return def;
        }
        String v = o.toString();
        if (StringUtils.isEmpty(v)) {
            return def;
        }
        try {
            return Integer.parseInt(v);
        } catch (Exception ignored) {
        }
        return def;
    }

    /**
     * 转换为Long类型，默认值为null
     * @param o 输入对象
     * @return Long值
     */
    public static Long toLong(Object o) {
        return toLong(o, null);
    }

    /**
     * 转换为Long类型
     * @param o 输入对象
     * @param def 默认值
     * @return Long值
     */
    public static Long toLong(Object o, Long def) {
        if (o == null) {
            return def;
        }
        String v = o.toString();
        if (StringUtils.isEmpty(v)) {
            return def;
        }
        try {
            return Long.parseLong(v);
        } catch (Exception ignored) {
        }
        return def;
    }

    /**
     * 转换为Double类型，默认值为null
     * @param o 输入对象
     * @return Double值
     */
    public static Double toDouble(Object o) {
        return toDouble(o, null);
    }

    /**
     * 转换为Double类型
     * @param o 输入对象
     * @param def 默认值
     * @return Double值
     */
    public static Double toDouble(Object o, Double def) {
        if (o == null) {
            return def;
        }
        String v = o.toString();
        if (StringUtils.isEmpty(v)) {
            return def;
        }
        try {
            return Double.parseDouble(v);
        } catch (Exception ignored) {
        }
        return def;
    }

    /**
     * 字符串转Boolean，默认值为false
     * @param o 输入字符串
     * @return Boolean值
     */
    public static Boolean toBoolean(String o) {
        return toBoolean(o, false);
    }

    /**
     * 转换为字符串，默认值为null
     * @param o 输入对象
     * @return 字符串
     */
    public static String toString(Object o) {
        return toString(o, null);
    }

    /**
     * 转换为字符串
     * @param o 输入对象
     * @param def 默认值
     * @return 字符串
     */
    public static String toString(Object o, String def) {
        if (o == null) {
            return def;
        }
        return o.toString();
    }
}

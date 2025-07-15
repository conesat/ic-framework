package cn.icframework.core.common.helper;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * 国际化工具类
 */
public class I18N {
    /**
     * 默认构造方法
     */
    public I18N() {}

    /**
     * Spring 国际化消息源
     */
    @Autowired
    private MessageSource messageSource;

    /**
     * 静态消息源
     */
    private static MessageSource staticMessageSource;

    /**
     * 初始化静态消息源
     */
    @PostConstruct
    public void init() {
        I18N.staticMessageSource = messageSource;
    }

    /**
     * 直接获取国际化数据
     *
     * @param messageKey 消息 key
     * @param arguments 参数
     * @return 国际化消息字符串
     */
    public static String g(Object messageKey, Object... arguments) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            String message = staticMessageSource.getMessage(messageKey.toString(), null, locale);
            if (StringUtils.isEmpty(message)) {
                return MessageFormat.format(messageKey.toString(), arguments);
            }
            return MessageFormat.format(message, arguments);
        } catch (Exception e) {
            return MessageFormat.format(messageKey.toString(), arguments);
        }
    }

    /**
     * 类前缀+key获取国际化数据
     *
     * @param messageKey 消息 key
     * @param tClass 类
     * @param arguments 参数
     * @return 国际化消息字符串
     * @param <T> 泛型
     */
    public static <T> String g(Object messageKey, Class<T> tClass, Object... arguments) {
        String g = g(tClass.getSimpleName() + "." + messageKey, arguments);
        if (StringUtils.isNotBlank(g)) {
            return g.replace(tClass.getSimpleName() + ".", "");
        }
        return messageKey.toString();
    }

    /*错误信息*/

    /**
     * 类前缀+key获取国际化错误信息
     *
     * @param tClass 类
     * @param messageKey 消息 key
     * @param arguments 参数
     * @return 国际化错误消息字符串
     * @param <T> 泛型
     */
    public static <T> String e(Class<T> tClass, String messageKey, Object... arguments) {
        return e(tClass.getSimpleName(), messageKey, arguments);
    }

    /**
     * 指定类名+key获取国际化错误信息
     *
     * @param className 类名
     * @param messageKey 消息 key
     * @param arguments 参数
     * @return 国际化错误消息字符串
     */
    public static String e(String className, String messageKey, Object... arguments) {
        String g = g(className + ".ERR." + messageKey);
        if (StringUtils.isNotBlank(g)) {
            return MessageFormat.format(g.replace(className + ".ERR.", ""), arguments);
        }
        return MessageFormat.format(messageKey, arguments);
    }
}
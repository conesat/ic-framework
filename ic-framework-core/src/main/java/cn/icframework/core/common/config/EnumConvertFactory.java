package cn.icframework.core.common.config;


import cn.icframework.common.interfaces.IEnum;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 枚举类型转换工厂。
 * <p>
 * 用于将字符串类型参数自动转换为实现 IEnum 接口的枚举类型。
 * </p>
 * @author hzl
 * @since 2023/6/21
 */
@Component
public class EnumConvertFactory implements ConverterFactory<String, IEnum> {
    /**
     * 获取字符串到 IEnum 枚举的转换器。
     *
     * @param tClass 枚举类型的 Class
     * @return Converter
     * @param <T> 枚举类型
     */
    @NotNull
    @Override
    public <T extends IEnum> Converter<String, T> getConverter(@NotNull Class<T> tClass) {
        return new StringToIEum<>(tClass);
    }

    /**
     * 字符串转 IEnum 枚举的转换器实现。
     * @param <T> 枚举类型
     */
    @SuppressWarnings("all")
    private static class StringToIEum<T extends IEnum> implements Converter<String, T> {
        /**
         * 目标枚举类型
         */
        private Class<T> targerType;

        /**
         * 构造方法
         * @param targerType 枚举类型
         */
        public StringToIEum(Class<T> targerType) {
            this.targerType = targerType;
        }

        /**
         * 字符串转枚举
         * @param source 字符串
         * @return 枚举对象
         */
        @Override
        public T convert(String source) {
            if (StringUtils.isEmpty(source)) {
                return null;
            }
            return (T) EnumConvertFactory.getIEnum(this.targerType, source);
        }
    }

    /**
     * 根据 code 字段获取对应的枚举对象。
     *
     * @param tClass 枚举类型
     * @param source code 字符串
     * @return 匹配的枚举对象，未匹配返回 null
     * @param <T> 枚举类型
     */
    public static <T extends IEnum> Object getIEnum(Class<T> tClass, String source) {
        for (T enumObj : tClass.getEnumConstants()) {
            if (source.equals(String.valueOf(enumObj.code()))) {
                return enumObj;
            }
        }
        return null;
    }
}
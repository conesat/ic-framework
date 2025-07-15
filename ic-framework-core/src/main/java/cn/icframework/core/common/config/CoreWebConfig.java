package cn.icframework.core.common.config;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Web配置类
 *
 * 提供Web相关配置。
 */
@Configuration
public class CoreWebConfig implements WebMvcConfigurer {
    /**
     * 默认构造方法
     */
    public CoreWebConfig() {}

    /**
     * 枚举转换工厂
     */
    @Resource
    private EnumConvertFactory enumConvertFactory;

    /**
     * 添加自定义格式化器和转换器，包括枚举和日期时间类型。
     *
     * @param registry 格式化器注册表
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(enumConvertFactory);
        registry.addConverter(String.class, LocalDateTime.class, source -> StringUtils.isEmpty(source) ? null : LocalDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        registry.addConverter(String.class, LocalDate.class, source -> StringUtils.isEmpty(source) ? null : LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        registry.addConverter(String.class, LocalTime.class, source -> StringUtils.isEmpty(source) ? null : LocalTime.parse(source, DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

}
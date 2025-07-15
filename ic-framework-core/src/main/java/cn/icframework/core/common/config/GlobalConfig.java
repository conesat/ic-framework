package cn.icframework.core.common.config;

import cn.icframework.common.interfaces.IEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * 全局配置类。
 * <p>
 * 配置全局 Jackson 序列化规则，包括 IEnum 枚举和 Long 类型的序列化。
 * </p>
 * @author hzl
 * @since 2024/8/22
 */
@Configuration
public class GlobalConfig {

    /**
     * 配置 IEnum 枚举类型的序列化方式。
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer enumCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.serializerByType(IEnum.class, new JsonSerializer<IEnum>() {
            @Override
            public void serialize(IEnum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeStartObject();
                gen.writeNumberField("code", value.code());
                gen.writeStringField("text", value.text());
                gen.writeEndObject();
            }
        });
    }

    /**
     * 配置 long 基本类型的序列化方式。
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer longCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.serializerByType(long.class, getLongJsonSerializer());
    }

    /**
     * 配置 Long 包装类型的序列化方式。
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer longLCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.serializerByType(Long.class, getLongJsonSerializer());
    }

    /**
     * 获取 Long 类型的自定义序列化器。
     *
     * @return JsonSerializer<Long>
     */
    @NotNull
    private static JsonSerializer<Long> getLongJsonSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value == null) {
                    gen.writeNull();
                } else if (value > 9007199254740991L) {
                    // 超过js number最大值，转为字符串
                    gen.writeString(value.toString());
                } else {
                    gen.writeNumber(value);
                }
            }
        };
    }

}
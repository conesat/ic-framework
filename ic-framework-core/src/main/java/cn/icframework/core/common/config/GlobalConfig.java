package cn.icframework.core.common.config;

import cn.icframework.common.interfaces.IEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.module.SimpleModule;
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
     * 配置 IEnum 枚举类型的反序列化方式。
     *
     * @return Module
     */
    @Bean
    public Module iEnumDeserializeModule() {
        return new IEnumDeserializeModule();
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

    /**
     * IEnum 反序列化模块。
     * <p>
     * Jackson 默认会把数字反序列化为枚举 ordinal，这会和 IEnum.code() 语义冲突。
     * 这里统一按 code 解析，同时兼容枚举 name 和 {code,text} 对象结构。
     * </p>
     */
    private static class IEnumDeserializeModule extends SimpleModule {
        @Override
        public void setupModule(SetupContext context) {
            super.setupModule(context);
            context.addDeserializers(new Deserializers.Base() {
                @Override
                public JsonDeserializer<?> findEnumDeserializer(Class<?> type,
                                                                DeserializationConfig config,
                                                                BeanDescription beanDesc) {
                    if (type.isEnum() && IEnum.class.isAssignableFrom(type)) {
                        return new IEnumDeserializer(type);
                    }
                    return null;
                }
            });
        }
    }

    /**
     * IEnum 反序列化器。
     */
    private static class IEnumDeserializer extends JsonDeserializer<Enum<?>> {
        private final Class<?> enumType;

        private IEnumDeserializer(Class<?> enumType) {
            this.enumType = enumType;
        }

        @Override
        public Enum<?> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            JsonToken token = parser.currentToken();
            if (token == JsonToken.VALUE_NULL) {
                return null;
            }
            if (token == JsonToken.VALUE_NUMBER_INT) {
                return fromCode(parser.getIntValue(), parser);
            }
            if (token == JsonToken.VALUE_STRING) {
                String value = parser.getText();
                if (value == null || value.isBlank()) {
                    return null;
                }
                return fromString(value.trim(), parser);
            }
            if (token == JsonToken.START_OBJECT) {
                return fromObject(parser);
            }
            throw invalid(parser.getText(), parser);
        }

        private Enum<?> fromObject(JsonParser parser) throws IOException {
            Integer code = null;
            String name = null;
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = parser.currentName();
                parser.nextToken();
                if ("code".equals(fieldName) && parser.currentToken() == JsonToken.VALUE_NUMBER_INT) {
                    code = parser.getIntValue();
                } else if ("name".equals(fieldName) && parser.currentToken() == JsonToken.VALUE_STRING) {
                    name = parser.getText();
                } else {
                    parser.skipChildren();
                }
            }
            if (code != null) {
                return fromCode(code, parser);
            }
            if (name != null && !name.isBlank()) {
                return fromString(name.trim(), parser);
            }
            return null;
        }

        private Enum<?> fromString(String value, JsonParser parser) throws IOException {
            try {
                return fromCode(Integer.parseInt(value), parser);
            } catch (NumberFormatException ignored) {
                // 非数字 code 时继续按枚举名称匹配。
            }
            for (Object item : enumType.getEnumConstants()) {
                Enum<?> enumItem = (Enum<?>) item;
                if (enumItem.name().equals(value) || enumItem.name().equalsIgnoreCase(value)) {
                    return enumItem;
                }
            }
            throw invalid(value, parser);
        }

        private Enum<?> fromCode(int code, JsonParser parser) throws IOException {
            for (Object item : enumType.getEnumConstants()) {
                IEnum enumItem = (IEnum) item;
                if (enumItem.code() == code) {
                    return (Enum<?>) item;
                }
            }
            throw invalid(code, parser);
        }

        private InvalidFormatException invalid(Object value, JsonParser parser) {
            return InvalidFormatException.from(parser,
                    "Cannot deserialize " + enumType.getSimpleName() + " from value " + value,
                    value,
                    enumType);
        }
    }

}

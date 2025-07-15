package cn.icframework.core.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime 序列化与反序列化配置。
 * <p>
 * 配置 Jackson 对 LocalDateTime 类型的序列化与反序列化格式。
 * </p>
 * @author hzl
 * @since 2023/6/14
 */
@Configuration
public class LocalDateTimeSerializerConfig {

    /**
     * 配置 Jackson 对 LocalDateTime 的序列化与反序列化。
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            //序列化
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            //反序列化
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer());
        };
    }

    /**
     * LocalDateTime 反序列化器。
     */
    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        /**
         * 反序列化方法，将时间戳转为 LocalDateTime。
         *
         * @param p JsonParser
         * @param deserializationContext 反序列化上下文
         * @return LocalDateTime
         * @throws IOException IO异常
         */
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext deserializationContext)
                throws IOException {
            long timestamp = p.getValueAsLong();
            if (timestamp > 0) {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
            } else {
                return null;
            }
        }
    }

}

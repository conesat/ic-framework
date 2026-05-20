package cn.icframework.core.common.config;

import cn.icframework.common.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalConfigTest {

    private final ObjectMapper objectMapper = buildObjectMapper();

    @Test
    void shouldDeserializeIEnumByCodeInsteadOfOrdinal() throws Exception {
        TestDto dto = objectMapper.readValue("{\"status\":1}", TestDto.class);

        assertEquals(Status.ENABLE, dto.status);
    }

    @Test
    void shouldKeepIEnumObjectSerialization() throws Exception {
        String json = objectMapper.writeValueAsString(new TestDto(Status.ENABLE));

        assertEquals("{\"status\":{\"code\":1,\"text\":\"可用\"}}", json);
    }

    private static ObjectMapper buildObjectMapper() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.enumCustomizer().customize(builder);
        builder.modules(globalConfig.iEnumDeserializeModule());
        return builder.build();
    }

    private static class TestDto {
        public Status status;

        public TestDto() {
        }

        public TestDto(Status status) {
            this.status = status;
        }
    }
}

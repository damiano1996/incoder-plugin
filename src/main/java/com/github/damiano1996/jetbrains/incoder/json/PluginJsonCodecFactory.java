package com.github.damiano1996.jetbrains.incoder.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.langchain4j.internal.Json;
import dev.langchain4j.spi.json.JsonCodecFactory;
import java.io.IOException;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;

public final class PluginJsonCodecFactory implements JsonCodecFactory {

    @Override
    public Json.@NotNull JsonCodec create() {
        ObjectMapper mapper =
                new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return new JacksonCodec(mapper);
    }

    private record JacksonCodec(ObjectMapper mapper) implements Json.JsonCodec {

        @Override
        public String toJson(Object value) {
            try {
                return mapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON serialization error", e);
            }
        }

        @Override
        public <T> T fromJson(String json, Class<T> type) {
            try {
                return mapper.readValue(json, type);
            } catch (IOException e) {
                throw new RuntimeException("JSON deserialization error into " + type, e);
            }
        }

        @Override
        public <T> T fromJson(String json, Type type) {
            try {
                return mapper.readValue(json, mapper.getTypeFactory().constructType(type));
            } catch (IOException e) {
                throw new RuntimeException("JSON deserialization error into " + type, e);
            }
        }
    }
}

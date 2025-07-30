package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ToolInputArgs {

    @JsonProperty("args")
    public Map<String, Object> args = new HashMap<>();

    /**
     * Creates ToolInputArgs from JSON string
     *
     * @param argsString JSON string representation
     * @return ToolInputArgs instance
     * @throws JsonProcessingException if parsing fails
     */
    public static ToolInputArgs fromString(String argsString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(argsString, ToolInputArgs.class);
    }

    /**
     * Alternative method that parses the string as a Map first Useful when the JSON structure
     * doesn't exactly match the class
     *
     * @param argsString JSON string representation
     * @return ToolInputArgs instance
     * @throws JsonProcessingException if parsing fails
     */
    public static ToolInputArgs fromStringAsMap(String argsString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> argsMap = objectMapper.readValue(argsString, new TypeReference<>() {});

        return new ToolInputArgs(argsMap);
    }
}

package com.github.damiano1996.jetbrains.incoder.language.model.server;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic.AnthropicLanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic.AnthropicParameters;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ollama.OllamaLanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ollama.OllamaParameters;
import com.github.damiano1996.jetbrains.incoder.language.model.server.openai.OpenAiLanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.openai.OpenAiParameters;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@SuperBuilder
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "serverName")
@JsonSubTypes({
    @JsonSubTypes.Type(
            value = AnthropicParameters.class,
            name = AnthropicLanguageModelServer.ANTHROPIC),
    @JsonSubTypes.Type(value = OllamaParameters.class, name = OllamaLanguageModelServer.OLLAMA),
    @JsonSubTypes.Type(value = OpenAiParameters.class, name = OpenAiLanguageModelServer.OPEN_AI)
})
public abstract class LanguageModelParameters {
    public String serverName;
    public String modelName;
    public String baseUrl;
    public String apiKey;
    public Integer maxTokens;
    public Double temperature;
    public List<String> stopSequences;
    public Integer timeout;
}

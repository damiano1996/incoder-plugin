package com.github.damiano1996.jetbrains.incoder.language.model.server;

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

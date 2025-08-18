package com.github.damiano1996.jetbrains.incoder.language.model.server;

import java.time.Duration;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class LanguageModelParameters {
    public String serverName;
    public String modelName;
    public String baseUrl;
    public String apiKey;
    public Integer maxTokens;
    public Double temperature;
    public List<String> stopSequences;
    public Duration timeout;
}

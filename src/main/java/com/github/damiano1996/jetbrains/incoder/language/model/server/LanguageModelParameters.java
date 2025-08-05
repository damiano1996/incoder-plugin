package com.github.damiano1996.jetbrains.incoder.language.model.server;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LanguageModelParameters {
    public String serverName;
    public String modelName;
    public String baseUrl;
    public String apiKey;
    public Integer maxTokens;
    public Double temperature;
}

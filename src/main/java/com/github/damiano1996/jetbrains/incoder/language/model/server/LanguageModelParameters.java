package com.github.damiano1996.jetbrains.incoder.language.model.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class LanguageModelParameters {
    private String serverName;
    private String modelName;
    private String baseUrl;
    private String apiKey;
    private Integer maxTokens;
    private Double temperature;

}

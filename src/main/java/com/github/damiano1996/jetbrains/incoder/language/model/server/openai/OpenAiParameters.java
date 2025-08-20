package com.github.damiano1996.jetbrains.incoder.language.model.server.openai;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class OpenAiParameters extends LanguageModelParameters {
    public Integer maxCompletionTokens;
    public Double presencePenalty;
    public Double frequencyPenalty;
    public Map<String, Integer> logitBias;
    public String responseFormat;
    public Boolean strictJsonSchema;
    public Integer seed;
    public String user;
    public Boolean strictTools;
    public Boolean store;
    public Map<String, String> metadata;
    public String serviceTier;
    public String organizationId;
    public String projectId;
}

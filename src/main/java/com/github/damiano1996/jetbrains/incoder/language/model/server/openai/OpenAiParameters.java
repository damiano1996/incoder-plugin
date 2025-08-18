package com.github.damiano1996.jetbrains.incoder.language.model.server.openai;

import static com.github.damiano1996.jetbrains.incoder.language.model.server.BaseProviderUIStrategy.copyCommon;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import java.util.Collections;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
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

    public static OpenAiParameters toOpenAi(LanguageModelParameters base) {
        if (base instanceof OpenAiParameters o) {
            return o;
        }
        OpenAiParameters o = new OpenAiParameters();
        copyCommon(base, o);
        if (o.maxCompletionTokens == null)
            o.maxCompletionTokens = o.maxTokens != null ? o.maxTokens : 2048;
        if (o.presencePenalty == null) o.presencePenalty = 0.0;
        if (o.frequencyPenalty == null) o.frequencyPenalty = 0.0;
        if (o.logitBias == null) o.logitBias = Collections.emptyMap();
        if (o.stopSequences == null) o.stopSequences = Collections.emptyList();
        if (o.metadata == null) o.metadata = Collections.emptyMap();
        return o;
    }
}

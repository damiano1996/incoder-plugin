package com.github.damiano1996.jetbrains.incoder.language.model.server.ollama;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.chat.request.ResponseFormat;
import java.util.Map;
import java.util.Set;
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
public class OllamaParameters extends LanguageModelParameters {
    public Integer topK;
    public Double repeatPenalty;
    public Integer seed;
    public Integer numPredict;
    public Integer numCtx;
    public ResponseFormat responseFormat;
    public Set<Capability> supportedCapabilities;
    public Map<String, String> customHeaders;
}

package com.github.damiano1996.jetbrains.incoder.language.model.server.ollama;

import static com.github.damiano1996.jetbrains.incoder.language.model.server.BaseProviderUIStrategy.copyCommon;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.chat.request.ResponseFormat;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OllamaParameters extends LanguageModelParameters {
    public Integer topK;
    public Double repeatPenalty;
    public Integer seed;
    public Integer numPredict;
    public Integer numCtx;
    public ResponseFormat responseFormat;
    public Set<Capability> supportedCapabilities;
    public Map<String, String> customHeaders;

    public static OllamaParameters toOllama(LanguageModelParameters base) {
        if (base instanceof OllamaParameters ol) {
            return ol;
        }

        OllamaParameters ol = new OllamaParameters();
        copyCommon(base, ol);
        if (ol.topK == null) ol.topK = 40;
        if (ol.repeatPenalty == null) ol.repeatPenalty = 1.1;
        if (ol.seed == null) ol.seed = 0;
        if (ol.numPredict == null) ol.numPredict = ol.maxTokens != null ? ol.maxTokens : 2048;
        if (ol.numCtx == null) ol.numCtx = 8192;
        return ol;
    }
}

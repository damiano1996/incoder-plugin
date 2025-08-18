package com.github.damiano1996.jetbrains.incoder.language.model.server.ollama;

import static com.github.damiano1996.jetbrains.incoder.language.model.server.ollama.OllamaParameters.toOllama;

import com.github.damiano1996.jetbrains.incoder.language.model.server.BaseLanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaModel;
import dev.langchain4j.model.ollama.OllamaModels;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OllamaLanguageModelServer extends BaseLanguageModelServer {

    public static final String OLLAMA = "Ollama";

    @Override
    public ChatLanguageModel createChatLanguageModel(LanguageModelParameters parameters) {
        OllamaParameters p = toOllama(parameters);

        return OllamaChatModel.builder()
                .baseUrl(p.baseUrl)
                .modelName(p.modelName)
                .temperature(p.temperature)
                .topK(p.topK)
                .repeatPenalty(p.repeatPenalty)
                .seed(p.seed)
                .numPredict(p.numPredict != null ? p.numPredict : p.maxTokens)
                .numCtx(p.numCtx)
                .stop(p.stopSequences)
                .responseFormat(p.responseFormat)
                .timeout(p.timeout)
                .build();
    }

    @Override
    public StreamingChatLanguageModel createStreamingChatLanguageModel(
            LanguageModelParameters parameters) {
        OllamaParameters p = toOllama(parameters);

        return OllamaStreamingChatModel.builder()
                .baseUrl(p.baseUrl)
                .modelName(p.modelName)
                .temperature(p.temperature)
                .topK(p.topK)
                .repeatPenalty(p.repeatPenalty)
                .seed(p.seed)
                .numPredict(p.numPredict != null ? p.numPredict : p.maxTokens)
                .numCtx(p.numCtx)
                .stop(p.stopSequences)
                .responseFormat(p.responseFormat)
                .timeout(p.timeout)
                .build();
    }

    @Override
    public String getName() {
        return OLLAMA;
    }

    @Override
    public List<String> getAvailableModels(String baseUrl) {
        try {
            return OllamaModels.builder()
                    .baseUrl(baseUrl)
                    .maxRetries(1)
                    .build()
                    .availableModels()
                    .content()
                    .stream()
                    .map(OllamaModel::getModel)
                    .toList();
        } catch (Exception e) {
            log.warn("Unable to get available models.");
            return Collections.emptyList();
        }
    }

    @Override
    public LanguageModelParameters getDefaultParameters() {
        OllamaParameters p = new OllamaParameters();
        p.serverName = getName();
        p.modelName = "";
        p.baseUrl = "http://localhost:11434/";
        p.apiKey = "";
        p.maxTokens = 2048;
        p.temperature = 0.1;
        p.stopSequences = Collections.emptyList();
        p.timeout = Duration.of(10, ChronoUnit.SECONDS);

        p.topK = 40;
        p.repeatPenalty = 1.1;
        p.seed = 0;
        p.numPredict = 2048;
        p.numCtx = 8192;
        p.responseFormat = null;

        return p;
    }
}

package com.github.damiano1996.jetbrains.incoder.language.model.server.ollama;

import com.github.damiano1996.jetbrains.incoder.language.model.server.BaseLanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.*;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OllamaLanguageModelServer extends BaseLanguageModelServer {

    @Override
    public ChatLanguageModel createChatLanguageModel(LanguageModelParameters parameters) {
        return OllamaChatModel.builder()
                .baseUrl(parameters.baseUrl)
                .modelName(parameters.modelName)
                .temperature(parameters.temperature)
                .timeout(DEFAULT_TIMEOUT)
                .build();
    }

    @Override
    public StreamingChatLanguageModel createStreamingChatLanguageModel(
            LanguageModelParameters parameters) {
        return OllamaStreamingChatModel.builder()
                .baseUrl(parameters.baseUrl)
                .modelName(parameters.modelName)
                .temperature(parameters.temperature)
                .timeout(DEFAULT_TIMEOUT)
                .build();
    }

    @Override
    public String getName() {
        return "Ollama";
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
        return new LanguageModelParameters(getName(), "", "http://localhost:11434/", "", 2048, 0.1);
    }
}

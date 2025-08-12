package com.github.damiano1996.jetbrains.incoder.language.model.server.openai;

import com.github.damiano1996.jetbrains.incoder.language.model.server.BaseLanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.*;
import java.util.Arrays;
import java.util.List;

public class OpenAiLanguageModelServer extends BaseLanguageModelServer {

    @Override
    public ChatLanguageModel createChatLanguageModel(LanguageModelParameters parameters) {
        return OpenAiChatModel.builder()
                .baseUrl(parameters.baseUrl)
                .apiKey(parameters.apiKey)
                .modelName(parameters.modelName)
                .temperature(parameters.temperature)
                .maxCompletionTokens(parameters.maxTokens)
                .timeout(DEFAULT_TIMEOUT)
                .build();
    }

    @Override
    public StreamingChatLanguageModel createStreamingChatLanguageModel(
            LanguageModelParameters parameters) {
        return OpenAiStreamingChatModel.builder()
                .baseUrl(parameters.baseUrl)
                .apiKey(parameters.apiKey)
                .modelName(parameters.modelName)
                .temperature(parameters.temperature)
                .maxCompletionTokens(parameters.maxTokens)
                .timeout(DEFAULT_TIMEOUT)
                .build();
    }

    @Override
    public String getName() {
        return "Open AI";
    }

    @Override
    public List<String> getAvailableModels(String baseUrl) {
        return Arrays.stream(OpenAiChatModelName.values()).map(Enum::toString).toList();
    }

    @Override
    public LanguageModelParameters getDefaultParameters() {
        return new LanguageModelParameters(
                getName(), "", "https://api.openai.com/v1", "", 2048, 0.1);
    }
}

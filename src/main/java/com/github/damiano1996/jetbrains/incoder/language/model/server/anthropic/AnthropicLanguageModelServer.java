package com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic;

import com.github.damiano1996.jetbrains.incoder.language.model.server.BaseLanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import java.util.Arrays;
import java.util.List;

public class AnthropicLanguageModelServer extends BaseLanguageModelServer {

    @Override
    public ChatLanguageModel createChatLanguageModel(LanguageModelParameters parameters) {
        return AnthropicChatModel.builder()
                .baseUrl(parameters.getBaseUrl())
                .apiKey(parameters.getApiKey())
                .modelName(parameters.getModelName())
                .temperature(parameters.getTemperature())
                .maxTokens(parameters.getMaxTokens())
                .timeout(DEFAULT_TIMEOUT)
                .build();
    }

    @Override
    public StreamingChatLanguageModel createStreamingChatLanguageModel(
            LanguageModelParameters parameters) {
        return AnthropicStreamingChatModel.builder()
                .baseUrl(parameters.getBaseUrl())
                .apiKey(parameters.getApiKey())
                .modelName(parameters.getModelName())
                .temperature(parameters.getTemperature())
                .maxTokens(parameters.getMaxTokens())
                .timeout(DEFAULT_TIMEOUT)
                .build();
    }

    @Override
    public String getName() {
        return "Anthropic";
    }

    @Override
    public List<String> getAvailableModels(String baseUrl) {
        return Arrays.stream(AnthropicChatModelName.values()).map(Enum::toString).toList();
    }

    @Override
    public LanguageModelParameters getDefaultParameters() {
        return new LanguageModelParameters(
                getName(), "", "https://api.anthropic.com/v1/", "", 64000, 0.1);
    }
}

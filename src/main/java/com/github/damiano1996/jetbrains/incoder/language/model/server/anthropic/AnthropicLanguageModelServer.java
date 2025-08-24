package com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic;

import com.github.damiano1996.jetbrains.incoder.language.model.server.BaseLanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AnthropicLanguageModelServer extends BaseLanguageModelServer {

    public static final String ANTHROPIC = "Anthropic";

    @Override
    public ChatModel createChatLanguageModel(LanguageModelParameters parameters) {
        AnthropicParameters p = (AnthropicParameters) parameters;
        return AnthropicChatModel.builder()
                .baseUrl(p.baseUrl)
                .apiKey(p.apiKey)
                .version(p.version)
                .modelName(p.modelName)
                .temperature(p.temperature)
                .topK(p.topK)
                .maxTokens(p.maxTokens)
                .stopSequences(p.stopSequences)
                .cacheSystemMessages(Boolean.TRUE.equals(p.cacheSystemMessages))
                .cacheTools(Boolean.TRUE.equals(p.cacheTools))
                .timeout(Duration.of(p.timeout, ChronoUnit.SECONDS))
                .build();
    }

    @Override
    public StreamingChatModel createStreamingChatLanguageModel(LanguageModelParameters parameters) {
        AnthropicParameters p = (AnthropicParameters) parameters;
        return AnthropicStreamingChatModel.builder()
                .baseUrl(p.baseUrl)
                .apiKey(p.apiKey)
                .version(p.version)
                .modelName(p.modelName)
                .temperature(p.temperature)
                .topK(p.topK)
                .maxTokens(p.maxTokens)
                .stopSequences(p.stopSequences)
                .cacheSystemMessages(Boolean.TRUE.equals(p.cacheSystemMessages))
                .cacheTools(Boolean.TRUE.equals(p.cacheTools))
                .timeout(Duration.of(p.timeout, ChronoUnit.SECONDS))
                .build();
    }

    @Override
    public String getName() {
        return ANTHROPIC;
    }

    @Override
    public List<String> getAvailableModels(String baseUrl) {
        return Arrays.stream(AnthropicChatModelName.values()).map(Enum::toString).toList();
    }

    @Override
    public LanguageModelParameters getDefaultParameters() {
        AnthropicParameters p = new AnthropicParameters();
        p.serverName = getName();
        p.modelName = "";
        p.baseUrl = "https://api.anthropic.com/v1/";
        p.apiKey = "";
        p.maxTokens = 64000;
        p.temperature = 0.1;
        p.topK = 40;
        p.stopSequences = Collections.emptyList();
        p.timeout = 10;
        p.version = "2023-06-01";
        p.cacheSystemMessages = false;
        p.cacheTools = false;
        return p;
    }
}

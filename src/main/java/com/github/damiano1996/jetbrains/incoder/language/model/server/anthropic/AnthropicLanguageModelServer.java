package com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic;

import com.github.damiano1996.jetbrains.incoder.language.model.server.BaseLanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic.settings.AnthropicSettings;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import java.util.Arrays;
import java.util.List;

public class AnthropicLanguageModelServer extends BaseLanguageModelServer {

    private static AnthropicSettings.State getState() {
        return AnthropicSettings.getInstance().getState();
    }

    @Override
    public ChatModel createChatModel() {
        return AnthropicChatModel.builder()
                .apiKey(getState().apiKey)
                .modelName(getState().modelName)
                .temperature(getState().temperature)
                .build();
    }

    @Override
    public StreamingChatModel createStreamingChatModel() {
        return AnthropicStreamingChatModel.builder()
                .apiKey(getState().apiKey)
                .modelName(getState().modelName)
                .temperature(getState().temperature)
                .build();
    }

    @Override
    public String getName() {
        return "Anthropic";
    }

    @Override
    public List<String> getAvailableModels() {
        return Arrays.stream(AnthropicChatModelName.values()).map(Enum::toString).toList();
    }

    @Override
    public String getSelectedModelName() {
        return getState().modelName;
    }
}

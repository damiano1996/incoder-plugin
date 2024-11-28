package com.github.damiano1996.intellijplugin.incoder.language.model.server.anthropic;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.BaseLanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.anthropic.settings.AnthropicSettings;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaModel;
import dev.langchain4j.model.ollama.OllamaModels;

import java.util.Arrays;
import java.util.List;

public class AnthropicLanguageModelServer extends BaseLanguageModelServer {

    private static AnthropicSettings.State getState() {
        return AnthropicSettings.getInstance().getState();
    }

    @Override
    public ChatLanguageModel createChatLanguageModel() {
        return AnthropicChatModel.builder()
                .baseUrl(getState().apiKey)
                .modelName(getState().modelName)
                .temperature(getState().temperature)
                .build();
    }

    @Override
    public StreamingChatLanguageModel createStreamingChatLanguageModel() {
        return AnthropicStreamingChatModel.builder()
                .baseUrl(getState().apiKey)
                .modelName(getState().modelName)
                .temperature(getState().temperature)
                .build();
    }

    @Override
    public List<String> getAvailableModels() throws LanguageModelException {
        try {
            return Arrays.stream(AnthropicChatModelName.values()).map(Enum::name).toList();
        } catch (Exception e) {
            throw new LanguageModelException(e);
        }
    }

    @Override
    public String getSelectedModelName() {
        return getState().modelName;
    }
}

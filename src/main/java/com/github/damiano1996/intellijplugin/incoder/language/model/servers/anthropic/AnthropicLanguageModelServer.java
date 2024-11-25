package com.github.damiano1996.intellijplugin.incoder.language.model.servers.anthropic;

import com.github.damiano1996.intellijplugin.incoder.language.model.servers.BaseLanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.servers.anthropic.settings.AnthropicSettings;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import org.apache.commons.lang.NotImplementedException;

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
    public boolean isHealthy() {
        throw new NotImplementedException();
    }
}

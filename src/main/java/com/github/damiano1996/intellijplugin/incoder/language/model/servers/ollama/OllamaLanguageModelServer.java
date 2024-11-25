package com.github.damiano1996.intellijplugin.incoder.language.model.servers.ollama;

import com.github.damiano1996.intellijplugin.incoder.language.model.servers.BaseLanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.servers.ollama.settings.OllamaSettings;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import org.apache.commons.lang.NotImplementedException;

public class OllamaLanguageModelServer extends BaseLanguageModelServer {

    private static OllamaSettings.State getState() {
        return OllamaSettings.getInstance().getState();
    }

    @Override
    public ChatLanguageModel createChatLanguageModel() {
        return OllamaChatModel.builder()
                .baseUrl(getState().baseUrl)
                .modelName(getState().modelName)
                .temperature(getState().temperature)
                .build();

    }

    @Override
    public StreamingChatLanguageModel createStreamingChatLanguageModel() {
        return OllamaStreamingChatModel.builder()
                .baseUrl(getState().baseUrl)
                .modelName(getState().modelName)
                .temperature(getState().temperature)
                .build();

    }

    @Override
    public boolean isHealthy() {
        throw new NotImplementedException();
    }
}

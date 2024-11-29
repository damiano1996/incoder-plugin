package com.github.damiano1996.intellijplugin.incoder.language.model.server.ollama;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.BaseLanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.ollama.settings.OllamaSettings;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.*;
import java.util.List;

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
    public List<String> getAvailableModels() {
            return OllamaModels.builder()
                    .baseUrl(getState().baseUrl)
                    .build()
                    .availableModels()
                    .content()
                    .stream()
                    .map(OllamaModel::getModel)
                    .toList();

    }

    @Override
    public String getSelectedModelName() {
        return getState().modelName;
    }
}

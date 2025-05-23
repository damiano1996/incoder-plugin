package com.github.damiano1996.jetbrains.incoder.language.model.server.ollama;

import com.github.damiano1996.jetbrains.incoder.language.model.server.BaseLanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ollama.settings.OllamaSettings;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.ollama.*;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class OllamaLanguageModelServer extends BaseLanguageModelServer {

    private static OllamaSettings.@NotNull State getState() {
        return OllamaSettings.getInstance().getState();
    }

    @Override
    public ChatModel createChatModel() {
        return OllamaChatModel.builder()
                .baseUrl(getState().baseUrl)
                .modelName(getState().modelName)
                .temperature(getState().temperature)
                .build();
    }

    @Override
    public StreamingChatModel createStreamingChatModel() {
        return OllamaStreamingChatModel.builder()
                .baseUrl(getState().baseUrl)
                .modelName(getState().modelName)
                .temperature(getState().temperature)
                .build();
    }

    @Override
    public String getName() {
        return "Ollama";
    }

    @Override
    public List<String> getAvailableModels() {
        return getAvailableModels(getState().baseUrl);
    }

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
    public String getSelectedModelName() {
        return getState().modelName;
    }
}

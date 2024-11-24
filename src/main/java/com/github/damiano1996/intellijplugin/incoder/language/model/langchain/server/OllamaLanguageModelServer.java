package com.github.damiano1996.intellijplugin.incoder.language.model.langchain.server;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelClientImpl;
import com.github.damiano1996.intellijplugin.incoder.language.model.langchain.LangChainLanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.langchain.settings.LangChainSettings;
import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelClient;
import com.github.damiano1996.intellijplugin.incoder.language.model.ServerException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OllamaLanguageModelServer extends LangChainLanguageModelServer {

    private static LangChainSettings.@NotNull State getSettingsState() {
        return Objects.requireNonNull(
                LangChainSettings.getInstance().getState(), "LangChain settings must be defined.");
    }

    @Override
    public ChatLanguageModel createChatLanguageModel() {
        return OllamaChatModel.builder()
                .baseUrl(getSettingsState().ollamaState.baseUrl)
                .modelName(getSettingsState().ollamaState.modelName)
                .temperature(getSettingsState().ollamaState.temperature)
                .build();

    }

    @Override
    public StreamingChatLanguageModel createStreamingChatLanguageModel() {
        return OllamaStreamingChatModel.builder()
                .baseUrl(getSettingsState().ollamaState.baseUrl)
                .modelName(getSettingsState().ollamaState.modelName)
                .temperature(getSettingsState().ollamaState.temperature)
                .build();

    }

    @Override
    public boolean isHealthy() {
        throw new NotImplementedException();
    }

    @Override
    public LanguageModelClient createClient() throws ServerException {
        return new LanguageModelClientImpl(createChatLanguageModel(), createStreamingChatLanguageModel());
    }
}

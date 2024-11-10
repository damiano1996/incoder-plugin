package com.github.damiano1996.intellijplugin.incoder.llm.langchain.server;

import com.github.damiano1996.intellijplugin.incoder.initializable.InitializableException;
import com.github.damiano1996.intellijplugin.incoder.initializable.InitializableListener;
import com.github.damiano1996.intellijplugin.incoder.llm.LlmClient;
import com.github.damiano1996.intellijplugin.incoder.llm.langchain.client.LangChainLlmClient;
import com.github.damiano1996.intellijplugin.incoder.llm.langchain.server.settings.LangChainSettings;
import com.github.damiano1996.intellijplugin.incoder.llm.server.LlmServer;
import com.github.damiano1996.intellijplugin.incoder.llm.server.ServerException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class LangChainLlmServer implements LlmServer {

    private final List<InitializableListener> listeners = new ArrayList<>();

    private static LangChainSettings.@NotNull State getSettingsState() {
        return Objects.requireNonNull(
                LangChainSettings.getInstance().getState(), "LangChain settings must be defined.");
    }

    @Override
    public void subscribe(InitializableListener listener) {
        listeners.add(listener);
    }

    @Override
    public void init() throws InitializableException {}

    @Override
    public void close() throws InitializableException {}

    public ChatLanguageModel createModel() {
        //noinspection SwitchStatementWithTooFewBranches
        switch (getSettingsState().modelType) {
            case OLLAMA -> {
                return OllamaChatModel.builder()
                        .baseUrl(getSettingsState().ollamaState.baseUrl)
                        .modelName(getSettingsState().ollamaState.modelName)
                        .temperature(getSettingsState().ollamaState.temperature)
                        .build();
            }
            default -> throw new IllegalStateException(
                    "Unexpected value: " + getSettingsState().modelType);
        }
    }

    @Override
    public LlmClient createClient() throws ServerException {
        return new LangChainLlmClient(createModel());
    }
}

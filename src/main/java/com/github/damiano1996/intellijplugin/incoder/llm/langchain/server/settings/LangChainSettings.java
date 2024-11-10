package com.github.damiano1996.intellijplugin.incoder.llm.langchain.server.settings;

import com.github.damiano1996.intellijplugin.incoder.llm.langchain.server.LangChainModelType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Service(Service.Level.APP)
@State(
        name = "LangChainSettings",
        storages = {@Storage("InCoderSettings.xml")})
public final class LangChainSettings implements PersistentStateComponent<LangChainSettings.State> {

    private State state = new State();

    public static LangChainSettings getInstance() {
        return ApplicationManager.getApplication().getService(LangChainSettings.class);
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    @ToString
    public static class State {

        public LangChainModelType modelType = LangChainModelType.OLLAMA;

        public OllamaState ollamaState = new OllamaState();

        @ToString
        public static class OllamaState {
            public String baseUrl = "http://localhost:11434/";
            public String modelName = "llama3.1";
            public Double temperature = 0.2;
        }
    }
}

package com.github.damiano1996.intellijplugin.incoder.language.model.servers.ollama.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@Service(Service.Level.APP)
@State(
        name = "OllamaSettings",
        storages = {@Storage("InCoderSettings.xml")})
public final class OllamaSettings implements PersistentStateComponent<OllamaSettings.State> {

    private State state = new State();

    public static OllamaSettings getInstance() {
        return ApplicationManager.getApplication().getService(OllamaSettings.class);
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    @ToString
    public static class State {

        public String baseUrl = "http://localhost:11434/";
        public String modelName = "";
        public Double temperature = 0.2;

    }
}

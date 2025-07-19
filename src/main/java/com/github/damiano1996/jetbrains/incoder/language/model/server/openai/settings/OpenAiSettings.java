package com.github.damiano1996.jetbrains.incoder.language.model.server.openai.settings;

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
        name = "OpenAiSettings",
        storages = {@Storage("InCoderSettings.xml")})
public final class OpenAiSettings implements PersistentStateComponent<OpenAiSettings.State> {

    private State state = new State();

    public static OpenAiSettings getInstance() {
        return ApplicationManager.getApplication().getService(OpenAiSettings.class);
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    @ToString
    public static class State {

        public String baseUrl = "https://api.openai.com/v1";
        public String apiKey = "";
        public String modelName = "";
        public Double temperature = 0.2;
        public Integer maxTokens = 2048;
    }
}

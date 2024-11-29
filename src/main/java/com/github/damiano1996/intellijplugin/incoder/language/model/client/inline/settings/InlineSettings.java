package com.github.damiano1996.intellijplugin.incoder.language.model.client.inline.settings;

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
        name = "InlineSettings",
        storages = {@Storage("InCoderSettings.xml")})
public final class InlineSettings implements PersistentStateComponent<InlineSettings.State> {

    @NotNull private State state = new State();

    public static InlineSettings getInstance() {
        return ApplicationManager.getApplication().getService(InlineSettings.class);
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    @ToString
    public static class State {
        public boolean enable = true;

        public String systemMessageInstructions = """
                    - What is the single line of missing code between these contexts?
                    - Respond only with the necessary code to complete the line.
                    - Do not include markdown, comments, or additional explanations.
                    - If the line is partially written, complete it up to the first newline.
                    - If the line is already at the end, return an empty response.
                    """;
    }
}

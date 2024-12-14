package com.github.damiano1996.jetbrains.incoder.language.model.client.doc.settings;

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
        name = "DocumentationSettings",
        storages = {@Storage("InCoderSettings.xml")})
public final class DocumentationSettings
        implements PersistentStateComponent<DocumentationSettings.State> {

    @NotNull private State state = new State();

    public static DocumentationSettings getInstance() {
        return ApplicationManager.getApplication().getService(DocumentationSettings.class);
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    @ToString
    public static class State {
        public String documentationInstructions =
                """
                - Analyze the following code and add clear, concise documentation in the appropriate style for the language.
                - Include descriptions for all functions, methods, classes, parameters, return values, and any complex or non-obvious logic.
                - Ensure the documentation explains the purpose, inputs, outputs, and behavior of the code.
                """;
    }
}

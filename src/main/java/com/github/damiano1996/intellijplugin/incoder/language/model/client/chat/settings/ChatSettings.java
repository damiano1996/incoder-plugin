package com.github.damiano1996.intellijplugin.incoder.language.model.client.chat.settings;

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
        name = "ChatSettings",
        storages = {@Storage("InCoderSettings.xml")})
public final class ChatSettings implements PersistentStateComponent<ChatSettings.State> {

    @NotNull private State state = new State();

    public static ChatSettings getInstance() {
        return ApplicationManager.getApplication().getService(ChatSettings.class);
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    @ToString
    public static class State {
        public int maxMessages = 10;

        public String systemMessageInstructionsWithCode =
                """
                    - You are an AI assistant integrated into a JetBrains plugin, providing expert coding assistance and development support directly within the IDE.
                    - If the user input pertains to the provided code, respond with the code edited according to the user's instructions.
                    """;

        public String systemMessageInstructions =
                """
                    - You are an AI assistant integrated into a JetBrains plugin, providing expert coding assistance and development support directly within the IDE.
                    """;
    }
}

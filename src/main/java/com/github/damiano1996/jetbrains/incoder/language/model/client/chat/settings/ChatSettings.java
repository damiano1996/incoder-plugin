package com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
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
        public String serverName = "";
        public int maxMessages = 20;
        public String systemMessageInstructions = loadDefaultSystemPrompt();
        public boolean enableTools = true;

        public static @NotNull String loadDefaultSystemPrompt() {
            try (InputStream inputStream =
                    State.class.getClassLoader().getResourceAsStream("prompts/system_prompt.txt")) {
                if (inputStream != null) {
                    return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                }
            } catch (IOException e) {
                log.error("Unable to read system prompt.", e);
            }
            return "";
        }
    }
}

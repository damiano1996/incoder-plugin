package com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic.AnthropicParameters;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ollama.OllamaParameters;
import com.github.damiano1996.jetbrains.incoder.language.model.server.openai.OpenAiParameters;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.XCollection;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
        @XCollection(
                elementTypes = {
                    AnthropicParameters.class,
                    OllamaParameters.class,
                    OpenAiParameters.class
                },
                style = XCollection.Style.v2)
        public List<LanguageModelParameters> defaultLanguageModelParameters = new ArrayList<>();

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

        public LanguageModelParameters getDefaultLanguageModelParameters() {
            return defaultLanguageModelParameters.isEmpty()
                    ? null
                    : defaultLanguageModelParameters.get(0);
        }

        public void setDefaultLanguageModelParameters(LanguageModelParameters parameters) {
            defaultLanguageModelParameters.clear();
            if (parameters != null) {
                defaultLanguageModelParameters.add(parameters);
            }
        }
    }
}

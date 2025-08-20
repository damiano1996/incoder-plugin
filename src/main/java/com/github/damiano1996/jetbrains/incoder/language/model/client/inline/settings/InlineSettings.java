package com.github.damiano1996.jetbrains.incoder.language.model.client.inline.settings;

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
import java.util.ArrayList;
import java.util.List;
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
        @XCollection(
                elementTypes = {
                    AnthropicParameters.class,
                    OllamaParameters.class,
                    OpenAiParameters.class
                },
                style = XCollection.Style.v2)
        public List<LanguageModelParameters> selectedLanguageModelParameters = new ArrayList<>();

        public boolean enable = false;
        public boolean triggerEndLine = true;

        public String systemMessageInstructions =
                """
                You are an inline code completion assistant.
                Follow these rules strictly:
                1. Output only the minimal code needed to finish the current line.
                2. Do not include markdown, comments, or extra tokens.
                3. If the line is partially written, complete it up to the next newline.
                4. If the line is already complete, output nothing.
                """;

        public LanguageModelParameters getSelectedLanguageModelParameters() {
            return selectedLanguageModelParameters.isEmpty()
                    ? null
                    : selectedLanguageModelParameters.get(0);
        }

        public void setSelectedLanguageModelParameters(LanguageModelParameters parameters) {
            selectedLanguageModelParameters.clear();
            if (parameters != null) {
                selectedLanguageModelParameters.add(parameters);
            }
        }
    }
}

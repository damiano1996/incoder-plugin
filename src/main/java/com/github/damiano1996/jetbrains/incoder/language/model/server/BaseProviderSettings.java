package com.github.damiano1996.jetbrains.incoder.language.model.server;

import com.intellij.openapi.components.PersistentStateComponent;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseProviderSettings<T extends ChatLanguageModel>
        implements PersistentStateComponent<ChatRequestParameters> {

    private ChatRequestParameters chatRequestParameters;

    public BaseProviderSettings(T t) {
        chatRequestParameters = t.defaultRequestParameters();
    }

    @Override
    public @Nullable ChatRequestParameters getState() {
        return chatRequestParameters;
    }

    @Override
    public void loadState(@NotNull ChatRequestParameters chatRequestParameters) {
        this.chatRequestParameters = chatRequestParameters;
    }
}

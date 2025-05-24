package com.github.damiano1996.jetbrains.incoder.language.model.server;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.client.LanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.LanguageModelClientImpl;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings.ChatSettings;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Slf4j
public abstract class BaseLanguageModelServer implements LanguageModelServer {

    public abstract ChatModel createChatModel();

    public abstract StreamingChatModel createStreamingChatModel();

    @Contract(" -> new")
    @Override
    public @NotNull LanguageModelClient createClient() throws LanguageModelException {
        try {
            return new LanguageModelClientImpl(createChatModel(), createStreamingChatModel());
        } catch (Exception e) {
            throw new LanguageModelException(
                    ("Unable to create the client for %s.\n" + "%s")
                            .formatted(getName(), e.getMessage()),
                    e);
        }
    }

    protected ChatMemory createChatMemory() {
        return MessageWindowChatMemory.withMaxMessages(
                ChatSettings.getInstance().getState().maxMessages);
    }
}

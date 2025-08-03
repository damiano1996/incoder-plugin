package com.github.damiano1996.jetbrains.incoder.language.model.server;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.ChatLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.ChatLanguageModelClientImpl;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.InlineLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.InlineLanguageModelClientImpl;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Slf4j
public abstract class BaseLanguageModelServer implements LanguageModelServer {

    public static final Duration TIMEOUT = Duration.of(10, ChronoUnit.SECONDS);

    public abstract String getModelName();

    public abstract ChatLanguageModel createChatLanguageModel();

    public abstract StreamingChatLanguageModel createStreamingChatLanguageModel();

    @Contract(" -> new")
    @Override
    public @NotNull InlineLanguageModelClient createInlineClient() throws LanguageModelException {
        try {
            return new InlineLanguageModelClientImpl(
                    getModelName(), createChatLanguageModel(), createStreamingChatLanguageModel());
        } catch (Exception e) {
            throw new LanguageModelException(
                    ("Unable to create the inline client for %s.\n%s")
                            .formatted(getName(), e.getMessage()),
                    e);
        }
    }

    @Contract(" -> new")
    @Override
    public @NotNull ChatLanguageModelClient createChatClient() throws LanguageModelException {
        try {
            return new ChatLanguageModelClientImpl(
                    getModelName(), createChatLanguageModel(), createStreamingChatLanguageModel());
        } catch (Exception e) {
            throw new LanguageModelException(
                    ("Unable to create the chat client for %s.\n%s")
                            .formatted(getName(), e.getMessage()),
                    e);
        }
    }
}

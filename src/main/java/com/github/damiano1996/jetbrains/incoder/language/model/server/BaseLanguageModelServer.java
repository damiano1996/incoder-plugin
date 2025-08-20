package com.github.damiano1996.jetbrains.incoder.language.model.server;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.ChatLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.ChatLanguageModelClientImpl;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.InlineLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.InlineLanguageModelClientImpl;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Slf4j
public abstract class BaseLanguageModelServer implements LanguageModelServer {

    public abstract ChatLanguageModel createChatLanguageModel(LanguageModelParameters parameters);

    public abstract StreamingChatLanguageModel createStreamingChatLanguageModel(
            LanguageModelParameters parameters);

    @Contract("_ -> new")
    @Override
    public @NotNull InlineLanguageModelClient createInlineClient(LanguageModelParameters parameters)
            throws LanguageModelException {
        try {
            return new InlineLanguageModelClientImpl(
                    parameters, createChatLanguageModel(parameters));
        } catch (Exception e) {
            throw new LanguageModelException(
                    ("Unable to create the inline client for %s.\n%s")
                            .formatted(getName(), e.getMessage()),
                    e);
        }
    }

    @Contract("_ -> new")
    @Override
    public @NotNull ChatLanguageModelClient createChatClient(LanguageModelParameters parameters)
            throws LanguageModelException {
        try {
            return new ChatLanguageModelClientImpl(
                    parameters, createStreamingChatLanguageModel(parameters));
        } catch (Exception e) {
            throw new LanguageModelException(
                    ("Unable to create the chat client for %s.\n%s")
                            .formatted(getName(), e.getMessage()),
                    e);
        }
    }

    @Override
    public void verify(LanguageModelParameters parameters) throws LanguageModelException {
        try {
            createChatLanguageModel(parameters).chat("Say OK. Nothing else");
        } catch (Exception e) {
            throw new LanguageModelException(
                    "Parameters are not valid. Error from %s: %s"
                            .formatted(getName(), e.getMessage()),
                    e);
        }
    }
}

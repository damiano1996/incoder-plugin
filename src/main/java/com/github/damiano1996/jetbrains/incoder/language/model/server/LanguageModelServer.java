package com.github.damiano1996.jetbrains.incoder.language.model.server;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.ChatLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.InlineLanguageModelClient;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface LanguageModelServer {

    String getName();

    List<String> getAvailableModels(String baseUrl);

    LanguageModelParameters getDefaultParameters();

    @NotNull
    InlineLanguageModelClient createInlineClient(LanguageModelParameters parameters)
            throws LanguageModelException;

    @NotNull
    ChatLanguageModelClient createChatClient(LanguageModelParameters parameters)
            throws LanguageModelException;

    void verify(LanguageModelParameters parameters) throws LanguageModelException;
}

package com.github.damiano1996.jetbrains.incoder.language.model.server;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.ChatLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.InlineLanguageModelClient;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface LanguageModelServer {

    String getName();

    List<String> getAvailableModels();

    @NotNull
    InlineLanguageModelClient createInlineClient() throws LanguageModelException;

    @NotNull
    ChatLanguageModelClient createChatClient() throws LanguageModelException;
}

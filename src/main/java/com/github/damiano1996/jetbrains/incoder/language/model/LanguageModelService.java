package com.github.damiano1996.jetbrains.incoder.language.model;

import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.ChatLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings.ChatSettings;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.InlineLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.settings.InlineSettings;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import com.intellij.openapi.util.ThrowableComputable;

import java.util.Set;

public interface LanguageModelService {

    Set<String> getAvailableServerNames();


    ThrowableComputable<ChatLanguageModelClient, LanguageModelException> createChatClient(ChatSettings.State settings, LanguageModelParameters parameters);

    ThrowableComputable<ChatLanguageModelClient, LanguageModelException> createChatClientWithDefaultSettings(LanguageModelParameters parameters);

    ThrowableComputable<InlineLanguageModelClient, LanguageModelException> createInlineClient(InlineSettings.State settings, LanguageModelParameters parameters);

    ThrowableComputable<InlineLanguageModelClient, LanguageModelException> createInlineClientWithDefaultSettings(LanguageModelParameters parameters);

    void verify(LanguageModelParameters parameters) throws LanguageModelException;
}

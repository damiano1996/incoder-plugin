package com.github.damiano1996.jetbrains.incoder.language.model;

import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.ChatLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings.ChatSettings;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.InlineLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.settings.InlineSettings;
import java.util.Set;

public interface LanguageModelService {

    Set<String> getAvailableServerNames();

    LanguageModelService with(InlineSettings.State settings) throws LanguageModelException;

    LanguageModelService with(ChatSettings.State settings) throws LanguageModelException;

    ChatLanguageModelClient getOrCreateChatClient() throws LanguageModelException;

    InlineLanguageModelClient getOrCreateInlineClient() throws LanguageModelException;
}

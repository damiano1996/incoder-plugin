package com.github.damiano1996.jetbrains.incoder.language.model.client;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.ChatCodingAssistant;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.InlineCodingAssistant;

public interface LanguageModelClient
        extends ChatCodingAssistant,
        InlineCodingAssistant {

    /**
     * Checks whether the connection is healthy.
     *
     * @throws LanguageModelException if the connection or settings are unhealthy.
     */
    void checkServerConnection() throws LanguageModelException;
}

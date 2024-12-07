package com.github.damiano1996.jetbrains.incoder.language.model.server;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;

public interface ServerFactory {

    default String getName() {
        try {
            return createServer().getName();
        } catch (LanguageModelException e) {
            throw new IllegalStateException("Name of the server must be implemented.", e);
        }
    }

    LanguageModelServer createServer() throws LanguageModelException;
}

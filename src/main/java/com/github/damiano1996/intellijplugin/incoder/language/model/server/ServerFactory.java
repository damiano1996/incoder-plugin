package com.github.damiano1996.intellijplugin.incoder.language.model.server;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;

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

package com.github.damiano1996.jetbrains.incoder.language.model;

import com.github.damiano1996.jetbrains.incoder.language.model.client.LanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelServer;
import org.jetbrains.annotations.NotNull;

public interface LanguageModelService {

    void init() throws LanguageModelException;

    void init(LanguageModelServer server) throws LanguageModelException;

    boolean isReady();

    String getSelectedModelName() throws LanguageModelException, IllegalStateException;

    @NotNull
    LanguageModelClient getClient() throws IllegalStateException;
}

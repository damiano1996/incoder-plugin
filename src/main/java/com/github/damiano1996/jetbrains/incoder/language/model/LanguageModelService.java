package com.github.damiano1996.jetbrains.incoder.language.model;

import com.github.damiano1996.jetbrains.incoder.language.model.client.LanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelServer;
import org.jetbrains.annotations.NotNull;

public interface LanguageModelService {

    void startWithDefaultServer() throws LanguageModelException;

    void startWith(LanguageModelServer server) throws LanguageModelException;

    String getSelectedModelName() throws LanguageModelException;

    @NotNull
    LanguageModelClient getClient() throws LanguageModelException;
}

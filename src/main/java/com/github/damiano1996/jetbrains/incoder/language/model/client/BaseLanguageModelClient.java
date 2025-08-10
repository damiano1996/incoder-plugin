package com.github.damiano1996.jetbrains.incoder.language.model.client;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;

public abstract class BaseLanguageModelClient implements LanguageModelClient {

    private final LanguageModelParameters parameters;

    protected BaseLanguageModelClient(LanguageModelParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public LanguageModelParameters getParameters() {
        return parameters;
    }
}

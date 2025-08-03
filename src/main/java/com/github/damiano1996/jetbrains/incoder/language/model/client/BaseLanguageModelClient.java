package com.github.damiano1996.jetbrains.incoder.language.model.client;

public abstract class BaseLanguageModelClient implements LanguageModelClient {

    private final String modelName;

    protected BaseLanguageModelClient(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public String getModelName() {
        return modelName;
    }
}

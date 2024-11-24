package com.github.damiano1996.intellijplugin.incoder.language.model;

import com.github.damiano1996.intellijplugin.incoder.language.model.langchain.settings.LangChainSettings;
import com.github.damiano1996.intellijplugin.incoder.language.model.langchain.server.OllamaLanguageModelServer;

import java.util.Objects;

public class LanguageModelServerFactoryImpl implements LanguageModelServerFactory {

    @Override
    public LanguageModelServer createServer(LangChainSettings settings) {
        switch (Objects.requireNonNull(settings.getState(), "LLM settings state must be defined.").modelType) {
            case OLLAMA -> {
                return new OllamaLanguageModelServer();
            }
            default -> throw new IllegalStateException(
                    "Unexpected value: " + settings.getState().modelType);
        }
    }
}

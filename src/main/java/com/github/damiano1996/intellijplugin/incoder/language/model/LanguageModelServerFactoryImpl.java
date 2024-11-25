package com.github.damiano1996.intellijplugin.incoder.language.model;

import com.github.damiano1996.intellijplugin.incoder.language.model.settings.ServerSettings;
import com.github.damiano1996.intellijplugin.incoder.language.model.servers.ollama.OllamaLanguageModelServer;

import java.util.Objects;

public class LanguageModelServerFactoryImpl implements LanguageModelServerFactory {

    @Override
    public LanguageModelServer createServer(ServerSettings settings) {
        switch (Objects.requireNonNull(settings.getState(), "LLM settings state must be defined.").modelType) {
            case OLLAMA -> {
                return new OllamaLanguageModelServer();
            }
            default -> throw new IllegalStateException(
                    "Unexpected value: " + settings.getState().modelType);
        }
    }
}

package com.github.damiano1996.jetbrains.incoder.language.model.server.ollama;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactory;
import com.github.damiano1996.jetbrains.incoder.language.model.server.settings.ui.ProviderUIStrategy;

public class OllamaFactory implements ServerFactory {

    @Override
    public LanguageModelServer createServer() {
        return new OllamaLanguageModelServer();
    }

    @Override
    public ProviderUIStrategy createProviderUIStrategy() {
        return new OllamaUIStrategy();
    }
}

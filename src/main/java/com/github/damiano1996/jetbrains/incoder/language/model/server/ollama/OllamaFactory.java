package com.github.damiano1996.jetbrains.incoder.language.model.server.ollama;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactory;

public class OllamaFactory implements ServerFactory {

    @Override
    public LanguageModelServer createServer() {
        return new OllamaLanguageModelServer();
    }
}

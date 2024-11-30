package com.github.damiano1996.intellijplugin.incoder.language.model.server.ollama;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.ServerFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.ollama.settings.OllamaConfigurable;
import com.intellij.openapi.options.Configurable;

public class OllamaFactory implements ServerFactory {
    @Override
    public Configurable createConfigurable() {
        return new OllamaConfigurable();
    }

    @Override
    public LanguageModelServer createServer() {
        return new OllamaLanguageModelServer();
    }

}

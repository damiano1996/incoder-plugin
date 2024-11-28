package com.github.damiano1996.intellijplugin.incoder.language.model.server.ollama;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.ollama.settings.OllamaConfigurable;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.settings.ServerAbstractFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.settings.ServerConfigurable;

public class OllamaAbstractFactory implements ServerAbstractFactory {
    @Override
    public LanguageModelServer createServer() {
        return new OllamaLanguageModelServer();
    }

    @Override
    public ServerConfigurable createConfigurable() {
        return new OllamaConfigurable();
    }
}

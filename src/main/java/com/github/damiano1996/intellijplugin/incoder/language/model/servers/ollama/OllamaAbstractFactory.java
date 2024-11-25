package com.github.damiano1996.intellijplugin.incoder.language.model.servers.ollama;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.servers.ollama.settings.OllamaConfigurable;
import com.github.damiano1996.intellijplugin.incoder.language.model.settings.ServerAbstractFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.settings.ServerConfigurable;

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

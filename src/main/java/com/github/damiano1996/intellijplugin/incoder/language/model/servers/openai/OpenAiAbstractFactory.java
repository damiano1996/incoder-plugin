package com.github.damiano1996.intellijplugin.incoder.language.model.servers.openai;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.servers.openai.settings.OpenAiConfigurable;
import com.github.damiano1996.intellijplugin.incoder.language.model.settings.ServerAbstractFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.settings.ServerConfigurable;

public class OpenAiAbstractFactory implements ServerAbstractFactory {
    @Override
    public LanguageModelServer createServer() {
        return new OpenAiLanguageModelServer();
    }

    @Override
    public ServerConfigurable createConfigurable() {
        return new OpenAiConfigurable();
    }
}

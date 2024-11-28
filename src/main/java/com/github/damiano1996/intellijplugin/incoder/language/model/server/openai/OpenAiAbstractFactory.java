package com.github.damiano1996.intellijplugin.incoder.language.model.server.openai;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.openai.settings.OpenAiConfigurable;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.settings.ServerAbstractFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.settings.ServerConfigurable;

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

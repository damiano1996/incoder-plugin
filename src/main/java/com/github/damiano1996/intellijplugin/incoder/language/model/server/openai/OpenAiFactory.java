package com.github.damiano1996.intellijplugin.incoder.language.model.server.openai;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.ServerFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.openai.settings.OpenAiConfigurable;
import com.intellij.openapi.options.Configurable;

public class OpenAiFactory implements ServerFactory {
    @Override
    public Configurable createConfigurable() {
        return new OpenAiConfigurable();
    }

    @Override
    public LanguageModelServer createServer() {
        return new OpenAiLanguageModelServer();
    }

}

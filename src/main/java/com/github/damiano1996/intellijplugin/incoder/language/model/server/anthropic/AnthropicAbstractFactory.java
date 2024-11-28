package com.github.damiano1996.intellijplugin.incoder.language.model.server.anthropic;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.anthropic.settings.AnthropicConfigurable;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.settings.ServerAbstractFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.settings.ServerConfigurable;

public class AnthropicAbstractFactory implements ServerAbstractFactory {
    @Override
    public LanguageModelServer createServer() {
        return new AnthropicLanguageModelServer();
    }

    @Override
    public ServerConfigurable createConfigurable() {
        return new AnthropicConfigurable();
    }
}

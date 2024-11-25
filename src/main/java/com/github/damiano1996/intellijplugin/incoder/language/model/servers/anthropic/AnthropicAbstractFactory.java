package com.github.damiano1996.intellijplugin.incoder.language.model.servers.anthropic;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.servers.anthropic.settings.AnthropicConfigurable;
import com.github.damiano1996.intellijplugin.incoder.language.model.settings.ServerAbstractFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.settings.ServerConfigurable;

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

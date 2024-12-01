package com.github.damiano1996.intellijplugin.incoder.language.model.server.anthropic;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.ServerFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.anthropic.settings.AnthropicConfigurable;
import com.intellij.openapi.options.Configurable;

public class AnthropicFactory implements ServerFactory {
    @Override
    public String getName() {
        return "Anthropic";
    }

    @Override
    public Configurable createConfigurable() {
        return new AnthropicConfigurable();
    }

    @Override
    public LanguageModelServer createServer() {
        return new AnthropicLanguageModelServer();
    }
}

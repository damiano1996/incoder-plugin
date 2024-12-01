package com.github.damiano1996.intellijplugin.incoder.language.model.server.anthropic;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.ServerFactory;

public class AnthropicFactory implements ServerFactory {
    @Override
    public String getName() {
        return "Anthropic";
    }

    @Override
    public LanguageModelServer createServer() {
        return new AnthropicLanguageModelServer();
    }
}

package com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactory;
import com.github.damiano1996.jetbrains.incoder.language.model.server.settings.ui.ProviderUIStrategy;

public class AnthropicFactory implements ServerFactory {

    @Override
    public LanguageModelServer createServer() {
        return new AnthropicLanguageModelServer();
    }

    @Override
    public ProviderUIStrategy createProviderUIStrategy() {
        return new AnthropicUIStrategy();
    }
}

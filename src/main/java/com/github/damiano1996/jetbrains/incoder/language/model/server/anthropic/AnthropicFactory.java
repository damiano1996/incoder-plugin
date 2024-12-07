package com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactory;

public class AnthropicFactory implements ServerFactory {

    @Override
    public LanguageModelServer createServer() {
        return new AnthropicLanguageModelServer();
    }
}

package com.github.damiano1996.jetbrains.incoder.language.model.server.openai;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactory;
import com.github.damiano1996.jetbrains.incoder.language.model.server.settings.ui.ProviderUIStrategy;

public class OpenAiFactory implements ServerFactory {

    @Override
    public LanguageModelServer createServer() {
        return new OpenAiLanguageModelServer();
    }

    @Override
    public ProviderUIStrategy createProviderUIStrategy() {
        return new OpenAiUIStrategy();
    }
}

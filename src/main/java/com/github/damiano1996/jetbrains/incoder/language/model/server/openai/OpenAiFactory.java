package com.github.damiano1996.jetbrains.incoder.language.model.server.openai;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactory;

public class OpenAiFactory implements ServerFactory {

    @Override
    public LanguageModelServer createServer() {
        return new OpenAiLanguageModelServer();
    }
}

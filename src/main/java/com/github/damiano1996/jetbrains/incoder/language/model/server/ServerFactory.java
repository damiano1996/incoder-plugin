package com.github.damiano1996.jetbrains.incoder.language.model.server;

import com.github.damiano1996.jetbrains.incoder.language.model.server.settings.ui.ProviderUIStrategy;

public interface ServerFactory {

    LanguageModelServer createServer();

    ProviderUIStrategy createProviderUIStrategy();
}

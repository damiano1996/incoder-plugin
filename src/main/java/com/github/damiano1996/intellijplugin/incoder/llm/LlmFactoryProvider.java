package com.github.damiano1996.intellijplugin.incoder.llm;

import com.github.damiano1996.intellijplugin.incoder.llm.server.settings.ServerSettings;

public interface LlmFactoryProvider {

    LlmAbstractFactory createLlmAbstractFactory(ServerSettings settings);
}

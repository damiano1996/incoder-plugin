package com.github.damiano1996.intellijplugin.incoder.language.model;

import com.github.damiano1996.intellijplugin.incoder.language.model.langchain.settings.LangChainSettings;

public interface LanguageModelServerFactory {

    LanguageModelServer createServer(LangChainSettings settings);
}

package com.github.damiano1996.intellijplugin.incoder.language.model;

import com.github.damiano1996.intellijplugin.incoder.language.model.settings.ServerSettings;

public interface LanguageModelServerFactory {

    LanguageModelServer createServer(ServerSettings settings);
}

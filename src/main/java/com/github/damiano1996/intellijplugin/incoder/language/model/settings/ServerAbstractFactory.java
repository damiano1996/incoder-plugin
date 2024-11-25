package com.github.damiano1996.intellijplugin.incoder.language.model.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;
import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelServer;

public interface ServerAbstractFactory {

    LanguageModelServer createServer() throws LanguageModelException;

    ServerConfigurable createConfigurable();
}

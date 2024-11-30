package com.github.damiano1996.intellijplugin.incoder.language.model.server;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;
import com.intellij.openapi.options.Configurable;

public interface ServerFactory {

    Configurable createConfigurable();

    LanguageModelServer createServer() throws LanguageModelException;
}

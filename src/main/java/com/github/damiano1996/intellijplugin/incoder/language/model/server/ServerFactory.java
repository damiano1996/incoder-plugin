package com.github.damiano1996.intellijplugin.incoder.language.model.server;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;

public interface ServerFactory {

    LanguageModelServer createServer() throws LanguageModelException;

}

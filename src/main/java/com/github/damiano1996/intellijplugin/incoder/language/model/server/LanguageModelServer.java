package com.github.damiano1996.intellijplugin.incoder.language.model.server;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;
import com.github.damiano1996.intellijplugin.incoder.language.model.client.LanguageModelClient;
import java.util.List;

public interface LanguageModelServer {

    List<String> getAvailableModels();

    String getSelectedModelName();

    LanguageModelClient createClient() throws LanguageModelException;
}

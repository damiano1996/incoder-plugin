package com.github.damiano1996.jetbrains.incoder.language.model.server;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.client.LanguageModelClient;
import java.util.List;

public interface LanguageModelServer {

    String getName();

    List<String> getAvailableModels();

    String getSelectedModelName();

    LanguageModelClient createClient() throws LanguageModelException;
}

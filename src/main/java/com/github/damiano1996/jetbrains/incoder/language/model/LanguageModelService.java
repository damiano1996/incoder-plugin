package com.github.damiano1996.jetbrains.incoder.language.model;

import com.github.damiano1996.jetbrains.incoder.language.model.client.LanguageModelClient;

public interface LanguageModelService {

    LanguageModelClient createClient(String serverName) throws LanguageModelException;
}

package com.github.damiano1996.jetbrains.incoder.language.model.server;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.client.LanguageModelClient;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface LanguageModelServer {

    String getName();

    List<String> getAvailableModels();

    @NotNull
    LanguageModelClient createClient() throws LanguageModelException;
}

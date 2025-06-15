package com.github.damiano1996.jetbrains.incoder.language.model;

import com.github.damiano1996.jetbrains.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelServer;
import dev.langchain4j.service.TokenStream;
import org.jetbrains.annotations.NotNull;

public interface LanguageModelService {

    void init() throws LanguageModelException;

    void init(LanguageModelServer server) throws LanguageModelException;

    boolean isReady();

    String getSelectedModelName() throws LanguageModelException, IllegalStateException;

    TokenStream chat(int memoryId, String prompt) throws LanguageModelException, IllegalStateException;

    String complete(@NotNull CodeCompletionContext codeCompletionContext) throws LanguageModelException, IllegalStateException;

}

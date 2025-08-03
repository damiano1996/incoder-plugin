package com.github.damiano1996.jetbrains.incoder.language.model.client.inline;

import com.github.damiano1996.jetbrains.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.jetbrains.incoder.language.model.client.LanguageModelClient;
import org.jetbrains.annotations.NotNull;

public interface InlineLanguageModelClient extends LanguageModelClient {

    /**
     * Requests code completion suggestions based on the provided context.
     *
     * @param codeCompletionContext the context containing code information for completion
     * @return the completed code suggestion as a string
     */
    String complete(@NotNull CodeCompletionContext codeCompletionContext);
}

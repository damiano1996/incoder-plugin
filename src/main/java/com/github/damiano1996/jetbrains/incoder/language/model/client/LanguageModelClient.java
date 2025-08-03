package com.github.damiano1996.jetbrains.incoder.language.model.client;

import com.github.damiano1996.jetbrains.incoder.completion.CodeCompletionContext;
import dev.langchain4j.service.TokenStream;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for language model clients that provide coding assistance capabilities. This interface
 * defines the contract for interacting with language models to support both chat-based
 * conversations and code completion functionality.
 */
public interface LanguageModelClient {

    String getModelName();

    /**
     * Initiates a chat conversation with the language model.
     *
     * @param memoryId the unique identifier for the conversation memory/context
     * @param prompt the user's input prompt or message
     * @return a TokenStream for streaming the model's response
     */
    TokenStream chat(int memoryId, String prompt);

    /**
     * Requests code completion suggestions based on the provided context.
     *
     * @param codeCompletionContext the context containing code information for completion
     * @return the completed code suggestion as a string
     */
    String complete(@NotNull CodeCompletionContext codeCompletionContext);
}

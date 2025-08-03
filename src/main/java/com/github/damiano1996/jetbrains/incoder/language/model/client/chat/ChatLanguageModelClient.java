package com.github.damiano1996.jetbrains.incoder.language.model.client.chat;

import com.github.damiano1996.jetbrains.incoder.language.model.client.LanguageModelClient;
import dev.langchain4j.service.TokenStream;

public interface ChatLanguageModelClient extends LanguageModelClient {

    /**
     * Initiates a chat conversation with the language model.
     *
     * @param memoryId the unique identifier for the conversation memory/context
     * @param prompt the user's input prompt or message
     * @return a TokenStream for streaming the model's response
     */
    TokenStream chat(int memoryId, String prompt);
}

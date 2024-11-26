package com.github.damiano1996.intellijplugin.incoder.language.model;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LanguageModelClientImpl implements LanguageModelClient {

    private final LanguageModelClient client;

    public LanguageModelClientImpl(
            ChatLanguageModel chatLanguageModel,
            StreamingChatLanguageModel streamingChatLanguageModel,
            ChatMemory chatMemory) {
        client =
                AiServices.builder(LanguageModelClient.class)
                        .streamingChatLanguageModel(streamingChatLanguageModel)
                        .chatLanguageModel(chatLanguageModel)
                        .chatMemory(chatMemory)
                        .build();
    }

    @Override
    public TokenStream complete(String leftContext, String rightContext) {
        log.debug("Completing code");
        return client.complete(leftContext, rightContext);
    }

    @Override
    public TokenStream code(String filePath, String code, String prompt) {
        log.debug("Editing script");
        return client.code(filePath, code, prompt);
    }

    @Override
    public TokenStream chat(String input) {
        log.debug("Chatting");
        return client.chat(input);
    }

    @Override
    public PromptType classify(String prompt) {
        log.debug("Classifying prompt: {}", prompt);
        return client.classify(prompt);
    }

    @Override
    public TokenStream answer(String filePath, String question, String code) {
        log.debug("Answering code question");
        return client.answer(filePath, question, code);
    }
}

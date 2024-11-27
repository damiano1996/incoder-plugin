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
        log.debug("Completing code...");
        return client.complete(leftContext, rightContext);
    }

    @Override
    public TokenStream chat(String code, String filePath, String projectBasePath, String prompt) {
        log.debug("Chatting about codes...");
        return client.chat(code, filePath, projectBasePath, prompt);
    }

    @Override
    public TokenStream chat(String projectBasePath, String prompt) {
        log.debug("Chatting...");
        return client.chat(projectBasePath, prompt);
    }

    @Override
    public PromptType classify(String prompt) {
        log.debug("Classifying prompt: {}...", prompt);
        return client.classify(prompt);
    }

    @Override
    public String createFilePath(String fileContent, String projectFolderTree) {
        log.debug("Defining file path");
        return client.createFilePath(fileContent, projectFolderTree).trim();
    }
}

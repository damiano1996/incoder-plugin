package com.github.damiano1996.jetbrains.incoder.language.model.client;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.ChatCodingAssistant;
import com.github.damiano1996.jetbrains.incoder.language.model.client.doc.DocumentationAssistant;
import com.github.damiano1996.jetbrains.incoder.language.model.client.file.FileManagerAssistant;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.InlineCodingAssistant;
import com.github.damiano1996.jetbrains.incoder.language.model.client.prompt.PromptClassifier;
import com.github.damiano1996.jetbrains.incoder.language.model.client.prompt.PromptType;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LanguageModelClientImpl implements LanguageModelClient {

    private final ChatLanguageModel chatLanguageModel;

    private final ChatCodingAssistant chatCodingAssistant;
    private final DocumentationAssistant documentationAssistant;
    private final InlineCodingAssistant inlineCodingAssistant;
    private final FileManagerAssistant fileManagerAssistant;
    private final PromptClassifier promptClassifier;

    public LanguageModelClientImpl(
            ChatLanguageModel chatLanguageModel,
            StreamingChatLanguageModel streamingChatLanguageModel,
            ChatMemory chatMemory) {

        this.chatLanguageModel = chatLanguageModel;

        chatCodingAssistant =
                AiServices.builder(ChatCodingAssistant.class)
                        .streamingChatLanguageModel(streamingChatLanguageModel)
                        .chatLanguageModel(chatLanguageModel)
                        .chatMemory(chatMemory)
                        .build();

        documentationAssistant =
                AiServices.builder(DocumentationAssistant.class)
                        .streamingChatLanguageModel(streamingChatLanguageModel)
                        .chatLanguageModel(chatLanguageModel)
                        .build();

        inlineCodingAssistant =
                AiServices.builder(InlineCodingAssistant.class)
                        .streamingChatLanguageModel(streamingChatLanguageModel)
                        .chatLanguageModel(chatLanguageModel)
                        .build();

        fileManagerAssistant =
                AiServices.builder(FileManagerAssistant.class)
                        .streamingChatLanguageModel(streamingChatLanguageModel)
                        .chatLanguageModel(chatLanguageModel)
                        .build();

        promptClassifier =
                AiServices.builder(PromptClassifier.class)
                        .streamingChatLanguageModel(streamingChatLanguageModel)
                        .chatLanguageModel(chatLanguageModel)
                        .build();
    }

    @Override
    public String complete(String instructions, String leftContext, String rightContext) {
        log.debug("Completing code...");
        return inlineCodingAssistant.complete(instructions, leftContext, rightContext);
    }

    @Override
    public TokenStream chat(
            String instructions,
            String code,
            String filePath,
            String projectBasePath,
            String prompt) {
        log.debug("Chatting about codes...");
        return chatCodingAssistant.chat(instructions, code, filePath, projectBasePath, prompt);
    }

    @Override
    public TokenStream chat(String instructions, String projectBasePath, String prompt) {
        log.debug("Chatting...");
        return chatCodingAssistant.chat(instructions, projectBasePath, prompt);
    }

    @Override
    public PromptType classify(String prompt) {
        log.debug("Classifying prompt: {}...", prompt);
        return promptClassifier.classify(prompt);
    }

    @Override
    public String createFileName(String fileContent, String language) {
        log.debug("Defining file path");
        return fileManagerAssistant.createFileName(fileContent, language).trim();
    }

    @Override
    public void checkServerConnection() throws LanguageModelException {
        try {
            chatLanguageModel.generate("Hello!");
        } catch (Exception e) {
            throw new LanguageModelException(e);
        }
    }

    @Override
    public String document(String instructions, String code) {
        return documentationAssistant.document(instructions, code);
    }
}

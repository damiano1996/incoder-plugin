package com.github.damiano1996.intellijplugin.incoder.language.model;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LanguageModelClientImpl implements LanguageModelClient {

    private final LanguageModelClient languageModelClient;
    private final LanguageModelClient languageModelClientStream;

    public LanguageModelClientImpl(
            ChatLanguageModel chatLanguageModel,
            StreamingChatLanguageModel streamingChatLanguageModel) {
        languageModelClient = AiServices.create(LanguageModelClient.class, chatLanguageModel);
        languageModelClientStream =
                AiServices.create(LanguageModelClient.class, streamingChatLanguageModel);
    }

    @Override
    public TokenStream complete(String leftContext, String rightContext) {
        return languageModelClientStream.complete(leftContext, rightContext);
    }

    @Override
    public TokenStream editCode(String filePath, String prompt, String actualCode) {
        log.debug("Editing script");
        return languageModelClientStream.editCode(filePath, prompt, actualCode);
    }

    @Override
    public TokenStream chat(String input) {
        log.debug("Chatting");
        return languageModelClientStream.chat(input);
    }

    @Override
    public PromptType classify(String prompt) {
        log.debug("Classifying prompt: {}", prompt);
        return languageModelClient.classify(prompt);
    }

    @Override
    public TokenStream answer(String filePath, String question, String code) {
        log.debug("Answering code question");
        return languageModelClientStream.answer(filePath, question, code);
    }
}

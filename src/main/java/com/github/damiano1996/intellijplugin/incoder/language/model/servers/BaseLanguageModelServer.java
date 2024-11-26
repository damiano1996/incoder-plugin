package com.github.damiano1996.intellijplugin.incoder.language.model.servers;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelClient;
import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelClientImpl;
import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;
import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelServer;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseLanguageModelServer implements LanguageModelServer {

    public abstract ChatLanguageModel createChatLanguageModel();

    public abstract StreamingChatLanguageModel createStreamingChatLanguageModel();

    @Override
    public LanguageModelClient createClient() throws LanguageModelException {
        try {
            ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

            return new LanguageModelClientImpl(
                    createChatLanguageModel(), createStreamingChatLanguageModel(), chatMemory);
        } catch (Exception e) {
            throw new LanguageModelException("Unable to create the client. " + e.getMessage(), e);
        }
    }
}

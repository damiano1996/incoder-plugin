package com.github.damiano1996.intellijplugin.incoder.language.model.server;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;
import com.github.damiano1996.intellijplugin.incoder.language.model.client.LanguageModelClient;
import com.github.damiano1996.intellijplugin.incoder.language.model.client.LanguageModelClientImpl;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.settings.ServerSettings;
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
            ChatMemory chatMemory = createChatMemory();

            return new LanguageModelClientImpl(
                    createChatLanguageModel(), createStreamingChatLanguageModel(), chatMemory);
        } catch (Exception e) {
            throw new LanguageModelException("Unable to create the client. " + e.getMessage(), e);
        }
    }

    protected ChatMemory createChatMemory() {
        return MessageWindowChatMemory.withMaxMessages(
                ServerSettings.getInstance().getState().chatMemoryMaxMessages);
    }
}

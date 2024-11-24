package com.github.damiano1996.intellijplugin.incoder.language.model.langchain;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelClient;
import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelClientImpl;
import com.github.damiano1996.intellijplugin.incoder.language.model.ServerException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;

public abstract class LangChainLanguageModelServer implements LanguageModelServer {

    public abstract ChatLanguageModel createChatLanguageModel();

    public abstract StreamingChatLanguageModel createStreamingChatLanguageModel();

    @Override
    public LanguageModelClient createClient() throws ServerException {
        return new LanguageModelClientImpl(createChatLanguageModel(), createStreamingChatLanguageModel());
    }
}

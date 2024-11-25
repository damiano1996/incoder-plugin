package com.github.damiano1996.intellijplugin.incoder.language.model.servers.openai;

import com.github.damiano1996.intellijplugin.incoder.language.model.servers.BaseLanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.servers.openai.settings.OpenAiSettings;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.apache.commons.lang.NotImplementedException;

public class OpenAiLanguageModelServer extends BaseLanguageModelServer {

    private static OpenAiSettings.State getState() {
        return OpenAiSettings.getInstance().getState();
    }

    @Override
    public ChatLanguageModel createChatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(getState().apiKey)
                .modelName(getState().modelName)
                .temperature(getState().temperature)
                .build();
    }

    @Override
    public StreamingChatLanguageModel createStreamingChatLanguageModel() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(getState().apiKey)
                .modelName(getState().modelName)
                .temperature(getState().temperature)
                .build();
    }

    @Override
    public boolean isHealthy() {
        throw new NotImplementedException();
    }
}

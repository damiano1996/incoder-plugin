package com.github.damiano1996.intellijplugin.incoder.language.model.server.openai;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.BaseLanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.openai.settings.OpenAiSettings;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiLanguageModelName;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import java.util.Arrays;
import java.util.List;

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
    public List<String> getAvailableModels() {
            return Arrays.stream(OpenAiLanguageModelName.values()).map(Enum::name).toList();
    }

    @Override
    public String getSelectedModelName() {
        return getState().modelName;
    }
}

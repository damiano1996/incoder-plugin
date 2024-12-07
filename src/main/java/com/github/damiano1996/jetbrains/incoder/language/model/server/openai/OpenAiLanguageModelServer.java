package com.github.damiano1996.jetbrains.incoder.language.model.server.openai;

import com.github.damiano1996.jetbrains.incoder.language.model.server.BaseLanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.openai.settings.OpenAiSettings;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.*;
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
    public String getName() {
        return "Open AI";
    }

    @Override
    public List<String> getAvailableModels() {
        return Arrays.stream(OpenAiChatModelName.values()).map(Enum::toString).toList();
    }

    @Override
    public String getSelectedModelName() {
        return getState().modelName;
    }
}

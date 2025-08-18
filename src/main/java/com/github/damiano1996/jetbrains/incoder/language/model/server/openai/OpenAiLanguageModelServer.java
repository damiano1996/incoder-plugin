package com.github.damiano1996.jetbrains.incoder.language.model.server.openai;

import static com.github.damiano1996.jetbrains.incoder.language.model.server.openai.OpenAiParameters.toOpenAi;

import com.github.damiano1996.jetbrains.incoder.language.model.server.BaseLanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OpenAiLanguageModelServer extends BaseLanguageModelServer {

    public static final String OPEN_AI = "Open AI";

    @Override
    public ChatLanguageModel createChatLanguageModel(LanguageModelParameters parameters) {
        OpenAiParameters p = toOpenAi(parameters);

        return OpenAiChatModel.builder()
                .baseUrl(p.baseUrl)
                .apiKey(p.apiKey)
                .organizationId(p.organizationId)
                .projectId(p.projectId)
                .modelName(p.modelName)
                .temperature(p.temperature)
                .maxCompletionTokens(
                        p.maxCompletionTokens != null ? p.maxCompletionTokens : p.maxTokens)
                .presencePenalty(p.presencePenalty)
                .frequencyPenalty(p.frequencyPenalty)
                .logitBias(p.logitBias)
                .seed(p.seed)
                .user(p.user)
                .responseFormat(p.responseFormat)
                .strictJsonSchema(Boolean.TRUE.equals(p.strictJsonSchema))
                .strictTools(Boolean.TRUE.equals(p.strictTools))
                .store(Boolean.TRUE.equals(p.store))
                .metadata(p.metadata)
                .serviceTier(p.serviceTier)
                .timeout(p.timeout)
                .build();
    }

    @Override
    public StreamingChatLanguageModel createStreamingChatLanguageModel(
            LanguageModelParameters parameters) {
        OpenAiParameters p = toOpenAi(parameters);

        return OpenAiStreamingChatModel.builder()
                .baseUrl(p.baseUrl)
                .apiKey(p.apiKey)
                .organizationId(p.organizationId)
                .projectId(p.projectId)
                .modelName(p.modelName)
                .temperature(p.temperature)
                .maxCompletionTokens(
                        p.maxCompletionTokens != null ? p.maxCompletionTokens : p.maxTokens)
                .presencePenalty(p.presencePenalty)
                .frequencyPenalty(p.frequencyPenalty)
                .logitBias(p.logitBias)
                .seed(p.seed)
                .user(p.user)
                .responseFormat(p.responseFormat)
                .strictJsonSchema(Boolean.TRUE.equals(p.strictJsonSchema))
                .strictTools(Boolean.TRUE.equals(p.strictTools))
                .store(Boolean.TRUE.equals(p.store))
                .metadata(p.metadata)
                .serviceTier(p.serviceTier)
                .timeout(p.timeout)
                .build();
    }

    @Override
    public String getName() {
        return OPEN_AI;
    }

    @Override
    public List<String> getAvailableModels(String baseUrl) {
        return Arrays.stream(OpenAiChatModelName.values()).map(Enum::toString).toList();
    }

    @Override
    public LanguageModelParameters getDefaultParameters() {
        OpenAiParameters p = new OpenAiParameters();
        p.serverName = getName();
        p.modelName = "";
        p.baseUrl = "https://api.openai.com/v1";
        p.apiKey = "";
        p.maxTokens = 2048;
        p.temperature = 0.1;
        p.maxCompletionTokens = 2048;
        p.stopSequences = Collections.emptyList();
        p.timeout = Duration.of(10, ChronoUnit.SECONDS);
        p.presencePenalty = 0.0;
        p.frequencyPenalty = 0.0;
        p.logitBias = Collections.emptyMap();
        p.metadata = Collections.emptyMap();
        p.strictJsonSchema = false;
        p.strictTools = false;
        p.store = false;
        p.organizationId = null;
        p.projectId = null;
        p.responseFormat = null;
        p.serviceTier = null;
        p.seed = null;
        p.user = null;
        return p;
    }
}

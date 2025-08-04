package com.github.damiano1996.jetbrains.incoder.language.model.client.inline;

import com.github.damiano1996.jetbrains.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.jetbrains.incoder.language.model.client.BaseLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.settings.InlineSettings;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class InlineLanguageModelClientImpl extends BaseLanguageModelClient
        implements InlineLanguageModelClient {

    private final InlineCodingAssistant inlineCodingAssistant;

    public InlineLanguageModelClientImpl(
            LanguageModelParameters parameters, ChatLanguageModel chatLanguageModel) {
        super(parameters);

        inlineCodingAssistant = getInlineCodingAssistant(chatLanguageModel);
    }

    private InlineCodingAssistant getInlineCodingAssistant(ChatLanguageModel chatLanguageModel) {
        final InlineCodingAssistant inlineCodingAssistant;
        inlineCodingAssistant =
                AiServices.builder(InlineCodingAssistant.class)
                        .chatLanguageModel(chatLanguageModel)
                        .build();
        return inlineCodingAssistant;
    }

    @Override
    public String complete(@NotNull CodeCompletionContext codeCompletionContext) {
        return inlineCodingAssistant.complete(
                InlineSettings.getInstance().getState().systemMessageInstructions,
                codeCompletionContext.leftContext(),
                codeCompletionContext.rightContext(),
                getLastLine(codeCompletionContext.leftContext()));
    }

    private String getLastLine(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }

        String[] lines = s.split("\n");
        return lines[lines.length - 1];
    }
}

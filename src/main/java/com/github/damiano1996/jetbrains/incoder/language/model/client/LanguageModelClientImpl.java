package com.github.damiano1996.jetbrains.incoder.language.model.client;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.ChatCodingAssistant;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings.ChatSettings;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.InlineCodingAssistant;
import com.github.damiano1996.jetbrains.incoder.language.model.client.tools.EditorTool;
import com.github.damiano1996.jetbrains.incoder.language.model.client.tools.FileTool;
import com.intellij.openapi.project.Project;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LanguageModelClientImpl implements LanguageModelClient {

    private final ChatLanguageModel chatLanguageModel;

    private final ChatCodingAssistant chatCodingAssistant;
    private final InlineCodingAssistant inlineCodingAssistant;

    public LanguageModelClientImpl(
            Project project, ChatLanguageModel chatLanguageModel,
            StreamingChatLanguageModel streamingChatLanguageModel) {

        this.chatLanguageModel = chatLanguageModel;

        chatCodingAssistant =
                AiServices.builder(ChatCodingAssistant.class)
                        .streamingChatLanguageModel(streamingChatLanguageModel)
                        .chatLanguageModel(chatLanguageModel)
                        .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(ChatSettings.getInstance().getState().maxMessages))
                        .tools(new FileTool(), new EditorTool(project))
                        .build();

        inlineCodingAssistant =
                AiServices.builder(InlineCodingAssistant.class)
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
    public TokenStream chat(int memoryId, String systemInstructions, String prompt) {
        return chatCodingAssistant.chat(memoryId, systemInstructions, prompt);
    }

    @Override
    public void checkServerConnection() throws LanguageModelException {
        try {
            chatLanguageModel.chat("Hello!");
        } catch (Exception e) {
            throw new LanguageModelException(e);
        }
    }
}

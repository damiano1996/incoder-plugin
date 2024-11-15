package com.github.damiano1996.intellijplugin.incoder.llm.langchain.client;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionException;
import com.github.damiano1996.intellijplugin.incoder.initializable.InitializableListener;
import com.github.damiano1996.intellijplugin.incoder.llm.LlmClient;
import com.intellij.openapi.editor.Editor;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.AiServiceTokenStream;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public class LangChainLlmClient implements LlmClient {

    private final ChatLanguageModel chatLanguageModel;
    private final StreamingChatLanguageModel streamingChatLanguageModel;
    private final LangChainCodeService langChainCodeService;

    public LangChainLlmClient(
            ChatLanguageModel chatLanguageModel,
            StreamingChatLanguageModel streamingChatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
        this.streamingChatLanguageModel = streamingChatLanguageModel;

        langChainCodeService = AiServices.create(LangChainCodeService.class, chatLanguageModel);
    }

    @Override
    public String codeComplete(@NotNull CodeCompletionContext context)
            throws CodeCompletionException {
        try {
            return langChainCodeService.complete(context.leftContext(), context.rightContext());
        } catch (Exception e) {
            throw new CodeCompletionException("Unable to generate code.", e);
        }
    }

    @Override
    public void subscribe(InitializableListener listener) {}

    @Override
    public void init() {}

    @Override
    public void close() {}

    @Override
    public TokenStream chat(String input) {
        return AiServices.create(LangChainCodeService.class, streamingChatLanguageModel).chat(input);
    }

    @Override
    public CompletableFuture<PromptType> classify(String prompt) {
        return CompletableFuture.supplyAsync(() -> langChainCodeService.classify(prompt));
    }

    @Override
    public CompletableFuture<CodeEditingResponse> edit(
            @NonNull Editor editor, @NonNull String editDescription) {
        return CompletableFuture.supplyAsync(
                () -> {
                    CodeEditResponse codeEditResponse =
                            langChainCodeService.editCode(
                                    editor.getVirtualFile().getPath(),
                                    editDescription,
                                    editor.getDocument().getText());
                    return new CodeEditingResponse(
                            codeEditResponse.getEditedCode(),
                            codeEditResponse.getChangesDescription());
                });
    }

    @Override
    public TokenStream answer(@NonNull Editor editor, @NonNull String question) {
        return AiServices.create(LangChainCodeService.class, streamingChatLanguageModel)
                .answer(
                        editor.getVirtualFile().getPath(),
                        question,
                        editor.getDocument().getText());
    }
}

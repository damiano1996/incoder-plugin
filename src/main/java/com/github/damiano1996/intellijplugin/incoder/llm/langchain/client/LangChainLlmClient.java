package com.github.damiano1996.intellijplugin.incoder.llm.langchain.client;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionException;
import com.github.damiano1996.intellijplugin.incoder.initializable.InitializableListener;
import com.github.damiano1996.intellijplugin.incoder.llm.LlmClient;
import com.github.damiano1996.intellijplugin.incoder.llm.langchain.client.generation.LangChainCodeGeneration;
import com.github.damiano1996.intellijplugin.incoder.llm.langchain.client.generation.LangChainCodeUpdate;
import com.intellij.openapi.editor.Editor;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class LangChainLlmClient implements LlmClient {

    private final ChatLanguageModel chatLanguageModel;
    private final StreamingChatLanguageModel streamingChatLanguageModel;

    public LangChainLlmClient(
            ChatLanguageModel chatLanguageModel,
            StreamingChatLanguageModel streamingChatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
        this.streamingChatLanguageModel = streamingChatLanguageModel;
    }

    @Override
    public String codeComplete(@NotNull CodeCompletionContext context)
            throws CodeCompletionException {
        try {
            LangChainCodeCompletion langChainCodeCompletion =
                    AiServices.create(LangChainCodeCompletion.class, chatLanguageModel);
            return langChainCodeCompletion.codeComplete(
                    context.leftContext(), context.rightContext());
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
    public CompletableFuture<String> chat(String input) {
        return CompletableFuture.supplyAsync(() -> chatLanguageModel.generate(input));
    }

    @Override
    public CompletableFuture<PromptType> classify(String prompt) {
        return CompletableFuture.supplyAsync(() -> AiServices.create(LangChainCodeGeneration.class, chatLanguageModel).classify(prompt));
    }

    @Override
    public CompletableFuture<CodeEditingResponse> edit(@NonNull Editor editor, @NonNull String editDescription) {
        return CompletableFuture.supplyAsync(() -> {
            LangChainCodeGeneration langChainCodeGeneration =
                    AiServices.create(LangChainCodeGeneration.class, chatLanguageModel);

            LangChainCodeUpdate langChainCodeUpdate =
                    langChainCodeGeneration.codeGenerate(editor.getVirtualFile().getPath(), editDescription, editor.getDocument().getText());
            return new CodeEditingResponse(
                    langChainCodeUpdate.getUpdatedCode(), langChainCodeUpdate.getNotes());
        });
    }

    @Override
    public CompletableFuture<CodeRagResponse> rag(@NonNull Editor editor, @NonNull String question) {
        return null;
    }
}

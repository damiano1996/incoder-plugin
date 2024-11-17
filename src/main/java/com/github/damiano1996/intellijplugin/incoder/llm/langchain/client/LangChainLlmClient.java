package com.github.damiano1996.intellijplugin.incoder.llm.langchain.client;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.intellijplugin.incoder.initializable.InitializableListener;
import com.github.damiano1996.intellijplugin.incoder.llm.LlmClient;
import com.intellij.openapi.editor.Editor;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class LangChainLlmClient implements LlmClient {

    private final LangChainCodeService langChainCodeService;
    private final LangChainCodeService langChainCodeServiceStream;

    public LangChainLlmClient(
            ChatLanguageModel chatLanguageModel,
            StreamingChatLanguageModel streamingChatLanguageModel) {
        langChainCodeService = AiServices.create(LangChainCodeService.class, chatLanguageModel);
        langChainCodeServiceStream =
                AiServices.create(LangChainCodeService.class, streamingChatLanguageModel);
    }

    @Override
    public void subscribe(InitializableListener listener) {}

    @Override
    public void init() {}

    @Override
    public void close() {}

    @Override
    public String codeComplete(@NotNull CodeCompletionContext context) {
        log.debug("Code completion: {}", context);
        return langChainCodeService.complete(context.leftContext(), context.rightContext());
    }

    @Override
    public TokenStream chat(String input) {
        log.debug("Chatting");
        return langChainCodeServiceStream.chat(input);
    }

    @Override
    public CompletableFuture<PromptType> classify(String prompt) {
        log.debug("Classifying prompt: {}", prompt);
        return CompletableFuture.supplyAsync(() -> langChainCodeService.classify(prompt));
    }

    @Override
    public TokenStream edit(@NonNull Editor editor, @NonNull String editDescription) {
        log.debug("Editing script");
        return langChainCodeServiceStream.editCode(
                editor.getVirtualFile().getPath(), editDescription, editor.getDocument().getText());
    }

    @Override
    public TokenStream answer(@NonNull Editor editor, @NonNull String question) {
        log.debug("Answering code question");
        return langChainCodeServiceStream.answer(
                editor.getVirtualFile().getPath(), question, editor.getDocument().getText());
    }
}

package com.github.damiano1996.intellijplugin.incoder.llm.langchain.client;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionException;
import com.github.damiano1996.intellijplugin.incoder.generation.CodeGenerationContext;
import com.github.damiano1996.intellijplugin.incoder.generation.GenerationStream;
import com.github.damiano1996.intellijplugin.incoder.initializable.InitializableListener;
import com.github.damiano1996.intellijplugin.incoder.llm.LlmClient;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class LangChainLlmClient implements LlmClient {

    private final ChatLanguageModel model;
    private final StreamingChatLanguageModel streamingChatLanguageModel;

    public LangChainLlmClient(
            ChatLanguageModel model, StreamingChatLanguageModel streamingChatLanguageModel) {
        this.model = model;
        this.streamingChatLanguageModel = streamingChatLanguageModel;
    }

    @Override
    public String codeComplete(@NotNull CodeCompletionContext context)
            throws CodeCompletionException {
        try {
            LangChainCodeCompletion langChainCodeCompletion =
                    AiServices.create(LangChainCodeCompletion.class, model);
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
    public GenerationStream generate(@NotNull CodeGenerationContext codeGenerationContext) {
        LangChainCodeGeneration langChainCodeGeneration =
                AiServices.create(LangChainCodeGeneration.class, streamingChatLanguageModel);

        TokenStream tokenStream =
                langChainCodeGeneration.codeGenerate(
                        codeGenerationContext.virtualFile().getPath(),
                        codeGenerationContext.prompt(),
                        codeGenerationContext.actualCode());

        return new GenerationStream() {

            @Override
            public GenerationStream onNext(Consumer<String> tokenHandler) {
                tokenStream.onNext(tokenHandler);
                return this;
            }

            @Override
            public GenerationStream onComplete(Consumer<String> completionHandler) {
                tokenStream.onComplete(
                        aiMessageResponse ->
                                completionHandler.accept(aiMessageResponse.content().text()));
                return this;
            }

            @Override
            public GenerationStream onError(Consumer<Throwable> errorHandler) {
                tokenStream.onError(errorHandler);
                return this;
            }

            @Override
            public GenerationStream ignoreErrors() {
                tokenStream.ignoreErrors();
                return this;
            }

            @Override
            public void start() {
                tokenStream.start();
            }
        };
    }
}

package com.github.damiano1996.intellijplugin.incoder.llm.langchain.client;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionException;
import com.github.damiano1996.intellijplugin.incoder.generation.CodeGenerationContext;
import com.github.damiano1996.intellijplugin.incoder.generation.CodeGenerationException;
import com.github.damiano1996.intellijplugin.incoder.generation.CodeUpdateResponse;
import com.github.damiano1996.intellijplugin.incoder.initializable.InitializableListener;
import com.github.damiano1996.intellijplugin.incoder.llm.LlmClient;
import com.github.damiano1996.intellijplugin.incoder.llm.langchain.client.generation.LangChainCodeGeneration;
import com.github.damiano1996.intellijplugin.incoder.llm.langchain.client.generation.LangChainCodeUpdate;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.jetbrains.annotations.NotNull;

public class LangChainLlmClient implements LlmClient {

    private final ChatLanguageModel chatLanguageModel;
    private final StreamingChatLanguageModel streamingChatLanguageModel;

    public LangChainLlmClient(
            ChatLanguageModel chatLanguageModel, StreamingChatLanguageModel streamingChatLanguageModel) {
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
    public CodeUpdateResponse generate(@NotNull CodeGenerationContext codeGenerationContext) throws CodeGenerationException {
        LangChainCodeGeneration langChainCodeGeneration =
                AiServices.create(LangChainCodeGeneration.class, chatLanguageModel);

        LangChainCodeUpdate langChainCodeUpdate =
                langChainCodeGeneration.codeGenerate(
                        codeGenerationContext.virtualFile().getPath(),
                        codeGenerationContext.prompt(),
                        codeGenerationContext.actualCode());
        return new CodeUpdateResponse(langChainCodeUpdate.getUpdatedCode(), langChainCodeUpdate.getNotes());
    }
}

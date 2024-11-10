package com.github.damiano1996.intellijplugin.incoder.llm.langchain.client;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionException;
import com.github.damiano1996.intellijplugin.incoder.generation.CodeGenerationContext;
import com.github.damiano1996.intellijplugin.incoder.generation.CodeGenerationException;
import com.github.damiano1996.intellijplugin.incoder.initializable.InitializableListener;
import com.github.damiano1996.intellijplugin.incoder.llm.LlmClient;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.jetbrains.annotations.NotNull;

public class LangChainLlmClient implements LlmClient {

    private final ChatLanguageModel model;

    public LangChainLlmClient(ChatLanguageModel model) {
        this.model = model;
    }

    @Override
    public String codeComplete(@NotNull CodeCompletionContext context)
            throws CodeCompletionException {
        try {
            LangChainCodeCompletion langChainCodeCompletion =
                    AiServices.create(LangChainCodeCompletion.class, model);
            return langChainCodeCompletion
                    .codeComplete(context.leftContext(), context.rightContext());
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
    public String generate(CodeGenerationContext codeGenerationContext) throws CodeGenerationException {
        try {
            LangChainCodeGeneration langChainCodeGeneration =
                    AiServices.create(LangChainCodeGeneration.class, model);
            return langChainCodeGeneration
                    .codeGenerate(codeGenerationContext.prompt(), codeGenerationContext.actualCode());
        } catch (Exception e) {
            throw new CodeGenerationException("Unable to generate code.", e);
        }}
}

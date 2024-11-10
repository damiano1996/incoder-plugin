package com.github.damiano1996.intellijplugin.incoder.llm.langchain.client;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionException;
import com.github.damiano1996.intellijplugin.incoder.initializable.InitializableException;
import com.github.damiano1996.intellijplugin.incoder.initializable.InitializableListener;
import com.github.damiano1996.intellijplugin.incoder.llm.LlmClient;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class LangChainLlmClient implements LlmClient {

    private final ChatLanguageModel model;
    private final List<InitializableListener> listeners = new ArrayList<>();

    public LangChainLlmClient(ChatLanguageModel model) {
        this.model = model;
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
    public void subscribe(InitializableListener listener) {
        listeners.add(listener);
    }

    @Override
    public void init() throws InitializableException {}

    @Override
    public void close() throws InitializableException {}
}

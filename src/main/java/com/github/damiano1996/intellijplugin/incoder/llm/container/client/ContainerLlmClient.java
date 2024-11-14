package com.github.damiano1996.intellijplugin.incoder.llm.container.client;

import com.github.damiano1996.intellijplugin.incoder.client.invoker.ApiException;
import com.github.damiano1996.intellijplugin.incoder.client.model.AutocompleteRequest;
import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionException;
import com.github.damiano1996.intellijplugin.incoder.initializable.InitializableException;
import com.github.damiano1996.intellijplugin.incoder.initializable.InitializableListener;
import com.github.damiano1996.intellijplugin.incoder.llm.LlmClient;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.intellij.openapi.editor.Editor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ContainerLlmClient implements LlmClient {

    private final com.github.damiano1996.intellijplugin.incoder.client.api.DefaultApi client;
    private final List<InitializableListener> listeners = new ArrayList<>();

    public ContainerLlmClient(URL baseUrl) {
        client = new com.github.damiano1996.intellijplugin.incoder.client.api.DefaultApi();
        client.setCustomBaseUrl(baseUrl.toString());
    }

    @Override
    public void subscribe(InitializableListener listener) {
        listeners.add(listener);
        log.debug("Listener subscribed");
    }

    @Override
    public void init() throws InitializableException {
        notify("Client is ready");
    }

    private void notify(String message) {
        listeners.forEach(listener -> listener.onStatusUpdate(message));
        log.debug("Listeners notified with message: {}", message);
    }

    @Override
    public String codeComplete(@NotNull CodeCompletionContext context)
            throws CodeCompletionException {
        try {
            return (String)
                    client.codeCompleteApiV1CodeCompletePost(
                                    new AutocompleteRequest()
                                            .leftContext(context.leftContext())
                                            .rightContext(context.rightContext()))
                            .getPrediction();
        } catch (ApiException e) {
            throw new CodeCompletionException("Unable to compute prediction.", e);
        }
    }

    @Override
    public void close() {}

    @Override
    public CompletableFuture<String> chat(String input) {
        return null;
    }

    @Override
    public CompletableFuture<PromptType> classify(String prompt) {
        return null;
    }

    @Override
    public CompletableFuture<CodeEditingResponse> edit(@NonNull Editor editor, @NonNull String editDescription) {
        return null;
    }

    @Override
    public CompletableFuture<CodeRagResponse> rag(@NonNull Editor editor, @NonNull String question) {
        return null;
    }
}

package com.github.damiano1996.intellijplugin.incoder.llm.container.client;

import com.github.damiano1996.intellijplugin.incoder.client.invoker.ApiException;
import com.github.damiano1996.intellijplugin.incoder.client.model.AutocompleteRequest;
import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionException;
import com.github.damiano1996.intellijplugin.incoder.generation.CodeGenerationContext;
import com.github.damiano1996.intellijplugin.incoder.generation.GenerationStream;
import com.github.damiano1996.intellijplugin.incoder.initializable.InitializableException;
import com.github.damiano1996.intellijplugin.incoder.initializable.InitializableListener;
import com.github.damiano1996.intellijplugin.incoder.llm.LlmClient;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
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
    public GenerationStream generate(CodeGenerationContext codeGenerationContext) {
        throw new NotImplementedException(
                "Container mode didn't implement code generation from prompt");
    }
}

package com.github.damiano1996.jetbrains.incoder.language.model.client.tokenstream;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import java.util.List;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StoppableTokenStreamImpl implements StoppableTokenStream {

    private TokenStream tokenStream;
    private Runnable onStart;
    private Runnable onStop;
    private Consumer<Throwable> errorHandler;

    private boolean stopRequested = false;

    public StoppableTokenStreamImpl(TokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    @Override
    public StoppableTokenStream onStart(Runnable runnable) {
        this.onStart = runnable;
        return this;
    }

    @Override
    public StoppableTokenStream onStop(Runnable runnable) {
        this.onStop = runnable;
        return this;
    }

    @Override
    public void stop() {
        log.debug("Stop requested");
        stopRequested = true;
    }

    @Override
    public StoppableTokenStream onPartialResponse(Consumer<String> partialResponseHandler) {
        tokenStream =
                tokenStream.onPartialResponse(
                        token -> {
                            if (stopRequested) {
                                onStop.run();
                                log.debug("Throwing exception to stop streaming");
                                // FIXME: AiServiceStreamingResponseHandler is catching the exception
                                throw new StopTokenStream();
                            }

                            partialResponseHandler.accept(token);
                        });
        return this;
    }

    @Override
    public StoppableTokenStream onRetrieved(Consumer<List<Content>> contentHandler) {
        tokenStream = tokenStream.onRetrieved(contentHandler);
        return this;
    }

    @Override
    public StoppableTokenStream onToolExecuted(Consumer<ToolExecution> toolExecuteHandler) {
        tokenStream = tokenStream.onToolExecuted(toolExecuteHandler);
        return this;
    }

    @Override
    public StoppableTokenStream onCompleteResponse(Consumer<ChatResponse> completeResponseHandler) {
        tokenStream =
                tokenStream.onCompleteResponse(
                        chatResponse -> {
                            stopRequested = false;
                            completeResponseHandler.accept(chatResponse);
                        });
        return this;
    }

    @Override
    public StoppableTokenStream onError(Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
        tokenStream = tokenStream.onError(errorHandler);
        return this;
    }

    @Override
    public StoppableTokenStream ignoreErrors() {
        tokenStream = tokenStream.ignoreErrors();
        return this;
    }

    @Override
    public void start() {
        try {
            log.debug("Starting streaming...");
            stopRequested = false;
            onStart.run();
            tokenStream.start();
            log.debug("Streaming started");
        } catch (Exception e) {
            log.error("Error while starting stream", e);
            stopRequested = false;
            errorHandler.accept(e);
        }
    }
}

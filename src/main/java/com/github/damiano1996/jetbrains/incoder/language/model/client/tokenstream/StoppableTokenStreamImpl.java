package com.github.damiano1996.jetbrains.incoder.language.model.client.tokenstream;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class StoppableTokenStreamImpl implements StoppableTokenStream {

    private TokenStream delegate;

    private Runnable onStart = () -> {};
    private Runnable onStop = () -> {};
    private Consumer<Throwable> onError = t -> {};

    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final AtomicBoolean stopNotified = new AtomicBoolean(false);

    public StoppableTokenStreamImpl(TokenStream delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    @Override
    public StoppableTokenStream onStart(Runnable r) {
        this.onStart = r != null ? r : () -> {};
        return this;
    }

    @Override
    public StoppableTokenStream onStop(Runnable r) {
        this.onStop = r != null ? r : () -> {};
        return this;
    }

    @Override
    public void stop() {
        if (stopped.compareAndSet(false, true)) {
            log.debug("Soft-stop requested");
            notifyStopOnce();
        }
    }

    private void notifyStopOnce() {
        if (stopNotified.compareAndSet(false, true)) {
            try {
                onStop.run();
            } catch (Throwable t) {
                log.warn("onStop failed", t);
            }
        }
    }

    @Override
    public StoppableTokenStream onPartialResponse(Consumer<String> handler) {
        delegate =
                delegate.onPartialResponse(
                        token -> {
                            if (stopped.get()) return;
                            handler.accept(token);
                        });
        return this;
    }

    @Override
    public TokenStream onRetrieved(Consumer<List<Content>> handler) {
        delegate =
                delegate.onRetrieved(
                        contents -> {
                            if (stopped.get()) return;
                            handler.accept(contents);
                        });
        return this;
    }

    @Override
    public StoppableTokenStream onToolExecuted(Consumer<ToolExecution> handler) {
        delegate =
                delegate.onToolExecuted(
                        exec -> {
                            if (stopped.get()) return;
                            handler.accept(exec);
                        });
        return this;
    }

    @Override
    public StoppableTokenStream onCompleteResponse(Consumer<ChatResponse> handler) {
        delegate =
                delegate.onCompleteResponse(
                        resp -> {
                            if (stopped.get()) return;
                            handler.accept(resp);
                        });
        return this;
    }

    @Override
    public StoppableTokenStream onError(Consumer<Throwable> handler) {
        this.onError = handler != null ? handler : t -> {};
        delegate =
                delegate.onError(
                        t -> {
                            if (stopped.get()) return;
                            this.onError.accept(t);
                        });
        return this;
    }

    @Override
    public StoppableTokenStream ignoreErrors() {
        delegate = delegate.ignoreErrors();
        return this;
    }

    @Override
    public void start() {
        stopped.set(false);
        stopNotified.set(false);
        try {
            onStart.run();
            delegate.start();
        } catch (Throwable t) {
            if (!stopped.get()) onError.accept(t);
            if (stopped.get()) notifyStopOnce();
        }
    }
}

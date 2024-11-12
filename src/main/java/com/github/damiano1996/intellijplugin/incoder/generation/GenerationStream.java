package com.github.damiano1996.intellijplugin.incoder.generation;

import java.util.function.Consumer;

/**
 * Represents a token stream from language model to which you can subscribe and receive updates when
 * a new token is available, when language model finishes streaming, or when an error occurs during
 * streaming. It is intended to be used as a return type in AI Service.
 */
public interface GenerationStream {

    /**
     * The provided consumer will be invoked every time a new token from a language model is
     * available.
     *
     * @param tokenHandler lambda that consumes tokens of the response
     * @return token stream instance used to configure or start stream processing
     */
    GenerationStream onNext(Consumer<String> tokenHandler);

    GenerationStream onComplete(Consumer<String> completionHandler);

    /**
     * The provided consumer will be invoked when an error occurs during streaming.
     *
     * @param errorHandler lambda that will be invoked when an error occurs
     * @return token stream instance used to configure or start stream processing
     */
    GenerationStream onError(Consumer<Throwable> errorHandler);

    /**
     * All errors during streaming will be ignored (but will be logged with a WARN log level).
     *
     * @return token stream instance used to configure or start stream processing
     */
    GenerationStream ignoreErrors();

    /**
     * Completes the current token stream building and starts processing.
     *
     * <p>Will send a request to LLM and start response streaming.
     */
    void start();
}

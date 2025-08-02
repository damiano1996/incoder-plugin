package com.github.damiano1996.jetbrains.incoder.language.model.client.tokenstream;

import dev.langchain4j.service.TokenStream;

public interface StoppableTokenStream extends TokenStream {

    StoppableTokenStream onStart(Runnable runnable);

    StoppableTokenStream onStop(Runnable runnable);

    void stop();
}

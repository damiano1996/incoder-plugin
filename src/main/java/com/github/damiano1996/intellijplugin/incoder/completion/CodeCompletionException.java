package com.github.damiano1996.intellijplugin.incoder.completion;

public class CodeCompletionException extends Exception {
    public CodeCompletionException() {}

    public CodeCompletionException(String message) {
        super(message);
    }

    public CodeCompletionException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodeCompletionException(Throwable cause) {
        super(cause);
    }
}

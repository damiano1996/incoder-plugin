package com.github.damiano1996.jetbrains.incoder.language.model;

public class LanguageModelException extends Exception {

    public LanguageModelException() {}

    public LanguageModelException(String message) {
        super(message);
    }

    public LanguageModelException(String message, Throwable cause) {
        super(message, cause);
    }

    public LanguageModelException(Throwable cause) {
        super(cause);
    }
}

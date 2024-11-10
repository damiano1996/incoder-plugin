package com.github.damiano1996.intellijplugin.incoder.generation;

public class CodeGenerationException extends Exception {
    public CodeGenerationException() {}

    public CodeGenerationException(String message) {
        super(message);
    }

    public CodeGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodeGenerationException(Throwable cause) {
        super(cause);
    }
}

package com.github.damiano1996.intellijplugin.incoder.initializable;

public class InitializableException extends Exception {
    public InitializableException() {}

    public InitializableException(String message) {
        super(message);
    }

    public InitializableException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitializableException(Throwable cause) {
        super(cause);
    }
}

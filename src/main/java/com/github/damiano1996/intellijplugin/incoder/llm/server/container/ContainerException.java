package com.github.damiano1996.intellijplugin.incoder.llm.server.container;

public class ContainerException extends Exception {

    public ContainerException() {}

    public ContainerException(String message) {
        super(message);
    }

    public ContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContainerException(Throwable cause) {
        super(cause);
    }
}

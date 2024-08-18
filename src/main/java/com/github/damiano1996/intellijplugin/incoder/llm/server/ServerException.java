package com.github.damiano1996.intellijplugin.incoder.llm.server;

public class ServerException extends Exception {
    public ServerException() {}

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }
}

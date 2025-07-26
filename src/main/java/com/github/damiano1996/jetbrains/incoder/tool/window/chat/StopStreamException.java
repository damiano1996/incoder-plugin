package com.github.damiano1996.jetbrains.incoder.tool.window.chat;

public class StopStreamException extends RuntimeException {
    public StopStreamException() {
        super("Stop stream requested.");
    }

    public StopStreamException(String message) {
        super(message);
    }
}

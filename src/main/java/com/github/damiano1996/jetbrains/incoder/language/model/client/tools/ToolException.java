package com.github.damiano1996.jetbrains.incoder.language.model.client.tools;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ToolException extends RuntimeException {
    public ToolException(String message) {
        super(message);
    }

    public ToolException(String message, Throwable cause) {
        super(message, cause);
    }
}

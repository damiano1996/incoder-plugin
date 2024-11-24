package com.github.damiano1996.intellijplugin.incoder.tool.window;

import lombok.NonNull;

public record ChatMessage(@NonNull Author author, @NonNull String message) {

    public enum Author {
        AI,
        USER
    }
}

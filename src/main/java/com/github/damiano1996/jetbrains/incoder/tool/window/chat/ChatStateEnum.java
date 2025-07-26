package com.github.damiano1996.jetbrains.incoder.tool.window.chat;

public enum ChatStateEnum {
    IDLE,
    GENERATING,
    STOPPING,
    ERROR;

    public boolean isGenerating() {
        return this == GENERATING;
    }

    public boolean canAcceptInput() {
        return this == IDLE;
    }

    public boolean canStop() {
        return this == GENERATING;
    }
}

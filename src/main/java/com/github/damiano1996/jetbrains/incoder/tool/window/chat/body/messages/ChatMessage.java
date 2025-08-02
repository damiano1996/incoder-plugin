package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages;

import javax.swing.*;

public interface ChatMessage extends StreamWriter {

    JPanel getMainPanel();

    @Override
    default void write(String token) {
        // nothing
    }

    @Override
    default String getText() {
        return "";
    }

    @Override
    default void closeStream() {
        // nothing
    }
}

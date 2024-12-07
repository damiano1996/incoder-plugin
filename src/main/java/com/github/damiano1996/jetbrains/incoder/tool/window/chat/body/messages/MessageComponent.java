package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages;

import javax.swing.*;
import org.apache.commons.lang.NotImplementedException;

public interface MessageComponent extends StreamWriter {

    JPanel getMainPanel();

    @Override
    default void undoLastWrite() {
        throw new NotImplementedException("Undo has not been implemented yet.");
    }
}

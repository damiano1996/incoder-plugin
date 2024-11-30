package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import org.apache.commons.lang.NotImplementedException;

import javax.swing.*;

public interface MessageComponent extends StreamWriter {

    JPanel getMainPanel();

    @Override
    default void undoLastWrite() {
        throw new NotImplementedException("Undo has not been implemented yet.");
    }
}

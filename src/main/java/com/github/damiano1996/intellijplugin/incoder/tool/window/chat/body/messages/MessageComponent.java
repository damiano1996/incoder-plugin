package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import javax.swing.*;

public interface MessageComponent {

    MessageComponent setMessage(String message);

    JPanel getMainPanel();
}
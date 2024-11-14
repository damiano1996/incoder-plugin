package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import javax.swing.*;
import lombok.Getter;

@Getter
public class AiMessage implements MessageComponent {
    private JPanel mainPanel;
    private JTextArea message;

    @Override
    public MessageComponent setMessage(String message) {
        this.message.setText(message);
        return this;
    }
}

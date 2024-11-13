package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import javax.swing.*;
import lombok.Getter;

@Getter
public class AiMessage implements MessageComponent {
    private JLabel message;
    private JPanel mainPanel;

    @Override
    public MessageComponent setMessage(String message) {
        this.message.setText(message);
        return this;
    }
}

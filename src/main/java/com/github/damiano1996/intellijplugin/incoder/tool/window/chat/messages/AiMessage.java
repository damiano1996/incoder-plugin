package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.messages;

import lombok.Getter;

import javax.swing.*;

@Getter
public class AiMessage implements MessageComponent{
    private JLabel message;
    private JPanel mainPanel;


    @Override
    public MessageComponent setMessage(String message) {
        this.message.setText(message);
        return this;
    }

}

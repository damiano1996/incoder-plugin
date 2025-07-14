package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.human;

import com.github.damiano1996.jetbrains.incoder.tool.window.ToolWindowColors;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.MessageComponent;
import com.github.damiano1996.jetbrains.incoder.ui.components.RoundedTextArea;
import javax.swing.*;
import lombok.Getter;

public class HumanMessageComponent implements MessageComponent {

    private final String userPrompt;

    private JTextArea promptTextArea;

    @Getter private JPanel mainPanel;

    public HumanMessageComponent(String userPrompt) {
        this.userPrompt = userPrompt;
    }

    @Override
    public void write(String token) {
        this.promptTextArea.setText(token);
    }

    @Override
    public String getText() {
        return promptTextArea.getText();
    }

    @Override
    public void streamClosed() {}

    private void createUIComponents() {
        promptTextArea = new RoundedTextArea(35, 35);
        promptTextArea.setBackground(ToolWindowColors.USER_MESSAGE_BACKGROUND);
        promptTextArea.setForeground(ToolWindowColors.USER_MESSAGE_FOREGROUND);
        promptTextArea.setText(userPrompt);
    }
}

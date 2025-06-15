package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages;

import com.github.damiano1996.jetbrains.incoder.ui.components.RoundedLabel;
import com.intellij.ui.JBColor;

import javax.swing.*;

public class ErrorMessageComponent implements MessageComponent {
    private JPanel mainPanel;
    private JLabel errorMessage;

    @Override
    public JPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    public void write(String token) {
        errorMessage.setText(errorMessage.getText() + token);
    }

    @Override
    public String getText() {
        return errorMessage.getText();
    }

    @Override
    public void streamClosed() {

    }

    private void createUIComponents() {
        errorMessage = new RoundedLabel(20, 20, 20, 2);
        errorMessage.setBackground(JBColor.RED);
        errorMessage.setForeground(JBColor.WHITE);
        errorMessage.setVerticalAlignment(SwingConstants.TOP);
        errorMessage.setHorizontalAlignment(SwingConstants.LEFT);
    }
}

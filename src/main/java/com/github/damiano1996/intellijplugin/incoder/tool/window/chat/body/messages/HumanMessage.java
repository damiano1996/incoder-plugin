package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import com.intellij.ui.JBColor;
import java.awt.*;
import javax.swing.*;

import com.intellij.ui.RoundedLineBorder;
import com.intellij.ui.components.JBTextArea;
import lombok.Getter;

public class HumanMessage implements MessageComponent {

    private JTextArea message;

    @Getter private JPanel mainPanel;

//    private void createUIComponents() {
//        message =
//                new JTextArea() {
//                    @Override
//                    protected void paintComponent(Graphics g) {
//                        Graphics2D g2 = (Graphics2D) g;
//                        g2.setRenderingHint(
//                                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//                        g2.setColor(
//                                new JBColor(
//                                        new Color(67, 69, 74, 255), new Color(67, 69, 74, 255)));
//                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
//                        super.paintComponent(g);
//                    }
//                };
//    }

    @Override
    public MessageComponent setMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            this.message.setText(message);
            mainPanel.repaint();
        });
        return this;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        message = new JBTextArea();
    }
}

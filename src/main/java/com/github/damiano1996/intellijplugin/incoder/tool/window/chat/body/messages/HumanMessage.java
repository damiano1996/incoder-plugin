package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import com.github.damiano1996.intellijplugin.incoder.llm.Llm;
import javax.swing.*;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class HumanMessage implements MessageComponent {

    private JTextArea message;

    @Getter private JPanel mainPanel;
    private JLabel promptTypeLabel;

    public void setPromptTypeLabel(Llm.@NotNull PromptType promptType) {
        promptTypeLabel.setText(promptType.name().toLowerCase());
        promptTypeLabel.setVisible(true);
    }

    //    private void createUIComponents() {
    //        message =
    //                new JTextArea() {
    //                    @Override
    //                    protected void paintComponent(Graphics g) {
    //                        Graphics2D g2 = (Graphics2D) g;
    //                        g2.setRenderingHint(
    //                                RenderingHints.KEY_ANTIALIASING,
    // RenderingHints.VALUE_ANTIALIAS_ON);
    //                        g2.setColor(
    //                                new JBColor(
    //                                        new Color(67, 69, 74, 255), new Color(67, 69, 74,
    // 255)));
    //                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
    //                        super.paintComponent(g);
    //                    }
    //                };
    //    }

    @Override
    public MessageComponent setMessage(String message) {
        this.message.setText(message);
        return this;
    }

    private void createUIComponents() {
        message = new RoundedTextArea(35, 35);
        message.setBackground(JBColor.namedColor("Label.foreground"));
        message.setForeground(JBColor.namedColor("Label.background"));

        // promptTypeLabel = new JBLabel();
        promptTypeLabel = new RoundedLabel(20, 20, 10);
        promptTypeLabel.setBackground(JBColor.namedColor("Label.background"));
        promptTypeLabel.setForeground(JBColor.namedColor("Label.foreground"));
    }
}

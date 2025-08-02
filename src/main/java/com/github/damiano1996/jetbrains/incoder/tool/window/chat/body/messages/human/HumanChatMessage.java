package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.human;

import static com.github.damiano1996.jetbrains.incoder.tool.window.chat.ChatConstants.ARC_DIAMETER;
import static com.github.damiano1996.jetbrains.incoder.tool.window.chat.ChatConstants.PADDING;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ChatMessage;
import com.github.damiano1996.jetbrains.incoder.ui.components.RoundedUtils;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;

public class HumanChatMessage implements ChatMessage {

    private final String userPrompt;
    @Getter private JPanel mainPanel;

    public HumanChatMessage(String userPrompt) {
        this.userPrompt = userPrompt;
        createUIComponents();
    }

    private void createUIComponents() {
        mainPanel = new JBPanel<>(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(JBUI.Borders.empty(10));

        JBTextArea promptTextArea =
                new JBTextArea() {
                    @Override
                    protected Graphics getComponentGraphics(Graphics graphics) {
                        return RoundedUtils.getRoundedComponentGraphics(
                                this, graphics, ARC_DIAMETER);
                    }
                };
        promptTextArea.setBorder(JBUI.Borders.empty(PADDING * 2));
        promptTextArea.setText(userPrompt);
        promptTextArea.setEditable(false);
        promptTextArea.setLineWrap(true);
        promptTextArea.setWrapStyleWord(true);
        promptTextArea.setOpaque(false);
        promptTextArea.setBackground(JBColor.background().darker());

        mainPanel.add(promptTextArea, BorderLayout.CENTER);
    }
}

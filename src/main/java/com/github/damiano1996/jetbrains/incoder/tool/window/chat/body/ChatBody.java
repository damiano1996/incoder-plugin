package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.MessageComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ChatBody {
    private JPanel mainPanel;
    private JPanel messagesPanel;
    private JScrollPane scrollPane;

    public void addMessage(@NotNull MessageComponent messageComponent) {
        var messageMainPanel = messageComponent.getMainPanel();
        messagesPanel.add(messageMainPanel);
        messageMainPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        repaint();
        scrollToBottom();
    }

    public void updateUI() {
        repaint();
        scrollToBottomSmoothly();
    }

    private void repaint() {
        messagesPanel.revalidate();
        messagesPanel.repaint();
    }

    private void scrollToBottom() {
        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            JScrollBar vertical = scrollPane.getVerticalScrollBar();
                            vertical.setValue(vertical.getMaximum());
                        });
    }

    private void scrollToBottomSmoothly() {
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        if (vertical.getValue() + vertical.getVisibleAmount() >= vertical.getMaximum() - 20) {
            Timer timer = new Timer(10, null);
            timer.addActionListener(
                    e -> {
                        int currentValue = vertical.getValue();
                        int targetValue = vertical.getMaximum() - vertical.getVisibleAmount();
                        if (currentValue < targetValue) {
                            vertical.setValue(Math.min(currentValue + 10, targetValue));
                        } else {
                            timer.stop();
                        }
                    });
            timer.start();
        }
    }

    private void createUIComponents() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(JBColor.namedColor("ToolWindow.background"));

        // Create messages panel with proper constraints
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(JBColor.namedColor("ToolWindow.background"));
        messagesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        messagesPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        // Create a wrapper panel to hold messagesPanel
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(JBColor.namedColor("ToolWindow.background"));
        wrapperPanel.add(messagesPanel, BorderLayout.NORTH);

        // Create scroll pane
        scrollPane = new JBScrollPane(wrapperPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }
}

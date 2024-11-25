package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body;

import com.github.damiano1996.intellijplugin.incoder.tool.window.ChatMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.MessageComponent;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.AiMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.human.HumanMessage;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
public class ChatBody {
    private JPanel mainPanel;
    private JPanel messagesPanel;
    private JScrollPane scrollPane;

    @Contract("_ -> new")
    public static @NotNull MessageComponent getMessageComponent(
            ChatMessage.@NonNull Author author) {
        return switch (author) {
            case AI -> new AiMessage();
            case USER -> new HumanMessage();
        };
    }

    public MessageComponent addMessage(@NotNull ChatMessage item) {
        var messageComponent = getMessageComponent(item.author());
        messageComponent.write(item.message());

        var messageMainPanel = messageComponent.getMainPanel();
        messagesPanel.add(messageMainPanel);
        messageMainPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        updateUI();

        return messageComponent;
    }

    public void updateUI() {
        messagesPanel.revalidate();
        messagesPanel.repaint();
        scrollToBottom();
    }

    public void scrollToBottom() {
        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            JScrollBar vertical = scrollPane.getVerticalScrollBar();
                            vertical.setValue(vertical.getMaximum());
                        });
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

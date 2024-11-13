package com.github.damiano1996.intellijplugin.incoder.tool.window.chat;

import com.github.damiano1996.intellijplugin.incoder.tool.window.ChatMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.messages.AiMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.messages.HumanMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.messages.MessageComponent;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

@Getter
public class Chat {

    private JPanel mainPanel;

    private JPanel p2;
    private JScrollPane scrollPane;

    @Contract("_ -> new")
    public static @NotNull MessageComponent getMessageComponent(ChatMessage.@NonNull Author author) {
        switch (author) {
            case AI -> {
                return new AiMessage();
            }
            case USER -> {
                return new HumanMessage();
            }
            default -> throw new IllegalStateException("Unexpected value: " + author);
        }
    }

    public void addMessage(@NotNull ChatMessage item) {
        var messageComponent = getMessageComponent(item.author()).setMessage(item.message());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = p2.getComponentCount();
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = JBUI.insets(5); // Optional: Add spacing

        p2.add(messageComponent.getMainPanel(), gbc);
//        p2.revalidate();
//        p2.repaint();
    }

    private void createUIComponents() {
        mainPanel = new JPanel();

        p2 = new JPanel();
        p2.setLayout(new GridBagLayout());

        scrollPane = new JBScrollPane(p2);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }
}

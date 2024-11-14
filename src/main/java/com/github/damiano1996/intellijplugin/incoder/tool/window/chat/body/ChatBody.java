package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body;

import com.github.damiano1996.intellijplugin.incoder.tool.window.ChatMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.AiMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.HumanMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.MessageComponent;
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

    private JPanel messagePanel;
    private JScrollPane scrollPane;

    @Contract("_ -> new")
    public static @NotNull MessageComponent getMessageComponent(
            ChatMessage.@NonNull Author author) {
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

    public MessageComponent addMessage(@NotNull ChatMessage item) {
        var messageComponent = getMessageComponent(item.author()).setMessage(item.message());
        messagePanel.add(messageComponent.getMainPanel(), 0);
        return messageComponent;
    }

    private void createUIComponents() {
        mainPanel = new JPanel();

        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));

        scrollPane = new JBScrollPane(messagePanel);
        // scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        // scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }
}

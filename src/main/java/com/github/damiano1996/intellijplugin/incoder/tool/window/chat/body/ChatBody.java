package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body;

import com.github.damiano1996.intellijplugin.incoder.tool.window.ChatMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.AiMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.HumanMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.MessageComponent;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@Getter
public class ChatBody {

    private JPanel mainPanel;

    private JList<MessageComponent> messageList;
    private DefaultListModel<MessageComponent> listModel;
    private JScrollPane scrollPane;

    @Contract("_ -> new")
    public static @NotNull MessageComponent getMessageComponent(
            ChatMessage.@NonNull Author author) {
        return switch (author) {
            case AI -> new HumanMessage();
            case USER -> new HumanMessage();
        };
    }

    public MessageComponent addMessage(@NotNull ChatMessage item) {
        var messageComponent = getMessageComponent(item.author()).setMessage(item.message());
        listModel.add(0, messageComponent);
        return messageComponent;
    }

    private void createUIComponents() {
        listModel = new DefaultListModel<>();
        messageList = new JList<>(listModel);
        messageList.setCellRenderer(new MessageComponentRenderer());

        scrollPane = new JBScrollPane(messageList);
    }

    // Custom renderer for JList
    private static class MessageComponentRenderer implements ListCellRenderer<MessageComponent> {
        @Override
        public Component getListCellRendererComponent(JList<? extends MessageComponent> list,
                                                      MessageComponent value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            return value.getMainPanel();
        }
    }
}

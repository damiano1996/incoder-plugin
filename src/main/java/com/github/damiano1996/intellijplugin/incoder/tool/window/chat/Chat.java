package com.github.damiano1996.intellijplugin.incoder.tool.window.chat;

import com.github.damiano1996.intellijplugin.incoder.tool.window.ChatMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.messages.AiMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.messages.HumanMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.messages.MessageComponent;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

@Getter
public class Chat {

    private JPanel mainPanel;
    private JBList<ChatMessage> chatMessageJList;
    private JScrollPane scrollPane;
    private DefaultListModel<ChatMessage> chatMessageDefaultListModel;

    private void createUIComponents() {
        chatMessageDefaultListModel = new DefaultListModel<>();
        chatMessageJList = new JBList<>(chatMessageDefaultListModel);
        chatMessageJList.setCellRenderer(new ChatMessageRenderer());
        scrollPane = new JBScrollPane(chatMessageJList);
    }

    public void addMessage(ChatMessage item) {
        chatMessageDefaultListModel.addElement(item);
    }

    public void removeMessage(ChatMessage item) {
        chatMessageDefaultListModel.removeElement(item);
    }

    private static class ChatMessageRenderer extends JPanel implements ListCellRenderer<ChatMessage> {

        @Contract("_ -> new")
        private static @NotNull MessageComponent getMessageComponent(ChatMessage.@NonNull Author author) {
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

        @Contract("_, _, _, _, _ -> this")
        @Override
        public Component getListCellRendererComponent(JList<? extends ChatMessage> list, @NotNull ChatMessage value, int index, boolean isSelected, boolean cellHasFocus) {
            removeAll();

            setLayout(new FlowLayout());

            add(getMessageComponent(value.author())
                    .setMessage(value.message())
                    .getMainPanel());

            return this;
        }
    }
}

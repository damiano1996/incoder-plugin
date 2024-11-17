package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body;

import com.github.damiano1996.intellijplugin.incoder.tool.window.ChatMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.HumanMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.MessageComponent;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.AiMessage;
import com.intellij.openapi.fileTypes.FileType;
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

    private JList<MessageComponent> messageList;
    private DefaultListModel<MessageComponent> listModel;
    private JScrollPane scrollPane;

    public static class FirableListModel<T> extends DefaultListModel<T> {
        public void update(int index) {
            fireContentsChanged(this, index, index);
        }
    }

    @Contract("_ -> new")
    public static @NotNull MessageComponent getMessageComponent(
            ChatMessage.@NonNull Author author) {
        return switch (author) {
            case AI -> new AiMessage();
            case USER -> new HumanMessage();
        };
    }

    public MessageComponent addMessage(@NotNull ChatMessage item, FileType fileType) {
        var messageComponent = getMessageComponent(item.author()).setMessage(item.message());
        listModel.insertElementAt(messageComponent, 0);
        return messageComponent;
    }

    private void createUIComponents() {
        mainPanel = new JPanel();
        mainPanel.setBackground(JBColor.namedColor("ToolWindow.background"));

        listModel = new FirableListModel<>();
        messageList = new JList<>(listModel);
        messageList.setCellRenderer(new MessageComponentRenderer());

        scrollPane = new JBScrollPane(messageList);
    }

    private static class MessageComponentRenderer implements ListCellRenderer<MessageComponent> {
        @Override
        public Component getListCellRendererComponent(
                JList<? extends MessageComponent> list,
                @NotNull MessageComponent value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            return value.getMainPanel();
        }
    }
}

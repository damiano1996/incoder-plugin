package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body;

import com.github.damiano1996.intellijplugin.incoder.tool.window.ChatMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.AiMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.HumanMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.MessageComponent;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
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

    public void addMessage(@NotNull ChatMessage item) {
        var messageComponent = getMessageComponent(item.author()).setMessage(item.message());
        GridBagConstraints gridBagConstraints = createGridBagConstraints();
        messagePanel.add(messageComponent.getMainPanel(), gridBagConstraints);
        mainPanel.revalidate();
    }

    private @NotNull GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = messagePanel.getComponentCount();
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = JBUI.insets(5);
        return gridBagConstraints;
    }

    private void createUIComponents() {
        mainPanel = new JPanel();

        messagePanel = new JPanel();
        messagePanel.setLayout(new GridBagLayout());

        scrollPane = new JBScrollPane(messagePanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);


    }
}

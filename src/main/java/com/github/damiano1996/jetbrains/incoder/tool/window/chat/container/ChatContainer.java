package com.github.damiano1996.jetbrains.incoder.tool.window.chat.container;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.Chat;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.container.toolbar.ChatActionToolbar;
import com.intellij.openapi.project.Project;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import lombok.Getter;

public class ChatContainer {
    private JComponent chatToolBar;
    private JPanel chatPanel;
    @Getter private JPanel mainPanel;

    private Project project;

    private List<Chat> chats;

    private void createUIComponents() {
        chatPanel = new JPanel();
        chatPanel = new JPanel();

        chats = new ArrayList<>();
        chatToolBar = ChatActionToolbar.createActionToolbarComponent(chatPanel, this);
    }

    public void setProject(Project project) {
        this.project = project;
        if (chats.isEmpty()) createNewChat();
    }

    public void createNewChat() {
        Chat chat = new Chat();
        chat.setPromptActionListener(project);
        chats.add(chat);
        chat.setChatId(chats.size() - 1);

        chatPanel.removeAll();

        chatPanel.setLayout(new BorderLayout());
        chatPanel.add(chat.getMainPanel(), BorderLayout.CENTER);

        chatPanel.revalidate();
        chatPanel.repaint();
    }
}

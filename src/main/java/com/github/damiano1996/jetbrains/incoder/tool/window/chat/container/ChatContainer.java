package com.github.damiano1996.jetbrains.incoder.tool.window.chat.container;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.Chat;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.container.toolbar.ChatActionToolbar;
import com.github.damiano1996.jetbrains.incoder.ui.components.Layout;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import lombok.Getter;

public class ChatContainer {
    private final JPanel chatPanel = new JPanel();
    @Getter private JPanel mainPanel;

    private final Project project;

    private final List<Chat> chats = new ArrayList<>();

    public ChatContainer(Project project) {
        this.project = project;
        createUIComponents();
        createNewChat();
    }

    private void createUIComponents() {
        JComponent chatToolBar = ChatActionToolbar.createActionToolbarComponent(chatPanel, this);
        chatToolBar.setPreferredSize(new Dimension(-1, 20));

        mainPanel =
                FormBuilder.createFormBuilder()
                        .addComponent(Layout.componentToRight(chatToolBar))
                        .addVerticalGap(4)
                        .addComponentFillVertically(chatPanel, 0)
                        .getPanel();

        mainPanel.setMinimumSize(new Dimension(400, 400));
        mainPanel.setPreferredSize(new Dimension(400, 400));
    }

    public void createNewChat() {
        try {
            Chat chat = new Chat(project);
            chats.add(chat);
            chat.setChatId(chats.size() - 1);

            chatPanel.removeAll();

            chatPanel.setLayout(new BorderLayout());
            chatPanel.add(chat.getMainPanel(), BorderLayout.CENTER);

            chatPanel.revalidate();
            chatPanel.repaint();
        } catch (LanguageModelException e) {
            // todo
        }
    }
}

package com.github.damiano1996.jetbrains.incoder.tool.window.chat.container.toolbar.actions;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.container.ChatContainer;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class NewChatAction extends AnAction {

    private final ChatContainer chatContainer;

    public NewChatAction(ChatContainer chatContainer) {
        super("New Chat", "Creates a new chat", AllIcons.Actions.AddList);
        this.chatContainer = chatContainer;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            chatContainer.createNewChat();
                        });
    }
}

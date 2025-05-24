package com.github.damiano1996.jetbrains.incoder.tool.window.chat.container.toolbar;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.container.ChatContainer;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.container.toolbar.actions.NewChatAction;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import javax.swing.*;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class ChatActionToolbar {

    public @NotNull JComponent createActionToolbarComponent(
            @Nullable JComponent targetComponent, ChatContainer chatContainer) {
        var actionGroup = new DefaultActionGroup("Chat Group", true);

        actionGroup.add(new NewChatAction(chatContainer));

        var actionToolbar =
                ActionManager.getInstance().createActionToolbar("ChatToolbar", actionGroup, true);
        actionToolbar.setTargetComponent(targetComponent);

        actionToolbar.setMiniMode(false);
        return actionToolbar.getComponent();
    }
}

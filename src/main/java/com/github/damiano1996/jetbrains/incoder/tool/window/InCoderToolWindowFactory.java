package com.github.damiano1996.jetbrains.incoder.tool.window;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.container.ChatContainer;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public final class InCoderToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ChatContainer chatContainer = new ChatContainer(project);
        Content content =
                ContentFactory.getInstance()
                        .createContent(chatContainer.getMainPanel(), null, false);
        toolWindow.getContentManager().addContent(content);
    }
}

package com.github.damiano1996.intellijplugin.incoder.tool.window;

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

        Tools tools = new Tools().setActionListeners(project);

        Content content =
                ContentFactory.getInstance()
                        .createContent(tools.getMainPanel(), "", false);
        toolWindow.getContentManager().addContent(content);

        tools.addMessageToHistory(new ChatMessage(ChatMessage.Author.USER, "Hello!"));
        tools.addMessageToHistory(new ChatMessage(ChatMessage.Author.AI, "I'm Jarvis."));

        tools.addMessageToHistory(new ChatMessage(ChatMessage.Author.USER, "Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World!"));
        tools.addMessageToHistory(new ChatMessage(ChatMessage.Author.AI, "Good morning!"));
    }


}
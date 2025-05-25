package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.code.toolbar;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.code.CodeMarkdownBlock;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.code.toolbar.actions.CopyClipboardCodeAction;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.code.toolbar.actions.CreateFileAction;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.code.toolbar.actions.MergeAction;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import javax.swing.*;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class CodeActionToolbar {

    public @NotNull JComponent createActionToolbarComponent(
            @Nullable JComponent targetComponent, CodeMarkdownBlock codeMarkdownBlock) {
        var actionGroup = new DefaultActionGroup("Coding Group", true);

        actionGroup.add(new MergeAction(codeMarkdownBlock));
        actionGroup.add(new CreateFileAction(codeMarkdownBlock));
        actionGroup.add(new CopyClipboardCodeAction(codeMarkdownBlock));

        var actionToolbar =
                ActionManager.getInstance()
                        .createActionToolbar("CodeBlockToolbar", actionGroup, true);
        actionToolbar.setTargetComponent(targetComponent);

        actionToolbar.setMiniMode(false);
        return actionToolbar.getComponent();
    }
}

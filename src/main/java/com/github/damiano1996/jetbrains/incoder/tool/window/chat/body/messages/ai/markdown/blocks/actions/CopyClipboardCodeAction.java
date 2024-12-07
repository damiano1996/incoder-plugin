package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.actions;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.CodeMarkdownBlock;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ide.CopyPasteManager;
import java.awt.datatransfer.StringSelection;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class CopyClipboardCodeAction extends AnAction {

    private final CodeMarkdownBlock codeBlock;

    public CopyClipboardCodeAction(CodeMarkdownBlock codeBlock) {
        super(
                "Copy to Clipboard",
                "Copies the code block content to clipboard",
                AllIcons.Actions.Copy);
        this.codeBlock = codeBlock;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            String code = codeBlock.getFullText();
                            CopyPasteManager.getInstance().setContents(new StringSelection(code));
                        });
    }
}

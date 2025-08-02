package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.blocks.code.toolbar.actions;

import com.github.damiano1996.jetbrains.incoder.diff.DiffUtils;
import com.github.damiano1996.jetbrains.incoder.notification.NotificationService;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.blocks.code.CodeMarkdownBlock;
import com.intellij.diff.DiffManager;
import com.intellij.diff.InvalidDiffRequestException;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class MergeAction extends AnAction {

    private final CodeMarkdownBlock codeBlock;

    public MergeAction(CodeMarkdownBlock codeBlock) {
        super("Merge with Current Document", "Merge selected changes", AllIcons.Vcs.Merge);
        this.codeBlock = codeBlock;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        var project = anActionEvent.getProject();

        var editor =
                FileEditorManager.getInstance(Objects.requireNonNull(project))
                        .getSelectedTextEditor();

        if (editor == null) {
            NotificationService.getInstance(project)
                    .notifyWarning(
                            "Unable to open a merge request when no file has been selected.");
            return;
        }

        showDiff(project, codeBlock.getText(), editor);
    }

    private void showDiff(Project project, String newCode, @NotNull Editor editor) {
        try {
            Document originalDoc =
                    FileDocumentManager.getInstance().getDocument(editor.getVirtualFile());
            var textMergeRequest =
                    DiffUtils.showDiffWithProposedChange(
                            project,
                            editor.getVirtualFile(),
                            Objects.requireNonNull(originalDoc).getText(),
                            newCode,
                            mergeResult -> log.info("Merge request result: {}", mergeResult));
            DiffManager.getInstance().showMerge(project, textMergeRequest);
        } catch (InvalidDiffRequestException e) {
            log.error("Unable to merge", e);
        }
    }
}

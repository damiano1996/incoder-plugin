package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.actions;

import com.github.damiano1996.intellijplugin.incoder.notification.NotificationService;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.CodeMarkdownBlock;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestFactory;
import com.intellij.diff.InvalidDiffRequestException;
import com.intellij.diff.merge.TextMergeRequest;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class MergeAction extends AnAction {

    private final CodeMarkdownBlock codeBlock;

    public MergeAction(CodeMarkdownBlock codeBlock) {
        super("Merge...", "Merge selected changes", AllIcons.Vcs.Merge);
        this.codeBlock = codeBlock;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        var project = anActionEvent.getProject();

        var editor = FileEditorManager.getInstance(Objects.requireNonNull(project))
                .getSelectedTextEditor();

        if (editor == null) {
            NotificationService.getInstance(project).notifyWarning("Unable to open a merge request when no file has been selected.");
            return;
        }

        showDiff(project, codeBlock.getFullText(), editor);
    }

    private void showDiff(Project project, String newCode, @NotNull Editor editor) {
        try {
            Document originalDoc =
                    FileDocumentManager.getInstance().getDocument(editor.getVirtualFile());
            var textMergeRequest =
                    showDiffWithProposedChange(
                            project,
                            editor.getVirtualFile(),
                            Objects.requireNonNull(originalDoc).getText(),
                            newCode);
            DiffManager.getInstance().showMerge(project, textMergeRequest);
        } catch (IOException | InvalidDiffRequestException e) {
            throw new RuntimeException(e);
        }
    }

    private @NotNull TextMergeRequest showDiffWithProposedChange(
            Project project,
            VirtualFile originalFile,
            @NotNull String originalContent,
            @NotNull String proposedContent)
            throws IOException, InvalidDiffRequestException {

        var contents = new ArrayList<byte[]>();
        contents.add(originalContent.getBytes());
        contents.add(proposedContent.getBytes());
        contents.add(proposedContent.getBytes());

        log.debug("Preparing merge request");

        return DiffRequestFactory.getInstance()
                .createTextMergeRequest(
                        project,
                        originalFile,
                        contents,
                        "InCoder Proposal",
                        List.of("Original", "Result", "InCoder proposal"),
                        mergeResult -> log.debug("Merge request result: {}", mergeResult));
    }
}

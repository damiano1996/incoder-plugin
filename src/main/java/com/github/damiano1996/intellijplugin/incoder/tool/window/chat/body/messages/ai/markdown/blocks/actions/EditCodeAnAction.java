package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.actions;

import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.CodeMarkdownBlock;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestFactory;
import com.intellij.diff.InvalidDiffRequestException;
import com.intellij.diff.merge.TextMergeRequest;
import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class EditCodeAnAction extends AnAction {

    private final CodeMarkdownBlock codeBlock;

    public EditCodeAnAction(CodeMarkdownBlock codeBlock) {
        super("Merge...", "Merge selected changes", AllIcons.Vcs.Merge);
        this.codeBlock = codeBlock;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        var project = anActionEvent.getProject();

        var editor = FileEditorManager.getInstance(Objects.requireNonNull(project))
                                .getSelectedTextEditor();

        if (editor == null) {
            createNewFile(project, "temp", codeBlock.getFullText());
        } else {
            showDiff(project, codeBlock.getFullText(), editor);
        }

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

    private void createNewFile(Project project, String fileName, String fileContent) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            VirtualFile baseDir = project.getBaseDir(); // Project base directory
            if (baseDir == null)

                return;


            try {
                VirtualFile newFile = baseDir.createChildData(this, fileName); // Create a new file
                VfsUtil.saveText(newFile, fileContent); // Write content to the file
                FileEditorManager.getInstance(project).openFile(newFile, true); // Open the file in editor
            } catch (IOException e) {

            }
        });
    }
}

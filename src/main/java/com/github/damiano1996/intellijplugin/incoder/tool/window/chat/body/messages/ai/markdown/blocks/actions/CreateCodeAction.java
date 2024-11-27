package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.actions;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelService;
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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class CreateCodeAction extends AnAction {

    private final CodeMarkdownBlock codeBlock;

    public CreateCodeAction(CodeMarkdownBlock codeBlock) {
        super("Create New File...", "Creates a new file with the code block content", AllIcons.Actions.AddFile);
        this.codeBlock = codeBlock;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        var project = anActionEvent.getProject();
        if (project == null)return;

        var filePath = Path.of(LanguageModelService.getInstance(project).createFilePath(codeBlock.getFullText()));
        log.debug("Generated file path: {}", filePath);

        createNewFile(project, filePath, codeBlock.getFullText());
    }

    private void createNewFile(Project project, Path filePath, String fileContent) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                VirtualFile targetDir = VfsUtil.createDirectories(filePath.getParent().toString());
                VirtualFile newFile = targetDir.createChildData(this, filePath.getFileName().toString());
                VfsUtil.saveText(newFile, fileContent);
                FileEditorManager.getInstance(project).openFile(newFile, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}

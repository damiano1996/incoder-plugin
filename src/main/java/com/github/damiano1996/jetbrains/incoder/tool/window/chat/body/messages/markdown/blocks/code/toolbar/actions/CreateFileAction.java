package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.blocks.code.toolbar.actions;

import com.github.damiano1996.jetbrains.incoder.notification.NotificationService;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.blocks.code.CodeMarkdownBlock;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class CreateFileAction extends AnAction {

    private final CodeMarkdownBlock codeBlock;

    public CreateFileAction(CodeMarkdownBlock codeBlock) {
        super(
                "Create New File from Code Block",
                "Creates a new file with the code block content",
                AllIcons.Actions.AddFile);
        this.codeBlock = codeBlock;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        var project = anActionEvent.getProject();
        if (project == null) return;

        try {
            var selectedFolder = chooseFolder(project);
            if (selectedFolder == null) return;

            var fileName = askForFileName(project);
            if (fileName == null || fileName.trim().isEmpty()) return;

            var selectedFolderPath = Path.of(selectedFolder.getPath());
            Path filePath = selectedFolderPath.resolve(fileName.trim());

            log.debug("Generated file path: {}", filePath);

            if (Files.exists(filePath)) {
                NotificationService.getInstance(project)
                        .notifyWarning(
                                "File '%s' already exists in the selected folder."
                                        .formatted(filePath.getFileName()));
                return;
            }

            log.debug("Going to create file with code block content");
            createNewFile(project, filePath, codeBlock.getText());

        } catch (Exception e) {
            NotificationService.getInstance(project).notifyError(e.getMessage());
        }
    }

    private VirtualFile chooseFolder(@NotNull Project project) {
        FileChooserDescriptor descriptor =
                FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle("Select a Folder to Save Your File");
        VirtualFile projectBaseDir = ProjectUtil.guessProjectDir(project);
        descriptor.setRoots(projectBaseDir);
        return FileChooser.chooseFile(descriptor, project, projectBaseDir);
    }

    private String askForFileName(@NotNull Project project) {
        return Messages.showInputDialog(
                project,
                "Enter the file name:",
                "Create New File",
                Messages.getQuestionIcon(),
                "",
                null);
    }

    private void createNewFile(Project project, Path filePath, String fileContent) {
        ApplicationManager.getApplication()
                .runWriteAction(
                        () -> {
                            try {
                                VirtualFile targetDir =
                                        VfsUtil.createDirectories(filePath.getParent().toString());
                                VirtualFile newFile =
                                        targetDir.createChildData(
                                                this, filePath.getFileName().toString());
                                VfsUtil.saveText(newFile, fileContent);
                                FileEditorManager.getInstance(project).openFile(newFile, true);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
    }
}

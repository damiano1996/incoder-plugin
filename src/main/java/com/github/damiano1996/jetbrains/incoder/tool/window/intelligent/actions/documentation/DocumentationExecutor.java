package com.github.damiano1996.jetbrains.incoder.tool.window.intelligent.actions.documentation;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelService;
import com.github.damiano1996.jetbrains.incoder.tool.window.intelligent.actions.IntelligentActionExecutor;
import com.github.damiano1996.jetbrains.incoder.tool.window.intelligent.actions.IntelligentActionObserver;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestFactory;
import com.intellij.diff.InvalidDiffRequestException;
import com.intellij.icons.AllIcons;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class DocumentationExecutor implements IntelligentActionExecutor {

    @Nullable
    @Setter
    private IntelligentActionObserver observer;

    private static void createMergeRequest(VirtualFile file, String documentedCode, Project project) throws IOException, InvalidDiffRequestException {
        var contents = new ArrayList<byte[]>();
        contents.add(VfsUtilCore.loadText(file).getBytes());
        contents.add(documentedCode.getBytes());
        contents.add(documentedCode.getBytes());

        var mergeRequest =
                DiffRequestFactory.getInstance()
                        .createMergeRequest(
                                project,
                                file,
                                contents,
                                "InCoder Proposal",
                                List.of(
                                        "Original",
                                        "Result",
                                        "InCoder proposal"),
                                mergeResult ->
                                        log.debug(
                                                "Merge request"
                                                        + " result: {}",
                                                mergeResult));

        DiffManager.getInstance().showMerge(project, mergeRequest);
    }

    @Override
    public void execute() {
        VirtualFile selectedFolder = selectFolder();
        if (selectedFolder != null) {
            ProgressManager.getInstance().run(new Task.Backgroundable(null, "Documenting files") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    Map<VirtualFile, String> updatedFiles = documentFiles(selectedFolder, indicator);
                    // ApplicationManager.getApplication().invokeLater(() -> showMergeRequestDialog(updatedFiles));

                    if (observer != null) {
                        observer.onActionCompleted();
                    }
                }
            });
        }
    }

    private VirtualFile selectFolder() {
        FileChooserDescriptor descriptor =
                FileChooserDescriptorFactory.createSingleFolderDescriptor();
        return FileChooser.chooseFile(descriptor, null, null);
    }

    private @NotNull Map<VirtualFile, String> documentFiles(VirtualFile folder, @NotNull ProgressIndicator indicator) {
        Map<VirtualFile, String> updatedFiles = new HashMap<>();
        VfsUtilCore.iterateChildrenRecursively(
                folder,
                VirtualFileFilter.ALL,
                fileOrDir -> {
                    if (!fileOrDir.isDirectory()) {
                        String filePath = fileOrDir.getPath();
                        try {
                            var filename = Path.of(filePath).getFileName();
                            log.debug("Documenting {}", filePath);
                            if (observer != null) {
                                observer.onProgressUpdate("Documenting: %s".formatted(filename));
                            }
                            indicator.setText("Documenting: %s".formatted(filename));

                            String fileContent = VfsUtil.loadText(fileOrDir);
                            String documentedCode =
                                    LanguageModelService.getInstance(
                                                    Objects.requireNonNull(
                                                            ProjectUtil.getActiveProject()))
                                            .document(fileContent);
                            updatedFiles.put(fileOrDir, documentedCode);

                            if (observer != null) {

                                var mergeButton = new JButton(AllIcons.Vcs.Merge);
                                mergeButton.addActionListener(e -> {
                                    try {
                                        createMergeRequest(fileOrDir, documentedCode, ProjectUtil.getActiveProject());
                                    } catch (IOException | InvalidDiffRequestException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                });

                                observer.onGeneratedArtifact(FormBuilder.createFormBuilder()
                                        .setFormLeftIndent(20)
                                        .addLabeledComponent(new JBLabel(filename.toString()), mergeButton, 1, false)
                                        .addVerticalGap(20)
                                        .setFormLeftIndent(0)
                                        .getPanel());
                            }

                        } catch (IOException ex) {
                            log.error("Error reading file content: {}", filePath, ex);
                        }
                    }
                    return true;
                });
        return updatedFiles;
    }

    private void showMergeRequestDialog(@NotNull Map<VirtualFile, String> updatedFiles) {
        Project project = Objects.requireNonNull(ProjectUtil.getActiveProject());

        if (!updatedFiles.isEmpty()) {
            ApplicationManager.getApplication()
                    .invokeLater(
                            () -> {
                                for (Map.Entry<VirtualFile, String> entry :
                                        updatedFiles.entrySet()) {
                                    VirtualFile file = entry.getKey();
                                    String documentedCode = entry.getValue();

                                    try {
                                        createMergeRequest(file, documentedCode, project);

                                    } catch (InvalidDiffRequestException | IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
        } else {
            Messages.showMessageDialog(
                    "No files were documented.",
                    "No Files Documented",
                    Messages.getInformationIcon());
        }
    }
}

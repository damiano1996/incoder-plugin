package com.github.damiano1996.jetbrains.incoder.tool.window.intelligent.actions;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelService;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestFactory;
import com.intellij.diff.InvalidDiffRequestException;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class DocumentationIntelligentAction implements IntelligentAction {
    @Override
    public String getName() {
        return "Documentation";
    }

    @Override
    public String getDescription() {
        return "Automatically generates comprehensive documentation for your code, including class"
                + " descriptions, method summaries, and parameter explanations. Review and"
                + " refine the generated documentation before seamlessly integrating it into"
                + " your codebase.";
    }

    @Override
    public ActionListener getActionListener() {
        return e -> {
            VirtualFile selectedFolder = selectFolder();
            if (selectedFolder != null) {
                ApplicationManager.getApplication()
                        .invokeLater(
                                () -> {
                                    Map<VirtualFile, String> updatedFiles =
                                            documentFiles(selectedFolder);
                                    showMergeRequestDialog(updatedFiles);
                                });
            }
        };
    }

    private VirtualFile selectFolder() {
        FileChooserDescriptor descriptor =
                FileChooserDescriptorFactory.createSingleFolderDescriptor();
        return FileChooser.chooseFile(descriptor, null, null);
    }

    private @NotNull Map<VirtualFile, String> documentFiles(VirtualFile folder) {
        Map<VirtualFile, String> updatedFiles = new HashMap<>();
        VfsUtilCore.iterateChildrenRecursively(
                folder,
                VirtualFileFilter.ALL,
                fileOrDir -> {
                    if (!fileOrDir.isDirectory()) {
                        String filePath = fileOrDir.getPath();
                        try {
                            log.debug("Documenting {}", filePath);
                            String fileContent = VfsUtil.loadText(fileOrDir);
                            String documentedCode =
                                    LanguageModelService.getInstance(
                                                    Objects.requireNonNull(
                                                            ProjectUtil.getActiveProject()))
                                            .document(fileContent);
                            updatedFiles.put(fileOrDir, documentedCode);
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

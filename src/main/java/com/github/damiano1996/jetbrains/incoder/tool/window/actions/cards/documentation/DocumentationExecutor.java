package com.github.damiano1996.jetbrains.incoder.tool.window.actions.cards.documentation;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelService;
import com.github.damiano1996.jetbrains.incoder.notification.NotificationService;
import com.github.damiano1996.jetbrains.incoder.tool.window.actions.cards.IntelligentActionExecutor;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestFactory;
import com.intellij.diff.InvalidDiffRequestException;
import com.intellij.icons.AllIcons;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DescriptionLabel;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class DocumentationExecutor implements IntelligentActionExecutor {

    private static void createMergeRequest(
            VirtualFile file, @NotNull String documentedCode, Project project) {
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
                                    List.of("Original", "Result", "InCoder proposal"),
                                    mergeResult ->
                                            log.debug("Merge request result: {}", mergeResult));

            DiffManager.getInstance().showMerge(project, mergeRequest);
        } catch (InvalidDiffRequestException e) {
            NotificationService.getInstance(project)
                    .notifyError(
                            "Unable to create merge request for %s"
                                    .formatted(Path.of(file.getPath()).getFileName()));
        } catch (IOException e) {
            NotificationService.getInstance(project)
                    .notifyError(
                            "Unable to read file content of %s"
                                    .formatted(Path.of(file.getPath()).getFileName()));
        }
    }

    private static @NotNull List<VirtualFile> getFilesToDocument(VirtualFile folder) {
        List<VirtualFile> files = new LinkedList<>();
        VfsUtilCore.iterateChildrenRecursively(
                folder,
                VirtualFileFilter.ALL,
                fileOrDir -> {
                    if (!fileOrDir.isDirectory()) {
                        files.add(fileOrDir);
                    }
                    return true;
                });
        return files;
    }

    @Override
    public CompletableFuture<Void> execute(
            Consumer<JComponent> componentConsumer, Supplier<Boolean> stopCondition) {
        Project project = Objects.requireNonNull(ProjectUtil.getActiveProject());

        VirtualFile selectedFolder = selectFolder(project);
        if (selectedFolder != null) {
            return CompletableFuture.supplyAsync(
                    () -> {
                        documentFiles(project, selectedFolder, componentConsumer, stopCondition);
                        return null;
                    });
        } else {
            return CompletableFuture.supplyAsync(() -> null);
        }
    }

    private VirtualFile selectFolder(Project project) {
        FileChooserDescriptor descriptor =
                FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle("Select the Folder to Document");
        descriptor.setDescription(
                "All files inside the selected folder and its subfolders will be documented.");
        VirtualFile projectBaseDir =
                com.intellij.openapi.project.ProjectUtil.guessProjectDir(project);
        descriptor.setRoots(projectBaseDir);
        return FileChooser.chooseFile(descriptor, project, projectBaseDir);
    }

    private void documentFiles(
            Project project,
            VirtualFile folder,
            @NotNull Consumer<JComponent> componentConsumer,
            Supplier<Boolean> cancellationCondition) {
        List<DocumentationFileEntry> fileEntries =
                getFilesToDocument(folder).stream()
                        .map(virtualFile -> new DocumentationFileEntry(project, virtualFile))
                        .toList();

        var progressLabel = new JBLabel("");
        var progressBar = getProgressBar(fileEntries);

        var basePath = Path.of(folder.getPath());
        var panel = getFormPanel(progressLabel, progressBar, basePath, fileEntries);
        componentConsumer.accept(panel);

        for (DocumentationFileEntry documentationFileEntry : fileEntries) {
            if (cancellationCondition.get()) break;

            String logMessage =
                    "Documenting ( %d/%d ) %s..."
                            .formatted(
                                    progressBar.getValue() + 1,
                                    fileEntries.size(),
                                    documentationFileEntry.getVirtualFile().getPath());
            progressLabel.setText(logMessage);

            generateDocumentation(project, documentationFileEntry);

            progressBar.setValue(progressBar.getValue() + 1);
        }

        progressBar.setVisible(false);
        progressLabel.setText("Documentations are ready. Review them to merge changes.");
    }

    private static @NotNull JProgressBar getProgressBar(
            @NotNull List<DocumentationFileEntry> fileEntries) {
        var progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setMaximum(0);
        progressBar.setMaximum(fileEntries.size());
        progressBar.setValue(0);
        return progressBar;
    }

    private static JPanel getFormPanel(
            JBLabel progressLabel,
            JProgressBar progressBar,
            @NotNull Path basePath,
            @NotNull List<DocumentationFileEntry> fileEntries) {
        var formBuilder =
                FormBuilder.createFormBuilder()
                        .addComponent(progressLabel)
                        .addComponent(progressBar)
                        .addVerticalGap(20)
                        .addComponent(new DescriptionLabel(basePath.toString()));

        Set<Path> addedFolders = new HashSet<>();
        addedFolders.add(basePath);

        for (DocumentationFileEntry documentationFileEntry : fileEntries) {
            var filePath = Path.of(documentationFileEntry.getVirtualFile().getPath());

            addTreeStructure(filePath, addedFolders, formBuilder, basePath);

            formBuilder
                    .setFormLeftIndent((filePath.getNameCount() - basePath.getNameCount()) * 30)
                    .addLabeledComponent(
                            new JBLabel(documentationFileEntry.getFilename()),
                            documentationFileEntry.mergeButton,
                            0,
                            false)
                    .setFormLeftIndent(0);
        }

        var panel = formBuilder.getPanel();
        return panel;
    }

    private static void addTreeStructure(
            @NotNull Path filePath,
            @NotNull Set<Path> addedFolders,
            FormBuilder formBuilder,
            Path basePath) {
        var pathLabel = filePath.getParent();
        List<Path> toBeAdded = new ArrayList<>();
        while (!addedFolders.contains(pathLabel)) {
            toBeAdded.add(0, pathLabel);
            pathLabel = pathLabel.getParent();
        }

        toBeAdded.forEach(
                folderName -> {
                    formBuilder
                            .setFormLeftIndent(
                                    (folderName.getNameCount() - basePath.getNameCount()) * 30)
                            .addComponent(new DescriptionLabel("└── " + folderName.getFileName()));

                    addedFolders.add(folderName);
                });
    }

    private static void generateDocumentation(
            Project project, @NotNull DocumentationFileEntry documentationFileEntry) {
        try {
            String fileContent = VfsUtil.loadText(documentationFileEntry.getVirtualFile());
            String documentedFile = LanguageModelService.getInstance(project).document(fileContent);
            documentationFileEntry.updateWithDocumentation(documentedFile);
        } catch (IOException e) {
            log.error("Unable to document the file.", e);
            NotificationService.getInstance(project)
                    .notifyError(
                            "Something went wrong... Unable to document the file %s"
                                    .formatted(documentationFileEntry.getFilename()));
        }
    }

    @Getter
    private static class DocumentationFileEntry {

        private final Project project;
        private final String filename;
        private final VirtualFile virtualFile;
        private final JButton mergeButton;

        private DocumentationFileEntry(Project project, @NotNull VirtualFile virtualFile) {
            this.project = project;
            this.virtualFile = virtualFile;

            filename = Path.of(virtualFile.getPath()).getFileName().toString();

            mergeButton = new JButton("Merge...", AllIcons.Vcs.Merge);
            mergeButton.setEnabled(false);
        }

        public void updateWithDocumentation(String documentedContent) {
            mergeButton.addActionListener(
                    e -> createMergeRequest(virtualFile, documentedContent, project));
            mergeButton.setEnabled(true);
        }
    }
}

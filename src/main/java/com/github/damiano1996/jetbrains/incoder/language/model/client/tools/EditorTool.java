package com.github.damiano1996.jetbrains.incoder.language.model.client.tools;

import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestFactory;
import com.intellij.diff.InvalidDiffRequestException;
import com.intellij.diff.merge.TextMergeRequest;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@AllArgsConstructor
public class EditorTool {

    private Project project;

    @Tool("Get the content of the file the user is watching")
    public String getFileContent() {
        log.debug("Getting current code from active editor");
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            String code = editor.getDocument().getText();
            log.debug("Successfully retrieved current code, length: {} characters", code.length());
            return code;
        } else {
            log.warn("No active editor found, unable to retrieve current code");
            return "Error: unable to read the content of the document that the user is reading.";
        }
    }

    @Tool("Get the path of the file the user is watching")
    public String getFilePath() {
        try {
            log.debug("Getting current file path from active editor");
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor != null) {
                String filePath = editor.getVirtualFile().getPath();
                log.debug("Successfully retrieved current file path: {}", filePath);
                return filePath;
            } else {
                log.warn("No active editor found, unable to retrieve current file path");
                return "Error: unable to get the path of the file the user is reading.";
            }
        } catch (Exception e) {
            return "Error: %s".formatted(e.getMessage());
        }
    }

    @Tool("Show a diff/merge view between the original file and proposed changes")
    public String showDiff(
            @P("Absolute file path of the file to compare") String filePath,
            @P("The new/modified content to compare against the original file") String proposedContent) {
        try {
            log.debug("Showing diff for file: {}", filePath);

            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
            if (virtualFile == null) {
                return "Error: File not found at path: " + filePath;
            }

            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
            if (document == null) {
                return "Error: Unable to get document for file: " + filePath;
            }

            String originalContent = document.getText();
            CompletableFuture<String> resultFuture = new CompletableFuture<>();

            var textMergeRequest = showDiffWithProposedChange(
                    project,
                    virtualFile,
                    originalContent,
                    proposedContent,
                    resultFuture);

            ApplicationManager.getApplication().runReadAction(() -> DiffManager.getInstance().showMerge(project, textMergeRequest));

            // Block until user makes a decision
            String result = resultFuture.get();
            log.debug("User action completed: {}", result);
            return result;

        } catch (Exception e) {
            log.error("Error showing diff for file: {}", filePath, e);
            return "Error showing diff: " + e.getMessage();
        }
    }

    private @NotNull TextMergeRequest showDiffWithProposedChange(
            Project project,
            VirtualFile originalFile,
            @NotNull String originalContent,
            @NotNull String proposedContent,
            CompletableFuture<String> resultFuture)
            throws InvalidDiffRequestException {

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
                        mergeResult -> {
                            log.debug("Merge request result: {}", mergeResult);
                            String resultMessage = switch (mergeResult) {
                                case RESOLVED -> "User accepted and resolved the merge successfully";
                                case CANCEL -> "User cancelled the merge operation";
                                case LEFT -> "User chose to keep the original content";
                                case RIGHT -> "User chose to accept the proposed changes";
                            };
                            resultFuture.complete(resultMessage);
                        });
    }

}

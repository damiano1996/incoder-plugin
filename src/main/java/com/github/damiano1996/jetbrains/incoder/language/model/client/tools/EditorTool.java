package com.github.damiano1996.jetbrains.incoder.language.model.client.tools;

import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestFactory;
import com.intellij.diff.InvalidDiffRequestException;
import com.intellij.diff.merge.MergeResult;
import com.intellij.diff.merge.TextMergeRequest;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Consumer;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
@AllArgsConstructor
public class EditorTool {

    private Project project;

    @Tool("Show a diff/merge view between the original file and proposed changes")
    public String showDiff(
            @P("Absolute file path of the file to compare") String filePath,
            @P("The new/modified content to compare against the original file")
                    String proposedContent) {
        try {
            log.debug("Showing diff for file: {}", filePath);

            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
            if (virtualFile == null) {
                return "Error: File not found at path: " + filePath;
            }

            String originalContent = getOriginalContent(filePath, virtualFile);

            final CompletableFuture<String> resultMessage = new CompletableFuture<>();

            Consumer<MergeResult> mergeResultConsumer =
                    mergeResult -> {
                        log.debug("Merge request result: {}", mergeResult);
                        resultMessage.complete(getMergeResultMessage(mergeResult));
                    };

            TextMergeRequest textMergeRequest =
                    showDiffWithProposedChange(
                            project,
                            virtualFile,
                            originalContent,
                            proposedContent,
                            mergeResultConsumer);

            ApplicationManager.getApplication()
                    .invokeLater(
                            () -> DiffManager.getInstance().showMerge(project, textMergeRequest));

            return resultMessage.get();

        } catch (Throwable e) {
            log.error("Error showing diff for file: {}", filePath, e);
            return "Error showing diff: " + e.getMessage();
        }
    }

    private static String getOriginalContent(String filePath, VirtualFile virtualFile)
            throws Throwable {
        return ApplicationManager.getApplication()
                .runReadAction(
                        (ThrowableComputable<String, Throwable>)
                                () -> {
                                    Document document =
                                            FileDocumentManager.getInstance()
                                                    .getDocument(virtualFile);
                                    if (document == null) {
                                        throw new IllegalArgumentException(
                                                "Unable to get document for file: " + filePath);
                                    }
                                    return document.getText();
                                });
    }

    private @NotNull TextMergeRequest showDiffWithProposedChange(
            Project project,
            VirtualFile originalFile,
            @NotNull String originalContent,
            @NotNull String proposedContent,
            @Nullable Consumer<? super MergeResult> mergeResultConsumer)
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
                        mergeResultConsumer);
    }

    @Contract(pure = true)
    private @NotNull String getMergeResultMessage(@NotNull MergeResult mergeResult) {
        return switch (mergeResult) {
            case RESOLVED -> "Changes successfully merged and applied to the file";
            case CANCEL -> "Merge operation was cancelled - no changes were made";
            case LEFT -> "Original content preserved - proposed changes were rejected";
            case RIGHT -> "Proposed changes fully accepted and applied to the file";
        };
    }
}

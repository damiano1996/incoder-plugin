package com.github.damiano1996.jetbrains.incoder.language.model.client.tools;

import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestFactory;
import com.intellij.diff.InvalidDiffRequestException;
import com.intellij.diff.merge.MergeResult;
import com.intellij.diff.merge.TextMergeRequest;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Consumer;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.nio.charset.StandardCharsets;
import java.util.*;
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

    public record PatchHunk(
            int startLine,
            int endLine,
            String oldContent,
            String newContent
    ) {}

    @Tool("""
        Creates and applies a unified diff patch to a file, presenting changes in a merge dialog for user review and approval.
        This tool allows precise code modifications with visual diff comparison before applying changes.""")
    public String createPatch(
            @P("""
                Absolute file path to the target file that needs to be modified.
                Must be a valid file path within the project (e.g., /path/to/project/src/main/java/MyClass.java).
                Use forward slashes for path separators regardless of operating system.""")
            String filePath,
            @P("""
                A list of PatchHunk objects containing the code changes to apply:
                - startLine: 1-based line number where the change begins (inclusive)
                - endLine: 1-based line number where the change ends (inclusive)
                - oldContent: Exact original content that will be replaced (used for verification)
                - newContent: New content to replace the old content
                
                Best practices:
                - Keep hunks small and focused (5-20 lines) for clarity
                - Ensure oldContent exactly matches the current file content
                - Line numbers should be accurate to avoid conflicts""")
            List<PatchHunk> patchHunks
    ) {
        try {
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
            if (virtualFile == null) {
                return "Error: File not found at path: %s".formatted(filePath);
            }

            String originalContent = getOriginalContent(virtualFile);

            String proposedContent = applyPatches(originalContent, patchHunks);

            return showDiff(filePath, proposedContent);

        } catch (Throwable e) {
            String errorMessage = "Unexpected error while applying patches to file %s: %s".formatted(filePath, e.getMessage());
            log.error(errorMessage, e);
            return errorMessage;
        }
    }

    private @NotNull String applyPatches(@NotNull String originalContent, @NotNull List<PatchHunk> patchHunks) {
        List<String> lines = new ArrayList<>(Arrays.asList(
                originalContent.split("\\R", -1)
        ));

        patchHunks.sort(Comparator
                .comparingInt(PatchHunk::startLine)
                .reversed()
        );

        for (PatchHunk h : patchHunks) {
            int startIdx = h.startLine() - 1;
            int endIdx   = h.endLine()   - 1;

            if (startIdx < 0 || endIdx >= lines.size() || startIdx > endIdx) {
                throw new IllegalArgumentException(
                        "PatchHunk out of bounds: " + h.startLine() + "-" + h.endLine()
                );
            }

            for (int i = 0; i <= endIdx - startIdx; i++) {
                lines.remove(startIdx);
            }

            String[] newLines = h.newContent().split("\\R", -1);
            for (int i = 0; i < newLines.length; i++) {
                lines.add(startIdx + i, newLines[i]);
            }
        }

        return String.join("\n", lines);
    }


    public String showDiff(
            String filePath,
            String proposedContent) {
        try {
            log.debug("Showing diff for file: {}", filePath);

            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
            if (virtualFile == null) {
                return "Error: File not found at path: %s".formatted(filePath);
            }

            String originalContent = getOriginalContent(virtualFile);

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
            return "Error showing diff: %s".formatted(e.getMessage());
        }
    }

    private static String getOriginalContent(VirtualFile virtualFile)
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
                                                "Unable to get document for file: %s".formatted(virtualFile.getPath()));
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

        var localBytes = originalContent   .getBytes(StandardCharsets.UTF_8);
        var baseBytes  = originalContent   .getBytes(StandardCharsets.UTF_8);
        var rightBytes = proposedContent   .getBytes(StandardCharsets.UTF_8);

        return DiffRequestFactory.getInstance()
                .createTextMergeRequest(
                        project,
                        originalFile,
                        List.of(localBytes, baseBytes, rightBytes),
                        "Merge InCoder Proposal",
                        List.of("Local version", "Original base", "InCoder proposal"),
                        mergeResultConsumer
                );

    }

    @Contract(pure = true)
    private @NotNull String getMergeResultMessage(@NotNull MergeResult mergeResult) {
        return switch (mergeResult) {
            case RESOLVED -> "Changes successfully merged and applied to the file";
            case CANCEL -> "Merge operation was cancelled by the user - no changes were made";
            case LEFT -> "Original content preserved - proposed changes were rejected";
            case RIGHT -> "Proposed changes fully accepted and applied to the file";
        };
    }
}

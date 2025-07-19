package com.github.damiano1996.jetbrains.incoder.language.model.client.tools;

import com.github.damiano1996.jetbrains.incoder.diff.DiffUtils;
import com.intellij.diff.DiffManager;
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
import dev.langchain4j.model.output.structured.Description;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Slf4j
@AllArgsConstructor
public class EditorTool {

    private Project project;

    private static String getOriginalContent(VirtualFile virtualFile) throws Throwable {
        return ApplicationManager.getApplication()
                .runReadAction(
                        (ThrowableComputable<String, Throwable>)
                                () -> {
                                    Document document =
                                            FileDocumentManager.getInstance()
                                                    .getDocument(virtualFile);
                                    if (document == null) {
                                        throw new IllegalArgumentException(
                                                "Unable to get document for file: %s"
                                                        .formatted(virtualFile.getPath()));
                                    }
                                    return document.getText();
                                });
    }

    @Tool(
            name = "CREATE_PATCH",
            value =
                    """
Creates and applies a unified diff patch to a file, presenting changes in a merge dialog for user review and approval.
This tool allows precise code modifications with visual diff comparison before applying changes.
""")
    public String createPatch(
            @P(
                            """
Absolute file path to the target file that needs to be modified.
Must be a valid file path within the project (e.g., /path/to/project/src/main/java/MyClass.java).
Use forward slashes for path separators regardless of operating system.
""")
                    String filePath,
            @P(
                            """
A list of PatchHunk objects representing targeted code modifications. Each PatchHunk defines:
- Precise line range for modifications (startLine and endLine)
- Original content to be replaced (oldContent)
- New content to replace the original (newContent)

Key characteristics:
- Line numbers are 1-based and inclusive
- Hunks are applied in reverse order to prevent line number shifting
- Allows granular, controlled code changes with explicit content verification
- Supports multiple, non-overlapping modifications in a single patch operation

Example structure:
[
  PatchHunk(5, 7, "original method body", "updated method implementation"),
  PatchHunk(12, 12, "single line to replace", "new line of code")
]
""")
                    List<PatchHunk> patchHunks) {
        try {
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
            if (virtualFile == null) {
                return "Error: File not found at path: %s".formatted(filePath);
            }

            String originalContent = getOriginalContent(virtualFile);

            String proposedContent = applyPatches(originalContent, patchHunks);

            return showDiff(filePath, proposedContent);

        } catch (Throwable e) {
            String errorMessage =
                    "Unexpected error while applying patches to file %s: %s"
                            .formatted(filePath, e.getMessage());
            log.error(errorMessage, e);
            return errorMessage;
        }
    }

    private @NotNull String applyPatches(
            @NotNull String originalContent, @NotNull List<PatchHunk> patchHunks) {
        List<String> lines = new ArrayList<>(Arrays.asList(originalContent.split("\\R", -1)));

        patchHunks.sort(Comparator.comparingInt(PatchHunk::startLine).reversed());

        for (PatchHunk h : patchHunks) {
            int startIdx = h.startLine() - 1;
            int endIdx = h.endLine() - 1;

            if (startIdx < 0 || endIdx >= lines.size() || startIdx > endIdx) {
                throw new IllegalArgumentException(
                        "PatchHunk out of bounds: %d-%d".formatted(h.startLine(), h.endLine()));
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

    public String showDiff(String filePath, String proposedContent) {
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
                    DiffUtils.showDiffWithProposedChange(
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

    @Contract(pure = true)
    private @NotNull String getMergeResultMessage(@NotNull MergeResult mergeResult) {
        return switch (mergeResult) {
            case RESOLVED -> "Changes successfully merged and applied to the file";
            case CANCEL -> "Merge operation was cancelled by the user - no changes were made";
            case LEFT -> "Original content preserved - proposed changes were rejected";
            case RIGHT -> "Proposed changes fully accepted and applied to the file";
        };
    }

    public record PatchHunk(
            @Description(
                            """
The starting line number for the patch hunk in the file.
Must be a 1-based line number indicating where the changes begin.
Line numbers are inclusive and should accurately reflect the file's current state.
Example: For the first line of a file, use 1; for the fifth line, use 5.
""")
                    int startLine,
            @Description(
                            """
The ending line number for the patch hunk in the file.
Must be a 1-based line number indicating where the changes end.
Line numbers are inclusive and should match the exact range of content to be replaced.
Must be greater than or equal to the startLine.
Example: If replacing lines 5-7, set startLine to 5 and endLine to 7.
""")
                    int endLine,
            @Description(
                            """
The exact original content to be replaced in the specified line range.
This serves as a verification mechanism to ensure the patch is applied to the correct content.
Must exactly match the current content in the file between startLine and endLine.
Used to prevent unintended modifications if the file content has changed.
Example: If replacing a method body, provide the exact current method body text.
""")
                    String oldContent,
            @Description(
                            """
The new content that will replace the oldContent in the specified line range.
Should be a complete and valid code snippet that will seamlessly replace the original content.
Ensure proper formatting, indentation, and syntax to maintain code readability.
Example: A new method implementation, refactored code block, or updated logic.
""")
                    String newContent) {}
}

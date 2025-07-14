package com.github.damiano1996.jetbrains.incoder.diff;

import com.intellij.diff.DiffRequestFactory;
import com.intellij.diff.InvalidDiffRequestException;
import com.intellij.diff.merge.MergeResult;
import com.intellij.diff.merge.TextMergeRequest;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Consumer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for handling diff operations and merge requests. Provides methods to create and
 * display diff views for code changes.
 */
@UtilityClass
public class DiffUtils {

    /**
     * Creates a text merge request to show a diff between original content and proposed changes.
     * This method sets up a three-way merge view where users can compare the local version,
     * original base, and the InCoder proposal.
     *
     * @param project the current project context
     * @param originalFile the virtual file being modified
     * @param oldContent the original content of the file
     * @param newContent the proposed new content from InCoder
     * @param mergeResultConsumer optional consumer to handle the merge result when user completes
     *     the merge
     * @return a TextMergeRequest that can be used to display the diff/merge dialog
     * @throws InvalidDiffRequestException if the diff request cannot be created due to invalid
     *     parameters
     */
    public @NotNull TextMergeRequest showDiffWithProposedChange(
            Project project,
            VirtualFile originalFile,
            @NotNull String oldContent,
            @NotNull String newContent,
            @Nullable Consumer<? super MergeResult> mergeResultConsumer)
            throws InvalidDiffRequestException {

        var localBytes = oldContent.getBytes(StandardCharsets.UTF_8);
        var baseBytes = oldContent.getBytes(StandardCharsets.UTF_8);
        var rightBytes = newContent.getBytes(StandardCharsets.UTF_8);

        return DiffRequestFactory.getInstance()
                .createTextMergeRequest(
                        project,
                        originalFile,
                        List.of(localBytes, baseBytes, rightBytes),
                        "Merge InCoder Proposal",
                        List.of("Local version", "Original base", "InCoder proposal"),
                        mergeResultConsumer);
    }
}

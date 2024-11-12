package com.github.damiano1996.intellijplugin.incoder.generation;

import com.github.damiano1996.intellijplugin.incoder.llm.LlmService;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestFactory;
import com.intellij.diff.InvalidDiffRequestException;
import com.intellij.diff.merge.TextMergeRequest;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Getter
@Service(Service.Level.PROJECT)
@Slf4j
public final class CodeGenerationService {

    private final Project project;

    public CodeGenerationService(Project project) {
        this.project = project;
    }

    public static CodeGenerationService getInstance(@NotNull Project project) {
        return project.getService(CodeGenerationService.class);
    }

    public void updateCode(String prompt) {
        log.debug("Going to update the code based on the user prompt: {}", prompt);
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        var actualCode = Objects.requireNonNull(editor).getDocument().getText();
        log.debug("Virtual file name: {}", editor.getVirtualFile().getName());

        LlmService.getInstance(project)
                .getGenerationStream(
                        new CodeGenerationContext(editor.getVirtualFile(), prompt, actualCode))
                .onNext(token -> {})
                .onComplete(
                        updatedCode ->
                                ApplicationManager.getApplication()
                                        .invokeLater(() -> showDiff(updatedCode, editor)))
                .onError(throwable -> {})
                .start();
    }

    private void showDiff(String newCode, @NotNull Editor editor) {
        try {
            Document originalDoc =
                    FileDocumentManager.getInstance().getDocument(editor.getVirtualFile());
            var textMergeRequest =
                    showDiffWithProposedChange(
                            project,
                            editor.getVirtualFile(),
                            Objects.requireNonNull(originalDoc).getText(),
                            newCode);
            DiffManager.getInstance().showMerge(project, textMergeRequest);
        } catch (IOException | InvalidDiffRequestException e) {
            throw new RuntimeException(e);
        }
    }

    public @NotNull TextMergeRequest showDiffWithProposedChange(
            Project project,
            VirtualFile originalFile,
            @NotNull String originalContent,
            @NotNull String proposedContent)
            throws IOException, InvalidDiffRequestException {

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
                        mergeResult -> log.debug("Merge request result: {}", mergeResult));
    }
}

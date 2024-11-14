package com.github.damiano1996.intellijplugin.incoder.generation;

import com.github.damiano1996.intellijplugin.incoder.llm.LlmService;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestFactory;
import com.intellij.diff.InvalidDiffRequestException;
import com.intellij.diff.merge.TextMergeRequest;
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

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Deprecated
@Service(Service.Level.PROJECT)
@Slf4j
public final class CodeGenerationService {

    private final Project project;
//    private final List<CodeGeneratorListener> listeners = new ArrayList<>();

    public CodeGenerationService(Project project) {
        this.project = project;
    }

    public static CodeGenerationService getInstance(@NotNull Project project) {
        return project.getService(CodeGenerationService.class);
    }
//
//    public void add(CodeGeneratorListener listener){
//        listeners.add(listener);
//    }
//
//    public void send(String prompt) {
//        log.debug("Going to update the code based on the user prompt: {}", prompt);
//        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
//        var actualCode = Objects.requireNonNull(editor).getDocument().getText();
//        log.debug("Virtual file name: {}", editor.getVirtualFile().getName());
//
//        LlmService.getInstance(project).getCodeUpdate(new PromptRequest(editor, prompt))
//                .thenAccept(codeUpdateResponse -> listeners.forEach(listener -> listener.onCodeGeneration(codeUpdateResponse)));
//
////        var generationResponse =
////                LlmService.getInstance(project)
////                        .getCodeUpdate(
////                                new CodeGenerationContext(
////                                        editor.getVirtualFile(), prompt, actualCode));
//
////        ApplicationManager.getApplication()
////                .invokeLater(() -> showDiff(generationResponse.updatedCode(), editor));
//
//        // return generationResponse;
//    }

    public static void showDiff(Project project, String newCode, @NotNull Editor editor) {
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

    public static @NotNull TextMergeRequest showDiffWithProposedChange(
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

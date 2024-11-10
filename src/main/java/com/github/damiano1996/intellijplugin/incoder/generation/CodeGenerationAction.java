package com.github.damiano1996.intellijplugin.incoder.generation;

import com.github.damiano1996.intellijplugin.incoder.llm.LlmService;
import com.github.damiano1996.intellijplugin.incoder.notification.NotificationService;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Service(Service.Level.PROJECT)
@Slf4j
public final class CodeGenerationAction {

    @Getter
    private final Project project;

    public CodeGenerationAction(Project project) {
        this.project = project;
    }

    public static CodeGenerationAction getInstance(@NotNull Project project) {
        return project.getService(CodeGenerationAction.class);
    }

    public void updateCode(String prompt) {
        try {
            log.debug("Going to update the code based on the user prompt: {}", prompt);
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            var actualCode = Objects.requireNonNull(editor).getDocument().getText();
            var updatedCode = LlmService.getInstance(project).generate(new CodeGenerationContext(prompt, actualCode));
            log.debug("New code version received: {}", updatedCode);
            WriteCommandAction.runWriteCommandAction(project, () -> {
                editor.getDocument().setText(updatedCode);
                log.debug("Editor updated");
            });
        } catch (CodeGenerationException e) {
            NotificationService.getInstance(project).notifyError(e.getMessage());
        }
    }

}

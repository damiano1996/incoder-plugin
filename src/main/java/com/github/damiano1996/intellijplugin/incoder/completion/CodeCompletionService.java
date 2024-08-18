package com.github.damiano1996.intellijplugin.incoder.completion;

import com.github.damiano1996.intellijplugin.incoder.completion.states.State;
import com.github.damiano1996.intellijplugin.incoder.completion.states.idle.IdleState;
import com.github.damiano1996.intellijplugin.incoder.llm.LlmService;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Service(Service.Level.PROJECT)
@Slf4j
public final class CodeCompletionService
        implements AnActionListener,
                CodeCompletionListener,
                DocumentListener,
                EditorMouseListener,
                Disposable {

    @Getter private final Project project;
    private State state;

    public CodeCompletionService(Project project) {
        this.project = project;
        this.state = new IdleState(this);
    }

    public static CodeCompletionService getInstance(@NotNull Project project) {
        return project.getService(CodeCompletionService.class);
    }

    public void init() {
        log.debug("Initializing ");

        log.debug("Adding listener for document");
        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(this, this);
        log.debug("Adding listeners for mouse");
        EditorFactory.getInstance().getEventMulticaster().addEditorMouseListener(this, this);
        log.debug("Subscribing to client service");
        LlmService.getInstance(this.project).subscribe(this);
    }

    public void next(@NotNull State state) {
        this.state = state;
    }

    public @Nullable Editor getEditor() {
        return FileEditorManager.getInstance(project).getSelectedTextEditor();
    }

    @Override
    public void onCodeCompletionPrediction(String prediction) {
        log.debug(
                "Prediction received: {}. Going to execute state {}",
                prediction,
                getStateSimpleName());
        this.state.onCodeCompletionPrediction(prediction);
    }

    @Override
    public void onCodeCompletionError(Throwable throwable) {
        this.state.onCodeCompletionError(throwable);
    }

    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        log.debug("Action performed. Going to execute state {}", getStateSimpleName());
        this.state.actionPerformed(anActionEvent);
    }

    @Override
    public void beforeActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event) {
        log.debug("Before action performed. Going to execute state {}", getStateSimpleName());
        this.state.beforeActionPerformed(action, event);
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        log.debug("Document has changed. Going to execute state {}", getStateSimpleName());
        this.state.documentChanged(event);
    }

    @Override
    public void mouseClicked(@NotNull EditorMouseEvent event) {
        EditorMouseListener.super.mouseClicked(event);
        log.debug("Mouse clicked. Going to execute state {}", getStateSimpleName());

        this.state.mouseClicked(event);
    }

    private @NotNull String getStateSimpleName() {
        return state.getClass().getSimpleName();
    }

    @Override
    public void dispose() {}
}

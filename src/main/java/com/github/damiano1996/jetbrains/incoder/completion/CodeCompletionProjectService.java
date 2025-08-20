package com.github.damiano1996.jetbrains.incoder.completion;

import com.github.damiano1996.jetbrains.incoder.completion.states.State;
import com.github.damiano1996.jetbrains.incoder.completion.states.idle.IdleState;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelProjectService;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.InlineLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.settings.InlineSettings;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import java.util.concurrent.CompletableFuture;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
public final class CodeCompletionProjectService implements CodeCompletionService, Disposable {

    private final Project project;
    private final CodeCompletionQueue codeCompletionQueue;
    private State state;

    @Setter private InlineLanguageModelClient inlineLanguageModelClient;

    public CodeCompletionProjectService(Project project) {
        this.project = project;
        this.state = new IdleState(this);

        log.debug("Staring async code completion queue...");
        codeCompletionQueue = new CodeCompletionQueue(project, this);
        CompletableFuture.runAsync(codeCompletionQueue);

        log.debug("Initializing {}...", CodeCompletionProjectService.class.getSimpleName());
        log.debug("Adding listener for document");
        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(this, this);
        log.debug("Adding listeners for mouse");
        EditorFactory.getInstance().getEventMulticaster().addEditorMouseListener(this, this);
    }

    public static CodeCompletionService getInstance(@NotNull Project project) {
        return project.getService(CodeCompletionService.class);
    }

    @Override
    public InlineLanguageModelClient getOrCreateInlineLanguageModelClient()
            throws LanguageModelException {
        if (inlineLanguageModelClient == null) {
            inlineLanguageModelClient =
                    LanguageModelProjectService.getInstance(project)
                            .createInlineClientWithDefaultSettings(
                                    InlineSettings.getInstance()
                                            .getState()
                                            .getSelectedLanguageModelParameters())
                            .compute();
        }

        return inlineLanguageModelClient;
    }

    public void next(@NotNull State state) {
        this.state = state;
    }

    public void enqueue(CodeCompletionContext codeCompletionContext) {
        codeCompletionQueue.enqueue(codeCompletionContext);
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

    @Override
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
        log.debug("Mouse clicked. Going to execute state {}", getStateSimpleName());
        this.state.mouseClicked(event);
    }

    private @NotNull String getStateSimpleName() {
        return state.getClass().getSimpleName();
    }

    @Override
    public void dispose() {}
}

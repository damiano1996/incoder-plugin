package com.github.damiano1996.intellijplugin.incoder.completion.states;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
@AllArgsConstructor
public abstract class BaseState implements State {

    protected final CodeCompletionService codeCompletionService;

    @Override
    public void onCodeCompletionPrediction(String prediction) {
        log.debug("Prediction received: '{}'. Executing state: {}", prediction, getClassName());
    }

    @Override
    public void onCodeCompletionError(@NotNull Throwable throwable) {
        log.debug(
                "Prediction error received: '{}'. Executing state: {}",
                throwable.getMessage(),
                getClassName());
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        log.debug("Document changed. Executing state: {}", getClassName());
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        log.debug("Action performed. Executing state: {}", getClassName());
    }

    @Override
    public void beforeActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event) {
        State.super.beforeActionPerformed(action, event);
        log.debug("Before action performed. Executing state: {}", getClassName());
    }

    @Override
    public void mouseClicked(@NotNull EditorMouseEvent event) {
        State.super.mouseClicked(event);
        log.debug("Mouse clicked. Executing state: {}", getClassName());
    }

    private @NotNull String getClassName() {
        return this.getClass().getSimpleName();
    }
}

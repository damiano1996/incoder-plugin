package com.github.damiano1996.intellijplugin.incoder.completion.states.decision;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionService;
import com.github.damiano1996.intellijplugin.incoder.completion.states.BaseState;
import com.github.damiano1996.intellijplugin.incoder.completion.states.idle.IdleState;
import com.github.damiano1996.intellijplugin.incoder.completion.states.preview.PreviewState;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.project.Project;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class DecisionState extends BaseState {

    private final String prediction;

    public DecisionState(CodeCompletionService codeCompletionService, String prediction) {
        super(codeCompletionService);
        this.prediction = prediction;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        super.actionPerformed(anActionEvent);

        handleAction(anActionEvent);
    }

    @Override
    public void beforeActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event) {
        super.beforeActionPerformed(action, event);

        handleAction(event);
    }

    @Override
    public void mouseClicked(@NotNull EditorMouseEvent event) {
        super.mouseClicked(event);

        ignorePrediction(event);
    }

    private void ignorePrediction(EditorMouseEvent event) {
        log.debug("Ignoring prediction due to mouse click");
        codeCompletionService.next(new IdleState(codeCompletionService));
        codeCompletionService.mouseClicked(event);
    }

    private void handleAction(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        Editor editor = event.getData(CommonDataKeys.EDITOR);

        if (project == null || editor == null) {
            log.debug("Project, or editor, was null. Returning");
            return;
        }

        handleEvent(event, project, editor);
    }

    private void handleEvent(@NotNull AnActionEvent anActionEvent, Project project, Editor editor) {
        InputEvent inputEvent = anActionEvent.getInputEvent();
        if (inputEvent instanceof KeyEvent keyEvent) {
            int keyCode = keyEvent.getKeyCode();

            switch (keyCode) {
                case KeyEvent.VK_TAB:
                    log.debug("TAB event detected");
                    keepFullPrediction(project, editor);
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_ESCAPE:
                    ignorePrediction(anActionEvent);
                    break;
                case KeyEvent.VK_RIGHT:
                    log.debug("RIGHT event detected");
                    keepPartialPrediction(project, editor);
                    break;
            }
        }
    }

    private void ignorePrediction(@NotNull AnActionEvent anActionEvent) {
        log.debug("Ignoring prediction");
        codeCompletionService.next(new IdleState(codeCompletionService));
        codeCompletionService.actionPerformed(anActionEvent);
    }

    private void keepFullPrediction(@NotNull Project project, Editor editor) {
        log.debug("Going to keep the prediction");
        WriteCommandAction.runWriteCommandAction(
                project,
                () -> {
                    editor.getDocument()
                            .insertString(editor.getCaretModel().getOffset(), prediction);

                    editor.getCaretModel()
                            .moveToOffset(editor.getCaretModel().getOffset() + prediction.length());

                    codeCompletionService.next(new IdleState(codeCompletionService));
                });
    }

    private void keepPartialPrediction(Project project, Editor editor) {
        log.debug("Going to keep partial prediction");
        WriteCommandAction.runWriteCommandAction(
                project,
                () -> {
                    editor.getDocument()
                            .insertString(
                                    editor.getCaretModel().getOffset(), prediction.substring(0, 1));

                    String nextPrediction = prediction.substring(1);
                    if (nextPrediction.isBlank()) {
                        log.debug("The entire prediction has been accepted");
                        codeCompletionService.next(new IdleState(codeCompletionService));
                    } else {
                        codeCompletionService.next(
                                new PreviewState(codeCompletionService, nextPrediction));
                    }
                });
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        super.documentChanged(event);

        codeCompletionService.next(new IdleState(codeCompletionService));
        codeCompletionService.documentChanged(event);
    }
}

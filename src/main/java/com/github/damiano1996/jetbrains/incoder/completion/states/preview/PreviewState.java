package com.github.damiano1996.jetbrains.incoder.completion.states.preview;

import com.github.damiano1996.jetbrains.incoder.completion.CodeCompletionService;
import com.github.damiano1996.jetbrains.incoder.completion.states.BaseState;
import com.github.damiano1996.jetbrains.incoder.completion.states.decision.DecisionState;
import com.github.damiano1996.jetbrains.incoder.completion.states.preview.renderer.PreviewInlayRenderer;
import com.intellij.codeInsight.daemon.impl.HintRenderer;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.InlayModel;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import java.awt.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class PreviewState extends BaseState {

    private final String prediction;

    public PreviewState(CodeCompletionService codeCompletionService, String prediction) {
        super(codeCompletionService);
        this.prediction = prediction;

        cleanInlay();
        renderPrediction();
    }

    private void renderPrediction() {
        EventQueue.invokeLater(
                () -> {
                    Editor editor = codeCompletionService.getEditor();
                    if (editor == null) {
                        log.debug("Editor was null, retuning");
                        return;
                    }

                    InlayModel inlayModel = editor.getInlayModel();

                    int offset = editor.getCaretModel().getOffset();
                    inlayModel.addInlineElement(offset, true, new PreviewInlayRenderer(prediction));

                    int endLineOffset =
                            Math.max(editor.getCaretModel().getVisualLineEnd() - 1, offset);
                    inlayModel.addInlineElement(endLineOffset, true, new PreviewInlayRenderer(" "));
                    inlayModel.addInlineElement(endLineOffset, true, new HintRenderer("Tab"));
                    inlayModel.addInlineElement(
                            endLineOffset, true, new PreviewInlayRenderer(" to complete"));
                });
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        super.documentChanged(event);

        cleanInlay();
        updateState();
        codeCompletionService.documentChanged(event);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        super.actionPerformed(anActionEvent);

        cleanInlay();
        updateState();
        codeCompletionService.actionPerformed(anActionEvent);
    }

    @Override
    public void beforeActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event) {
        super.beforeActionPerformed(action, event);

        cleanInlay();
        updateState();
        codeCompletionService.beforeActionPerformed(action, event);
    }

    @Override
    public void mouseClicked(@NotNull EditorMouseEvent event) {
        super.mouseClicked(event);

        cleanInlay();
        updateState();
        codeCompletionService.mouseClicked(event);
    }

    private void updateState() {
        log.debug("Going to decision state");
        codeCompletionService.next(new DecisionState(codeCompletionService, prediction));
    }

    private void cleanInlay() {
        try {
            Editor editor = codeCompletionService.getEditor();
            if (editor != null) {
                editor.getInlayModel()
                        .getInlineElementsInRange(0, editor.getDocument().getTextLength())
                        .forEach(Disposable::dispose);
            }
        } catch (Exception e) {
            log.warn("Error while cleaning inlay model: {}", e.getMessage());
        }
    }
}

package com.github.damiano1996.jetbrains.incoder.completion.states.idle;

import com.github.damiano1996.jetbrains.incoder.completion.CodeCompletionService;
import com.github.damiano1996.jetbrains.incoder.completion.states.BaseState;
import com.github.damiano1996.jetbrains.incoder.completion.states.generation.StartGenerationState;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.settings.InlineSettings;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.project.Project;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class IdleState extends BaseState {

    public IdleState(CodeCompletionService codeCompletionService) {
        super(codeCompletionService);
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        super.documentChanged(event);

        if (!InlineSettings.getInstance().getState().enable) {
            log.debug("Inline code completion is disabled.");
            return;
        }

        log.debug("Executing {} state", IdleState.class.getSimpleName());
        log.debug("Idle timeout reached, transitioning to RunGenerationState");
        codeCompletionService.next(new StartGenerationState(codeCompletionService));
        codeCompletionService.documentChanged(event);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        super.actionPerformed(anActionEvent);

        Project project = anActionEvent.getProject();
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);

        InputEvent inputEvent = anActionEvent.getInputEvent();
        if (inputEvent instanceof KeyEvent keyEvent) {
            int keyCode = keyEvent.getKeyCode();

            if (keyCode == KeyEvent.VK_TAB) {
                log.debug("TAB event detected");
                WriteCommandAction.runWriteCommandAction(
                        project,
                        () -> {
                            Objects.requireNonNull(editor)
                                    .getDocument()
                                    .insertString(editor.getCaretModel().getOffset(), "\t");
                        });
            }
        }
    }
}

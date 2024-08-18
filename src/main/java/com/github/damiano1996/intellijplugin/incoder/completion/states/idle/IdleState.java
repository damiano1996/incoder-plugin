package com.github.damiano1996.intellijplugin.incoder.completion.states.idle;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionService;
import com.github.damiano1996.intellijplugin.incoder.completion.states.BaseState;
import com.github.damiano1996.intellijplugin.incoder.completion.states.generation.StartGenerationState;
import com.intellij.openapi.editor.event.DocumentEvent;
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

        log.debug("Executing {} state", IdleState.class.getSimpleName());
        log.debug("Idle timeout reached, transitioning to RunGenerationState");
        codeCompletionService.next(new StartGenerationState(codeCompletionService));
        codeCompletionService.documentChanged(event);
    }
}

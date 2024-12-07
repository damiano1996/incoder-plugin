package com.github.damiano1996.jetbrains.incoder.completion.states.generation;

import com.github.damiano1996.jetbrains.incoder.completion.CodeCompletionService;
import com.github.damiano1996.jetbrains.incoder.completion.states.BaseState;
import com.github.damiano1996.jetbrains.incoder.completion.states.idle.IdleState;
import com.github.damiano1996.jetbrains.incoder.completion.states.preview.PreviewState;
import com.github.damiano1996.jetbrains.incoder.notification.NotificationService;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.editor.event.DocumentEvent;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class WaitGenerationState extends BaseState {

    public WaitGenerationState(CodeCompletionService codeCompletionService) {
        super(codeCompletionService);
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        super.documentChanged(event);

        log.debug(
                "The document has been updated while waiting the prediction. New fragment: {}. "
                        + "Going back to start a new generation",
                event.getNewFragment());
        codeCompletionService.next(new StartGenerationState(codeCompletionService));
        codeCompletionService.documentChanged(event);
    }

    @Override
    public void onCodeCompletionPrediction(String prediction) {
        super.onCodeCompletionPrediction(prediction);

        if (prediction == null || prediction.isBlank()) {
            log.debug("Prediction was null or blank");
            codeCompletionService.next(new IdleState(codeCompletionService));
        } else {
            log.info("Prediction: {}", prediction);
            log.debug("Going to preview state");
            codeCompletionService.next(new PreviewState(codeCompletionService, prediction));
        }
    }

    @Override
    public void onCodeCompletionError(@NotNull Throwable throwable) {
        super.onCodeCompletionError(throwable);
        log.warn("Error received on prediction...", throwable);
        NotificationService.getInstance(Objects.requireNonNull(ProjectUtil.getActiveProject()))
                .notifyError(throwable.getMessage());
        log.debug("Going to end state");
        codeCompletionService.next(new IdleState(codeCompletionService));
    }
}

package com.github.damiano1996.intellijplugin.incoder.completion.states.generation;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionService;
import com.github.damiano1996.intellijplugin.incoder.completion.states.BaseState;
import com.github.damiano1996.intellijplugin.incoder.completion.states.idle.IdleState;
import com.github.damiano1996.intellijplugin.incoder.language.model.client.inline.settings.InlineSettings;
import com.intellij.openapi.editor.event.DocumentEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class StartGenerationState extends BaseState {

    public StartGenerationState(CodeCompletionService codeCompletionService) {
        super(codeCompletionService);
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        super.documentChanged(event);

        String text = event.getDocument().getText();

        CodeCompletionContext codeCompletionContext = createRequest(text, event.getOffset() + 1);

        if (!InlineSettings.getInstance().getState().triggerEndLine) {
            next(codeCompletionContext);
        } else {

            log.debug("Trigger only on end of lines");

            if (codeCompletionContext.rightContext().startsWith("\n")) {
                next(codeCompletionContext);
            } else {
                log.debug("Going to idle state since left context is not at the end of the line.");
                codeCompletionService.next(new IdleState(codeCompletionService));
            }

        }
    }

    private void next(CodeCompletionContext codeCompletionContext) {
        codeCompletionService.getCodeCompletionQueue().add(codeCompletionContext);

        log.debug("Going to wait state to let the llm to process the context");
        codeCompletionService.next(new WaitGenerationState(codeCompletionService));
    }

    @Contract("_, _ -> new")
    private @NotNull CodeCompletionContext createRequest(@NotNull String text, int offset) {

        int actualOffset = Math.min(offset, text.length());
        String leftContext = text.substring(0, actualOffset);
        String rightContext = text.substring(actualOffset);

        return new CodeCompletionContext(leftContext, rightContext);
    }
}

package com.github.damiano1996.intellijplugin.incoder.completion.states.generation;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionService;
import com.github.damiano1996.intellijplugin.incoder.completion.states.BaseState;
import com.github.damiano1996.intellijplugin.incoder.llm.LlmService;
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

        LlmService.getInstance(codeCompletionService.getProject())
                .autocompletion(codeCompletionContext);

        log.debug("Going to wait state");
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

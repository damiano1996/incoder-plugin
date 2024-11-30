package com.github.damiano1996.intellijplugin.incoder.language.model.client.prompt;

import dev.langchain4j.service.UserMessage;

public interface PromptClassifier {

    @UserMessage("""
        Analyze the provided prompt and classify it into one of the available prompt types.
        Respond with only the name of the appropriate prompt type, nothing else.

        Prompt:
        {{it}}
    """)
    PromptType classify(String prompt);
}

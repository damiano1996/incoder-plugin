package com.github.damiano1996.intellijplugin.incoder.language.model.client.prompt;

import dev.langchain4j.service.UserMessage;

public interface PromptClassifier {

    @UserMessage("Classify the given prompt: {{it}}")
    PromptType classify(String prompt);
}

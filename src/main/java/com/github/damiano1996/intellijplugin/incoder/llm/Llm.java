package com.github.damiano1996.intellijplugin.incoder.llm;

import com.intellij.openapi.editor.Editor;
import dev.langchain4j.service.TokenStream;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;

public interface Llm {

    CompletableFuture<String> chat(String input);

    CompletableFuture<PromptType> classify(String prompt);

    CompletableFuture<CodeEditingResponse> edit(
            @NonNull Editor editor, @NonNull String editDescription);

    TokenStream answer(@NonNull Editor editor, @NonNull String question);

    enum PromptType {
        EDIT,
        CODE_QUESTION,
        OTHER,
    }

    record CodeEditingResponse(@NonNull String code, @NonNull String comments) {}

    record CodeAnswerResponse(@NonNull String response) {}
}

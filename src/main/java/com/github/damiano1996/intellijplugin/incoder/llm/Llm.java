package com.github.damiano1996.intellijplugin.incoder.llm;

import com.intellij.openapi.editor.Editor;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public interface Llm {

    CompletableFuture<String> chat(String input);

    CompletableFuture<PromptType> classify(String prompt);

    CompletableFuture<CodeEditingResponse> edit(@NonNull Editor editor, @NonNull String editDescription);

    CompletableFuture<CodeRagResponse> rag(@NonNull Editor editor, @NonNull String question);


    enum PromptType {
        EDIT,
        CODE_QUESTION,
        OTHER,
    }

    record CodeEditingResponse(
            @NonNull String code,
            @NonNull String comments) {}

        record CodeRagResponse(@NonNull String response){}
}

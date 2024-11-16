package com.github.damiano1996.intellijplugin.incoder.llm;

import com.intellij.openapi.editor.Editor;
import dev.langchain4j.model.output.structured.Description;
import dev.langchain4j.service.TokenStream;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;

public interface Llm {

    TokenStream chat(String input);

    CompletableFuture<PromptType> classify(String prompt);

    CompletableFuture<CodeEditingResponse> edit(
            @NonNull Editor editor, @NonNull String editDescription);

    TokenStream answer(@NonNull Editor editor, @NonNull String question);

    enum PromptType {
        @Description(
                "The prompt describes how the user wants to edit the code. The user can ask to add,"
                        + " remove, update the code.")
        EDIT,
        @Description("The user is asking something about the code he is viewing.")
        CODE_QUESTION,
        @Description("Unclassified prompt.")
        OTHER,
    }

    record CodeEditingResponse(@NonNull String code, @NonNull String comments) {}

    record CodeAnswerResponse(@NonNull String response) {}
}

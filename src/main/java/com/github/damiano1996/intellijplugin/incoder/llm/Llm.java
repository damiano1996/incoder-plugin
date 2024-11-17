package com.github.damiano1996.intellijplugin.incoder.llm;

import com.intellij.openapi.editor.Editor;
import dev.langchain4j.model.output.structured.Description;
import dev.langchain4j.service.TokenStream;
import lombok.Getter;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public interface Llm {

    TokenStream chat(String input);

    CompletableFuture<PromptType> classify(String prompt);

    TokenStream edit(
            @NonNull Editor editor, @NonNull String editDescription);

    TokenStream answer(@NonNull Editor editor, @NonNull String question);

    @Getter
    enum PromptType {
        @Description(
                """
                        The prompt describes how the user wants to edit the code.
                        The user can ask to add, remove, update the code.""")
        EDIT("edit"),
        @Description("The user is asking something about the code he is viewing.")
        CODE_QUESTION("code question"),
        @Description("General/unclassified prompt.")
        GENERAL("general");

        private final String name;

        PromptType(String name) {
            this.name = name;
        }
    }

}

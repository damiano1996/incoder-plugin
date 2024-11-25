package com.github.damiano1996.intellijplugin.incoder.language.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Getter;

@Getter
public enum PromptType {
    @Description(
            """
                    The prompt describes how the user wants to edit the code.
                    The user can ask to add, remove or update the code.""")
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

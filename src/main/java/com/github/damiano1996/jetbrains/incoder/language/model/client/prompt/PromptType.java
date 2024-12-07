package com.github.damiano1996.jetbrains.incoder.language.model.client.prompt;

import dev.langchain4j.model.output.structured.Description;
import lombok.Getter;

@Getter
public enum PromptType {
    @Description(
            """
            The user wants to modify the code by adding, removing, updating, or refactoring it.
            """)
    EDIT("Edit"),

    @Description(
            """
            The user needs help understanding, debugging, or analyzing the logic of the code.
            """)
    EXPLAIN("Explain"),

    @Description(
            """
            The user requests to generate new code, including templates, stubs, or new features.
            """)
    GENERATE("Generate"),

    @Description(
            """
            The user seeks performance improvements or optimization of the code.
            """)
    OPTIMIZE("Optimize"),

    @Description(
            """
            Generic or unclassified prompt that does not fit into other specific categories, such as chat questions.
            """)
    GENERAL("General");

    private final String displayName;

    PromptType(String displayName) {
        this.displayName = displayName;
    }
}

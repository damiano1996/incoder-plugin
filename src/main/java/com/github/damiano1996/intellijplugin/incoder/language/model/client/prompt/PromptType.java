package com.github.damiano1996.intellijplugin.incoder.language.model.client.prompt;

import dev.langchain4j.model.output.structured.Description;
import lombok.Getter;

@Getter
public enum PromptType {
    @Description(
            """
                    The prompt describes how the user wants to edit the code.
                    The user can ask to add, remove, refactor, or update code.""")
    EDIT("Edit"),

    @Description(
            """
                    The user is asking about the code they are viewing, such as understanding logic, debugging,
                    or seeking clarification.""")
    CODE_QUESTION("Code Question"),

    @Description(
            """
                    The user seeks assistance in generating new code, such as"
                + " creating templates, stubs, or new features.""")
    GENERATE("Generate"),

    @Description(
            """
                    The user requests optimization or performance improvements for"
                + " the existing code.""")
    OPTIMIZE("Optimize"),

    @Description(
            """
                    The user wants documentation or comments to be added, updated,"
                + " or clarified in the code.""")
    DOCUMENTATION("Documentation"),

    @Description(
            """
                    The user requests an analysis of the code for potential errors,"
                + " vulnerabilities, or best practices.""")
    ANALYZE("Analyze"),

    @Description("Generic, unknown or unclassified prompt that does not fit into other categories.")
    GENERAL("General");

    private final String displayName;

    PromptType(String displayName) {
        this.displayName = displayName;
    }
}

package com.github.damiano1996.intellijplugin.incoder.language.model;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface LanguageModelClient {

    @UserMessage(
            """
                    Given the following code context, provide only the missing line of code.

                    Left context:
                    {{leftContext}}

                    Right context:
                    {{rightContext}}

                    What is the single line of missing code between these contexts?
                    Respond only with the necessary code to complete the line.
                    Do not include markdown, comments, or additional explanations.
                    If the line is partially written, complete it up to the first newline.
                    If the line is already at the end, return an empty response.
                    """)
    TokenStream complete(
            @V("leftContext") String leftContext, @V("rightContext") String rightContext);

    @SystemMessage(
            """
                    You are an AI assistant integrated into a JetBrains plugin, providing expert coding assistance and development support directly within the IDE.

                    Context:
                    - Current code being viewed by the user:
                      {{code}}

                    - File path: {{filePath}}
                    - Project base path: {{projectBasePath}}

                    If the user input pertains to the provided code, respond with the code edited according to the user's instructions.
                    """
    )
    TokenStream chat(
            @V("code") String code, @V("filePath") String filePath, @V("projectBasePath") String projectBasePath, @UserMessage String prompt);

    @SystemMessage(
            """
                    You are an AI assistant integrated into a JetBrains plugin, providing expert coding assistance and development support directly within the IDE.

                    Context:
                    - Project base path: {{projectBasePath}}
                    """)
    TokenStream chat(@V("projectBasePath") String projectBasePath, @UserMessage String prompt);

    @UserMessage("Classify the given prompt: {{it}}")
    PromptType classify(String prompt);

    @UserMessage(
            """
                    Define a file path based on the file content:
                    {{fileContent}}
                    
                    Context:
                    - Project folder tree:
                    {{projectFolderTree}}
                    
                    Return only the absolute file path. Nothing else.
                    """
    )
    String createFilePath(@V("fileContent") String fileContent, @V("projectFolderTree") String projectFolderTree);
}

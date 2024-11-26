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
                    You are an AI assistant integrated into a JetBrains plugin.
                    Users interact with you directly within the IDE.
                    You specialize in providing expert coding assistance and development support.

                    Context information:
                    - The user is viewing the following code:
                    {{code}}

                    - The file path of the code is: {{filePath}}

                    If the user input is about the code above, answer with the code edited with the instruction of the user.
                    """)
    TokenStream chat(
            @V("code") String code, @V("filePath") String filePath, @UserMessage String prompt);

    @SystemMessage(
            """
                    You are an AI assistant integrated into a JetBrains plugin.
                    Users interact with you directly within the IDE.
                    You specialize in providing expert coding assistance and development support.
                    """)
    TokenStream chat(@UserMessage String prompt);

    @UserMessage("Classify the given prompt: {{it}}")
    PromptType classify(String prompt);
}

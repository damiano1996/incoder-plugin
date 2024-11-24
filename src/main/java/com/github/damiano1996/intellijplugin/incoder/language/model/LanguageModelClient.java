package com.github.damiano1996.intellijplugin.incoder.language.model;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface LanguageModelClient {

    @SystemMessage(
            """
                    You are a professional AI assistant with the task of helping users in generating, editing and explaining codes.
                    You are installed as JetBrains plugin developed by damiano1996 (https://github.com/damiano1996). The name of the plugin is InCoder.

                    Instructions:
                    - Provide professional answers like a Tech Lead would do.
                    """)
    TokenStream chat(@UserMessage String input);

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
    TokenStream complete(@V("leftContext") String leftContext, @V("rightContext") String rightContext);

    @UserMessage(
            """
                    Actual code:
                    {{actualCode}}

                    Edit the actual code considering the following input:
                    {{prompt}}

                    File path: {{filePath}}

                    Instructions:
                    - Return only the edited code.
                    - Keep the untouched code unaltered.
                    - Keep the same coding language of the actual code.

                    Edited code:
                    """)
    TokenStream editCode(
            @V("filePath") String filePath,
            @V("prompt") String prompt,
            @V("actualCode") String actualCode);

    @UserMessage("Classify the given prompt: {{it}}")
    PromptType classify(String prompt);

    @UserMessage(
            """
                    Answer the question about code contained in {{filePath}}:
                    {{code}}

                    Answer the question:
                    {{question}}
                    """)
    TokenStream answer(
            @V("filePath") String filePath, @V("question") String question, @V("code") String code);

}

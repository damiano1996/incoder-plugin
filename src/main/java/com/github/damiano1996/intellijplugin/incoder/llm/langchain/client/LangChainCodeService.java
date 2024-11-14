package com.github.damiano1996.intellijplugin.incoder.llm.langchain.client;

import com.github.damiano1996.intellijplugin.incoder.llm.Llm;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface LangChainCodeService {

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
    String complete(@V("leftContext") String leftContext, @V("rightContext") String rightContext);

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
                    - Do NOT insert prefixes and suffixes for Markdown.
                    - Keep the same coding language of the actual code.
                    - Do not add comments or descriptions before and after the code.
                    - Generate only the code without any markdown formatting or comments. Do not use ``` at the beginning or at the end.

                    Edited code:
                    """)
    CodeEditResponse editCode(
            @V("filePath") String filePath,
            @V("prompt") String prompt,
            @V("actualCode") String actualCode);

    @UserMessage("Classify the given prompt: {{it}}")
    Llm.PromptType classify(String prompt);

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

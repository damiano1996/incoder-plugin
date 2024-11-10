package com.github.damiano1996.intellijplugin.incoder.llm.langchain.client;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface LangChainCodeCompletion {


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
                    """
    )
    String codeComplete(
            @V("leftContext") String leftContext, @V("rightContext") String rightContext);
}

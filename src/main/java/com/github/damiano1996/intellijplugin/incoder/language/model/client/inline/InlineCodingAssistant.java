package com.github.damiano1996.intellijplugin.incoder.language.model.client.inline;

import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface InlineCodingAssistant {

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
}

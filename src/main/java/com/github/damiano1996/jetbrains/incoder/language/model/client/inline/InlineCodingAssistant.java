package com.github.damiano1996.jetbrains.incoder.language.model.client.inline;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface InlineCodingAssistant {

    @SystemMessage(
            """
            Instructions:
            {{instructions}}
            """)
    @UserMessage(
            """
            Use the following code snippets to complete the current line.
            Present only the completed code line, nothing else.

            LEFT CONTEXT (preceding code):
            {{leftContext}}
            ---
            RIGHT CONTEXT (following code):
            {{rightContext}}
            ---
            CONTINUE THE LAST LINE WITHOUT REPEATING:
            {{leftContextLastLine}}
            """)
    String complete(
            @V("instructions") String instructions,
            @V("leftContext") String leftContext,
            @V("rightContext") String rightContext,
            @V("leftContextLastLine") String lastLine);
}

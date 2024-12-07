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
                    Complete the last line:
                    {{leftContext}}
                    """)
    String complete(
            @V("instructions") String instructions,
            @V("leftContext") String leftContext,
            @V("rightContext") String rightContext);
}

package com.github.damiano1996.intellijplugin.incoder.language.model.client.inline;

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

                    Instructions:
                    {{instructions}}
                    """)
    String complete(
            @V("instructions") String instructions,
            @V("leftContext") String leftContext, @V("rightContext") String rightContext);
}

package com.github.damiano1996.intellijplugin.incoder.language.model.client.inline;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface InlineCodingAssistant {

    @UserMessage(
            """
                    Instructions:
                    {{instructions}}

                    Code:
                    {{leftContext}}
                    """)
    String complete(@V("instructions") String instructions, @V("leftContext") String leftContext);
}

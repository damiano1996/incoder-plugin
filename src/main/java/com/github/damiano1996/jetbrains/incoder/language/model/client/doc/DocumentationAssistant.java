package com.github.damiano1996.jetbrains.incoder.language.model.client.doc;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface DocumentationAssistant {

    @SystemMessage("""
                    Instructions:
                    {{instructions}}""")
    @UserMessage(
            """
                    Add documentation to the following:
                    {{code}}
                    """)
    String document(@V("instructions") String instructions, @V("code") String code);
}

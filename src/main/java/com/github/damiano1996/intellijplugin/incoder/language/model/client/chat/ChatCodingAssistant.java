package com.github.damiano1996.intellijplugin.incoder.language.model.client.chat;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ChatCodingAssistant {

    @SystemMessage(
            """
                    Context:
                    - Current code being viewed by the user:
                      {{code}}

                    - File path: {{filePath}}
                    - Project base path: {{projectBasePath}}

                    Instructions:
                    {{instructions}}
                    """)
    TokenStream chat(
            @V("instructions") String instructions,
            @V("code") String code,
            @V("filePath") String filePath,
            @V("projectBasePath") String projectBasePath,
            @UserMessage String prompt);

    @SystemMessage(
            """
                    Context:
                    - Project base path: {{projectBasePath}}

                    Instructions:
                    {{instructions}}
                    """)
    TokenStream chat(
            @V("instructions") String instructions,
            @V("projectBasePath") String projectBasePath,
            @UserMessage String prompt);
}

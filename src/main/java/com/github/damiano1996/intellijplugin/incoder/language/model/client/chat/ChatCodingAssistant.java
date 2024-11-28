package com.github.damiano1996.intellijplugin.incoder.language.model.client.chat;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ChatCodingAssistant {

    @SystemMessage(
            """
                    You are an AI assistant integrated into a JetBrains plugin, providing expert coding assistance and development support directly within the IDE.

                    Context:
                    - Current code being viewed by the user:
                      {{code}}

                    - File path: {{filePath}}
                    - Project base path: {{projectBasePath}}

                    If the user input pertains to the provided code, respond with the code edited according to the user's instructions.
                    """)
    TokenStream chat(
            @V("code") String code,
            @V("filePath") String filePath,
            @V("projectBasePath") String projectBasePath,
            @UserMessage String prompt);

    @SystemMessage(
            """
                    You are an AI assistant integrated into a JetBrains plugin, providing expert coding assistance and development support directly within the IDE.

                    Context:
                    - Project base path: {{projectBasePath}}
                    """)
    TokenStream chat(@V("projectBasePath") String projectBasePath, @UserMessage String prompt);
}

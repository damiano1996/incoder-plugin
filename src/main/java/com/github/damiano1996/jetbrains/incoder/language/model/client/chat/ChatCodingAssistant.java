package com.github.damiano1996.jetbrains.incoder.language.model.client.chat;

import dev.langchain4j.service.*;

public interface ChatCodingAssistant {

    @SystemMessage(
            """
                    {{systemInstructions}}
                    """)
    TokenStream chat(
            @MemoryId int memoryId,
            @V("systemInstructions") String systemInstructions,
            @UserMessage String prompt);

}

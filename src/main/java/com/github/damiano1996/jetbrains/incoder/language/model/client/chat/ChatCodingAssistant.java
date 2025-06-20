package com.github.damiano1996.jetbrains.incoder.language.model.client.chat;

import dev.langchain4j.service.*;

public interface ChatCodingAssistant {

    @SystemMessage(
            """
                    {{systemInstructions}}
                    """)
    @UserMessage(
            """
                    {{prompt}}
                    
                    Context Information:
                    - Current Date: {{currentDate}}
                    - Project Name: {{projectName}}
                    - Project Path: {{projectPath}}
                    - Current File: {{currentFile}}
                    - Programming Language: {{programmingLanguage}}
                    - IDE: {{ideInfo}}
                    - User Timezone: {{userTimezone}}
                    """)
    TokenStream chat(
            @MemoryId int memoryId,
            @V("systemInstructions") String systemInstructions,
            @V("currentDate") String currentDate,
            @V("projectName") String projectName,
            @V("projectPath") String projectPath,
            @V("currentFile") String currentFile,
            @V("programmingLanguage") String programmingLanguage,
            @V("ideInfo") String ideInfo,
            @V("userTimezone") String userTimezone,
            @V("prompt") String prompt);
}

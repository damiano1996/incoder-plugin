package com.github.damiano1996.intellijplugin.incoder.llm.langchain.client.generation;

import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface LangChainCodeGeneration {

    @UserMessage(
            """
                    Actual code:
                    {{actualCode}}

                    Please, update the actual code considering the following input:
                    {{prompt}}

                    File path: {{filePath}}

                    Instructions:
                    - Return only the updated code.
                    - Keep the untouched code unaltered.
                    - Do NOT insert prefixes and suffixes for Markdown.
                    - Keep the same coding language of the actual code.
                    - Do not add comments or descriptions before and after the code.
                    - Generate only the code without any markdown formatting or comments. Do not use ``` at the beginning or at the end.

                    Updated code:
                    """)
    LangChainCodeUpdate codeGenerate(
            @V("filePath") String filePath,
            @V("prompt") String prompt,
            @V("actualCode") String actualCode);
}

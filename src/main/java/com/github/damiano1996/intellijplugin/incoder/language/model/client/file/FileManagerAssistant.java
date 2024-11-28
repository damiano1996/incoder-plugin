package com.github.damiano1996.intellijplugin.incoder.language.model.client.file;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface FileManagerAssistant {

    @UserMessage(
            """
                    Define an file name based on the file content:
                    {{fileContent}}

                    Return only the file name with the extension. Nothing else. No prefixes or suffixes.
                    It will be used to name and save the file content.
                    """)
    String createFileName(@V("fileContent") String fileContent);
}

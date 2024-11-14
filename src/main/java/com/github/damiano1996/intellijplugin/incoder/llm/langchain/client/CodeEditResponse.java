package com.github.damiano1996.intellijplugin.incoder.llm.langchain.client;

import dev.langchain4j.model.output.structured.Description;
import lombok.Getter;

@Getter
public class CodeEditResponse {

    @Description("Edited code")
    String editedCode;

    @Description("Description of changes done to the original code")
    String changesDescription;
}

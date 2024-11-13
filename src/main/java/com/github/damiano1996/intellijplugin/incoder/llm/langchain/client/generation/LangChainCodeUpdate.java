package com.github.damiano1996.intellijplugin.incoder.llm.langchain.client.generation;

import dev.langchain4j.model.output.structured.Description;
import lombok.Getter;

@Getter
public class LangChainCodeUpdate {

    @Description("Updated code")
    String updatedCode;

    @Description("Additional notes about code changes that can be shared with the user")
    String notes;
}

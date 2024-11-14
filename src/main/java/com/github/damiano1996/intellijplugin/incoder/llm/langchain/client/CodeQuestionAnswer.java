package com.github.damiano1996.intellijplugin.incoder.llm.langchain.client;

import dev.langchain4j.model.output.structured.Description;
import lombok.Getter;

@Getter
public class CodeQuestionAnswer {

    @Description("Answer to the question")
    String answer;
}

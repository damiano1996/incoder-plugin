package com.github.damiano1996.jetbrains.incoder.tool.window.actions.cards.documentation;

import com.github.damiano1996.jetbrains.incoder.InCoderBundle;
import com.github.damiano1996.jetbrains.incoder.tool.window.actions.cards.IntelligentAction;
import com.github.damiano1996.jetbrains.incoder.tool.window.actions.cards.IntelligentActionExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentationIntelligentAction implements IntelligentAction {

    @Override
    public String getDisplayName() {
        return "Documentation";
    }

    @Override
    public String getDescription() {
        return InCoderBundle.message("intelligent.actions.documentation.description");
    }

    @Override
    public IntelligentActionExecutor createExecutor() {
        return new DocumentationExecutor();
    }
}

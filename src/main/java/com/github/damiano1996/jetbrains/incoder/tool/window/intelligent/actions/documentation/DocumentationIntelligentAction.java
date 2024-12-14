package com.github.damiano1996.jetbrains.incoder.tool.window.intelligent.actions.documentation;

import com.github.damiano1996.jetbrains.incoder.tool.window.intelligent.actions.IntelligentAction;
import com.github.damiano1996.jetbrains.incoder.tool.window.intelligent.actions.IntelligentActionExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentationIntelligentAction implements IntelligentAction {
    @Override
    public String getName() {
        return "Documentation";
    }

    @Override
    public String getDescription() {
        return "Automatically generates comprehensive documentation for your code, including class"
                + " descriptions, method summaries, and parameter explanations. Review and"
                + " refine the generated documentation before seamlessly integrating it into"
                + " your codebase.";
    }

    @Override
    public IntelligentActionExecutor getExecutor() {
        return new DocumentationExecutor();
    }
}

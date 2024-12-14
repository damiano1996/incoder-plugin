package com.github.damiano1996.jetbrains.incoder.tool.window.intelligent.actions;

public interface IntelligentAction {

    String getName();

    String getDescription();

    IntelligentActionExecutor getExecutor();
}

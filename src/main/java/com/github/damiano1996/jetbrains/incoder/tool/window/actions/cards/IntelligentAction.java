package com.github.damiano1996.jetbrains.incoder.tool.window.actions.cards;

public interface IntelligentAction {

    String getDisplayName();

    String getDescription();

    IntelligentActionExecutor createExecutor();
}

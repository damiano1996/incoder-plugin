package com.github.damiano1996.jetbrains.incoder.tool.window.intelligent.actions;

public interface IntelligentActionExecutor {

    void execute();

	void setObserver(IntelligentActionObserver observer);

}

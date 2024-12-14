package com.github.damiano1996.jetbrains.incoder.tool.window.intelligent.actions;

import java.awt.event.ActionListener;

public interface IntelligentAction {

    String getName();

    String getDescription();

    ActionListener getActionListener();
}

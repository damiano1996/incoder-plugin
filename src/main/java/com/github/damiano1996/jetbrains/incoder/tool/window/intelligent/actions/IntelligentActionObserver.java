package com.github.damiano1996.jetbrains.incoder.tool.window.intelligent.actions;

import javax.swing.*;

public interface IntelligentActionObserver {

    void onProgressUpdate(String text);

    void onGeneratedArtifact(JComponent component);

    void onActionCompleted();
}

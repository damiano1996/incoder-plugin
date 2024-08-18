package com.github.damiano1996.intellijplugin.incoder.completion.states;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionListener;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorMouseListener;

public interface State
        extends DocumentListener, EditorMouseListener, CodeCompletionListener, AnActionListener {

    void actionPerformed(AnActionEvent anActionEvent);
}

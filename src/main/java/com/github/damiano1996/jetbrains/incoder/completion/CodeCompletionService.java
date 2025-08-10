package com.github.damiano1996.jetbrains.incoder.completion;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.InlineLanguageModelClient;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorMouseListener;
import org.jetbrains.annotations.NotNull;

public interface CodeCompletionService
        extends AnActionListener, CodeCompletionListener, DocumentListener, EditorMouseListener {

    void actionPerformed(@NotNull AnActionEvent anActionEvent);

    InlineLanguageModelClient getOrCreateInlineLanguageModelClient() throws LanguageModelException;

    void setInlineLanguageModelClient(InlineLanguageModelClient client);
}

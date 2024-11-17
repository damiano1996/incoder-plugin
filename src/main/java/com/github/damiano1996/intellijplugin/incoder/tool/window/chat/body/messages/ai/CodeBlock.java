package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CodeBlock {

    @Getter
    private JPanel mainPanel;

    @Setter
    @Nullable
    private Project project;

    @Nullable
    private Editor editor;

    public void setText(String text) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (editor != null) return;
            initializeEditor(text);
        });


        if (project == null) return;
        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (editor == null) return;
            editor.getDocument().setText(text);
        });
    }

    private void initializeEditor(String text) {
        if (project == null) return;

        Document document = EditorFactory.getInstance().createDocument(text);

        editor =
                EditorFactory.getInstance()
                        .createEditor(
                                document,
                                project,
                                FileEditorManager.getInstance(project)
                                        .getSelectedTextEditor()
                                        .getVirtualFile()
                                        .getFileType(),
                                true);

        mainPanel.add(editor.getComponent());
    }
}

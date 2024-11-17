package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import javax.swing.*;

public class CodeEditorWriter implements StreamWriter {

    private final Project project;

    private Editor editor;

    public CodeEditorWriter(Project project) {
        this.project = project;

        ApplicationManager.getApplication()
                .invokeAndWait(
                        () -> {
                            Document document = EditorFactory.getInstance().createDocument("");

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
                        });
    }

    public void setText(String text) {
        WriteCommandAction.runWriteCommandAction(project, () -> editor.getDocument().setText(text));
    }

    @Override
    public void write(String token) {
        if (editor == null) return;
        setText(editor.getDocument().getText() + token);
    }

    @Override
    public String getFullText() {
        return editor.getDocument().getText();
    }

    public JComponent getComponent() {
        return editor.getComponent();
    }
}

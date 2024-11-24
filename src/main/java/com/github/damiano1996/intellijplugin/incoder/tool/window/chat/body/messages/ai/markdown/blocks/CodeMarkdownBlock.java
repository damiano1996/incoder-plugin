package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.LightVirtualFile;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

@Slf4j
public class CodeMarkdownBlock implements MarkdownBlock {

    private Editor editor;

    public CodeMarkdownBlock(Project project, FileType fileType, String initialText) {
        LightVirtualFile virtualFile = new LightVirtualFile("temp", fileType, initialText);
        Document document = EditorFactory.getInstance().createDocument(initialText);

        ApplicationManager.getApplication().invokeAndWait(() -> {
            editor = EditorFactory.getInstance().createEditor(document, project, virtualFile, false, EditorKind.PREVIEW);
            ((EditorEx) editor).setViewer(true);
        });
    }

    @Override
    public JComponent getComponent() {
        return editor.getComponent();
    }

    @Override
    public void write(String token) {
        ApplicationManager.getApplication().invokeLater(() -> {
            String currentText = editor.getDocument().getText();
            String updatedText = currentText + token;
            ApplicationManager.getApplication().runWriteAction(() -> editor.getDocument().setText(updatedText));
        });
    }

    @Override
    public String getFullText() {
        return editor.getDocument().getText();
    }
}

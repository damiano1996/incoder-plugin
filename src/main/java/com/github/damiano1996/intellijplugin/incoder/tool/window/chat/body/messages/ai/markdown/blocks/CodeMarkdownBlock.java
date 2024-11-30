package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks;

import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.LightVirtualFile;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@Slf4j
public class CodeMarkdownBlock implements MarkdownBlock {

    private final Project project;
    private Editor editor;

    private String lastWrittenToken = "";

    public CodeMarkdownBlock(Project project, Language language) {
        this.project = project;
        initEditor(language);
    }

    @Override
    public JComponent getComponent() {
        return editor.getComponent();
    }

    @Override
    public void write(@NotNull String token) {
        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            String currentText = editor.getDocument().getText();
                            String updatedText = currentText + token;

                            lastWrittenToken = token;

                            ApplicationManager.getApplication()
                                    .runWriteAction(
                                            () -> editor.getDocument().setText(updatedText));
                        });

    }

    @Override
    public void undoLastWrite() {
        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            String currentText = editor.getDocument().getText();
                            String updatedText = currentText.substring(0, currentText.length()-lastWrittenToken.length());

                            ApplicationManager.getApplication()
                                    .runWriteAction(
                                            () -> editor.getDocument().setText(updatedText));
                        });
    }

    private void initEditor(Language language) {
        var fileType = FileTypeManager.getInstance().findFileTypeByLanguage(language);
        var virtualFile = new LightVirtualFile("temp", fileType, "");
        var document = EditorFactory.getInstance().createDocument("");

        ApplicationManager.getApplication()
                .invokeAndWait(
                        () -> {
                            editor =
                                    EditorFactory.getInstance()
                                            .createEditor(
                                                    document,
                                                    project,
                                                    virtualFile,
                                                    false,
                                                    EditorKind.PREVIEW);
                            ((EditorEx) editor).setViewer(true);
                        });
    }

    @Override
    public String getFullText() {
        return editor.getDocument().getText();
    }

    public static @NotNull Language getLanguage(String languageName) {
        var languages = Language.getRegisteredLanguages();

        for (Language language : languages) {
            if (language.getID().equalsIgnoreCase(languageName)) {
                return language;
            }
        }

        log.debug("Unable to infer the language from the language name");
        return Language.ANY;
    }
}

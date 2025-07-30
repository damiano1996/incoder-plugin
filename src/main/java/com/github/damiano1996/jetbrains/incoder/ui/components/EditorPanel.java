package com.github.damiano1996.jetbrains.incoder.ui.components;

import com.intellij.lang.Language;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.TextAccessor;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class EditorPanel extends JPanel implements TextAccessor, Disposable {

    @Getter private final Language language;
    private final Project project;

    @Nullable private Editor editor;

    public EditorPanel(@NotNull Project project, @NotNull Language language) {
        this.language = language;
        this.project = project;

        setLayout(new BorderLayout());
        setFocusable(false);
        setOpaque(false);

        initializeEditor();
    }

    private void initializeEditor() {
        var fileType = FileTypeManager.getInstance().findFileTypeByLanguage(language);
        var virtualFile = new LightVirtualFile("temp", fileType, "");
        var document = EditorFactory.getInstance().createDocument("");

        ApplicationManager.getApplication()
                .invokeAndWait(
                        () -> {
                            try {
                                editor =
                                        EditorFactory.getInstance()
                                                .createEditor(
                                                        document,
                                                        project,
                                                        virtualFile,
                                                        false,
                                                        EditorKind.PREVIEW);

                                if (editor instanceof EditorEx editorEx) {
                                    editorEx.setViewer(false);
                                    editorEx.setCaretEnabled(false);
                                }

                                add(editor.getComponent(), BorderLayout.CENTER);
                                revalidate();
                                repaint();
                            } catch (Exception e) {
                                log.error("Unable to initialize embedded editor", e);
                            }
                        },
                        ModalityState.any());
    }

    @Override
    public String getText() {
        return editor != null ? editor.getDocument().getText() : "";
    }

    @Override
    public void setText(@NotNull String text) {
        if (editor == null) return;
        ApplicationManager.getApplication()
                .invokeLater(
                        () ->
                                ApplicationManager.getApplication()
                                        .runWriteAction(
                                                () -> {
                                                    if (editor != null) {
                                                        editor.getDocument().setText(text);
                                                    }
                                                }));
    }

    @Override
    public void dispose() {
        if (editor != null && !editor.isDisposed()) {
            EditorFactory.getInstance().releaseEditor(editor);
            editor = null;
        }
    }

    public static @NotNull Language guessLanguage(String languageName) {
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

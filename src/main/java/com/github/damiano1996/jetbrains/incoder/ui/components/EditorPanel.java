package com.github.damiano1996.jetbrains.incoder.ui.components;

import com.github.damiano1996.jetbrains.incoder.tool.window.ToolWindowColors;
import com.intellij.lang.Language;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.TextAccessor;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class EditorPanel extends JPanel implements TextAccessor, Disposable {

    @Getter private final Language language;

    private Editor editor;

    public EditorPanel(Project project, Language language) {
        this.language = language;

        setLayout(new BorderLayout());
        setBackground(ToolWindowColors.AI_MESSAGE_BACKGROUND);
        setForeground(ToolWindowColors.AI_MESSAGE_FOREGROUND);
        setFocusable(false);

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

                            add(editor.getComponent(), BorderLayout.CENTER);
                        });
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

    @Override
    public void setText(@NotNull String text) {
        ApplicationManager.getApplication()
                .invokeAndWait(
                        () -> {
                            ApplicationManager.getApplication()
                                    .runWriteAction(
                                            () -> {
                                                if (editor == null) return;
                                                editor.getDocument().setText(text);
                                            });
                        });
    }

    @Override
    public @NlsSafe String getText() {
        return editor.getDocument().getText();
    }

    @Override
    public void dispose() {
        if (editor != null && !editor.isDisposed()) {
            EditorFactory.getInstance().releaseEditor(editor);
            editor = null;
        }
    }
}

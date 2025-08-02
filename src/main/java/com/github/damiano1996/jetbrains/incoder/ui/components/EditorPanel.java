package com.github.damiano1996.jetbrains.incoder.ui.components;

import com.intellij.lang.Language;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.TextAccessor;
import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class EditorPanel extends JPanel implements TextAccessor, Disposable {

    @Getter private final Language language;

    private CompletableFuture<Editor> editorFuture = new CompletableFuture<>();

    public EditorPanel(@NotNull Language language) {
        this.language = language;

        setLayout(new BorderLayout());
        setFocusable(false);
        setOpaque(false);

        initEditor();
    }

    private void initEditor() {
        ApplicationManager.getApplication()
                .invokeAndWait(
                        () -> {
                            Editor editor = createEditor();
                            configureEditor(editor);
                            addEditorToPanel(editor);
                            editorFuture.complete(editor);
                        });
    }

    private Editor createEditor() {
        LanguageFileType fileType = FileTypeManager.getInstance().findFileTypeByLanguage(language);
        LightVirtualFile virtualFile = new LightVirtualFile("temp", fileType, "");
        Document document = EditorFactory.getInstance().createDocument("");

        return EditorFactory.getInstance()
                .createEditor(document, null, virtualFile, false, EditorKind.PREVIEW);
    }

    private void configureEditor(Editor editor) {
        if (editor instanceof EditorEx editorEx) {
            editorEx.setViewer(false);
            editorEx.setCaretEnabled(false);
        }
    }

    private void addEditorToPanel(Editor editor) {
        add(editor.getComponent(), BorderLayout.CENTER);
    }

    @Override
    public String getText() {
        try {
            return ApplicationManager.getApplication()
                    .runReadAction(
                            (ThrowableComputable<String, Throwable>)
                                    () -> editorFuture.get().getDocument().getText());

        } catch (Throwable e) {
            log.warn("Unable to get editor text", e);
            return "";
        }
    }

    @Override
    public void setText(@NotNull String text) {
        editorFuture =
                editorFuture.thenApply(
                        editor -> {
                            setText(text, editor);
                            return editor;
                        });
    }

    private static void setText(@NotNull String text, Editor editor) {
        ApplicationManager.getApplication()
                .invokeAndWait(
                        () ->
                                ApplicationManager.getApplication()
                                        .runWriteAction(
                                                () -> {
                                                    if (!editor.isDisposed())
                                                        editor.getDocument().setText(text);
                                                }));
    }

    public static @NotNull Language guessLanguage(@NotNull String languageName) {
        var languages = Language.getRegisteredLanguages();

        return languages.stream()
                .filter(lang -> lang.getID().equalsIgnoreCase(languageName))
                .findFirst()
                .orElseGet(
                        () -> {
                            log.debug("Unable to infer language from name: {}", languageName);
                            return Language.ANY;
                        });
    }

    @Override
    public void dispose() {
        try {
            Editor editor = editorFuture.get();
            if (editor != null && !editor.isDisposed()) {
                EditorFactory.getInstance().releaseEditor(editor);
            }
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Unable to dispose editor", e);
        }
    }
}

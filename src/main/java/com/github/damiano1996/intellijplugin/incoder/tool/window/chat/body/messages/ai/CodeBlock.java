package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai;

import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.EditorTextField;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CodeBlock {

    @Getter
    private JPanel mainPanel;

    @Setter
    private Project project;

//    private void createUIComponents() {
//        mainPanel = createEditorField("""
//                <!DOCTYPE html>
//                <html lang="en">
//                <head>
//                  <meta charset="UTF-8">
//                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
//                  <title>Sample Page</title>
//                  <style>
//                    body { font-family: Arial, sans-serif; margin: 20px; }
//                    header, footer { background-color: #f4f4f4; padding: 10px; text-align: center; }
//                    main { margin: 20px 0; }
//                  </style>
//                </head>
//                <body>
//                  <header>
//                    <h1>Welcome to My Page</h1>
//                  </header>
//                  <main>
//                    <p>This is a simple HTML structure.</p>
//                  </main>
//                  <footer>
//                    <p>&copy; 2024 My Website</p>
//                  </footer>
//                </body>
//                </html>
//                """, HtmlFileType.INSTANCE, null, project);
//    }

    public void setText(String text) {
        ApplicationManager.getApplication().invokeLater(() -> {
            Editor editor = EditorFactory.getInstance()
                    .createEditor(FileEditorManager.getInstance(
                                    project).getSelectedTextEditor().getDocument(),
                            project, FileEditorManager.getInstance(
                                    project).getSelectedTextEditor().getVirtualFile().getFileType(), true);

            mainPanel.add(editor.getComponent());
        });



    }

//    private static @NotNull EditorTextField createEditorField(String text, FileType fileType, TextRange rangeToSelect, @Nullable Project project) {
//        Document document = EditorFactory.getInstance().createDocument(text);
//        return EditorFactory.getInstance().cre
//    }
}

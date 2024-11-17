package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import com.github.damiano1996.intellijplugin.incoder.generation.CodeGenerationService;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class MarkdownPanel extends JPanel implements StreamWriter {

    private final List<MarkdownEditorPane> markdownEditorPanes;
    private final List<CodeEditorWriter> codeEditorWriters;
    @Setter @Nullable private Project project;
    private boolean isWritingACodeBlock = false;
    private boolean skipNext = false;

    public MarkdownPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        markdownEditorPanes = new ArrayList<>();
        codeEditorWriters = new ArrayList<>();

        addMarkdownEditorPane();
    }

    @Override
    public void write(@NotNull String token) {
        if (project == null) return;

        if (token.startsWith("```")) {
            skipNext = !isWritingACodeBlock;
            isWritingACodeBlock = !isWritingACodeBlock;

            if (isWritingACodeBlock) {
                addCodeEditorPanel();
            } else {
                addMarkdownEditorPane();
            }
        } else if (skipNext) {
            skipNext = false;
        } else {

            if (isWritingACodeBlock) {
                codeEditorWriters.get(codeEditorWriters.size() - 1).write(token);
            } else {
                markdownEditorPanes.get(markdownEditorPanes.size() - 1).write(token);
            }
        }
    }

    @Override
    public String getFullText() {
        return "";
    }

    private void addCodeEditorPanel() {
        var codeBlock = new CodeEditorWriter(project);
        codeEditorWriters.add(codeBlock);

        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            add(codeBlock.getComponent());

                            var button = new JButton("Merge...");
                            button.addActionListener(
                                    e ->
                                            CodeGenerationService.showDiff(
                                                    project,
                                                    codeBlock.getFullText(),
                                                    Objects.requireNonNull(
                                                            FileEditorManager.getInstance(project)
                                                                    .getSelectedTextEditor())));

                            add(button);
                        });
    }

    private void addMarkdownEditorPane() {
        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            var markdownPane = new MarkdownEditorPane();
                            markdownEditorPanes.add(markdownPane);
                            add(markdownPane);
                        });
    }
}

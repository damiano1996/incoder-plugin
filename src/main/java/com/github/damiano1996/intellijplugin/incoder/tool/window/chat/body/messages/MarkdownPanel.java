package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.jgoodies.forms.layout.FormLayout;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MarkdownPanel extends JPanel implements StreamWriter {

    @Setter @Nullable
    private Project project;

    private final List<MarkdownEditorPane> markdownEditorPanes;
    private final List<CodeEditorPanel> codeEditorPanels;

    private boolean isWritingACodeBlock = false;
    private boolean skipNext = false;

    public MarkdownPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        markdownEditorPanes = new ArrayList<>();
        codeEditorPanels = new ArrayList<>();

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
        }else if (skipNext){
            skipNext = false;
        } else {

            if (isWritingACodeBlock) {
                codeEditorPanels.get(codeEditorPanels.size() - 1).write(token);
            } else {
                markdownEditorPanes.get(markdownEditorPanes.size() - 1).write(token);
            }
        }
    }

    private void addCodeEditorPanel() {
        var codeBlock = new CodeEditorPanel(project);
        codeEditorPanels.add(codeBlock);

        ApplicationManager.getApplication().invokeLater(() -> add(codeBlock.getComponent()));
    }

    private void addMarkdownEditorPane() {
        ApplicationManager.getApplication().invokeLater(() -> {
            var markdownPane = new MarkdownEditorPane();
            markdownEditorPanes.add(markdownPane);
            markdownPane.setOpaque(false);
            markdownPane.setBackground(new JBColor(new Color(0, 0, 0, 0), new Color(0, 0, 0, 0)));
            add(markdownPane);
        });
    }
}

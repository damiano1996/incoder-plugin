package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai;

import static com.github.damiano1996.jetbrains.incoder.InCoderIcons.PLUGIN_ICON;

import com.github.damiano1996.jetbrains.incoder.tool.window.ToolWindowColors;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.MessageComponent;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.MarkdownPanel;
import com.github.damiano1996.jetbrains.incoder.ui.components.RoundedLabel;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import javax.swing.*;
import lombok.Getter;

public class AiMessageComponent implements MessageComponent {

    private final Project project;
    @Getter private JPanel mainPanel;
    private MarkdownPanel markdownPanel;
    private JLabel aiIconLabel;
    private JScrollPane scrollPane;
    private JLabel modelNameLabel;

    public AiMessageComponent(Project project) {
        this.project = project;
    }

    public void setModelName(String modelName) {
        modelNameLabel.setText(modelName);
        modelNameLabel.setVisible(true);
    }

    @Override
    public void write(String token) {
        this.markdownPanel.write(token);
    }

    @Override
    public String getText() {
        return markdownPanel.getText();
    }

    @Override
    public void streamClosed() {
        markdownPanel.streamClosed();
    }

    private void createUIComponents() {
        mainPanel = new JPanel();

        scrollPane = new JBScrollPane();

        markdownPanel = new MarkdownPanel(project);

        aiIconLabel = new JBLabel(PLUGIN_ICON);

        modelNameLabel = new RoundedLabel(20, 20, 15, 2);
        modelNameLabel.setBackground(ToolWindowColors.BADGE_BACKGROUND);
        modelNameLabel.setForeground(ToolWindowColors.BADGE_FOREGROUND);
        modelNameLabel.setVisible(false);
    }
}

package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai;

import static com.github.damiano1996.jetbrains.incoder.InCoderIcons.PLUGIN_ICON;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.MessageComponent;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.MarkdownPanel;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class AiMessageComponent implements MessageComponent {

    private final Project project;
    private final String modelName;

    @Getter private JPanel mainPanel;
    private MarkdownPanel markdownPanel;

    public AiMessageComponent(Project project, String modelName) {
        this.project = project;
        this.modelName = modelName;
        createUIComponents();
    }

    public AiMessageComponent(Project project) {
        this.project = project;
        this.modelName = null;
        createUIComponents();
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
        markdownPanel = new MarkdownPanel(project);
        markdownPanel.setFocusable(true);
        markdownPanel.setOpaque(false);

        JScrollPane scrollPane = new JBScrollPane(markdownPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setAutoscrolls(true);
        scrollPane.setFocusable(false);
        scrollPane.setOpaque(true);
        scrollPane.setWheelScrollingEnabled(true);

        FormBuilder builder = FormBuilder.createFormBuilder();

        if (modelName != null) {
            JLabel aiIconLabel = getAiIconLabel();
            JLabel modelNameLabel = getModelNameLabel();
            builder.addLabeledComponent(aiIconLabel, modelNameLabel, 1, false);
        }

        mainPanel =
                builder.setFormLeftIndent(10).addComponentFillVertically(scrollPane, 0).getPanel();

        mainPanel.setAutoscrolls(true);
        mainPanel.setEnabled(false);
        mainPanel.setFocusable(false);
        mainPanel.setOpaque(true);
    }

    private static @NotNull JLabel getAiIconLabel() {
        JLabel aiIconLabel = new JBLabel(PLUGIN_ICON);
        aiIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        aiIconLabel.setVerticalAlignment(SwingConstants.CENTER);
        aiIconLabel.setFocusable(false);
        aiIconLabel.setOpaque(false);
        return aiIconLabel;
    }

    private @NotNull JLabel getModelNameLabel() {
        JLabel modelNameLabel = new JBLabel(modelName);
        modelNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        modelNameLabel.setOpaque(false);
        return modelNameLabel;
    }
}

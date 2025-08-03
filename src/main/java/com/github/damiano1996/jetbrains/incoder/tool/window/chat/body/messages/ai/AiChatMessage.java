package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai;

import static com.github.damiano1996.jetbrains.incoder.InCoderIcons.PLUGIN_ICON;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ChatMessage;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class AiChatMessage implements ChatMessage {

    private final String modelName;

    @Getter private JPanel mainPanel;

    public AiChatMessage(String modelName) {
        this.modelName = modelName;
        createUIComponents();
    }

    private void createUIComponents() {
        FormBuilder builder = FormBuilder.createFormBuilder();

        JLabel aiIconLabel = getAiIconLabel();
        JLabel modelNameLabel = getModelNameLabel();
        mainPanel = builder.addLabeledComponent(aiIconLabel, modelNameLabel, 0, false).getPanel();

        mainPanel.setEnabled(false);
        mainPanel.setFocusable(false);
        mainPanel.setOpaque(true);
    }

    private static @NotNull JLabel getAiIconLabel() {
        JLabel aiIconLabel = new JBLabel(PLUGIN_ICON);
        aiIconLabel.setHorizontalAlignment(SwingConstants.LEFT);
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

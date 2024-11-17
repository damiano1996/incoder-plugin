package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import com.github.damiano1996.intellijplugin.incoder.llm.Llm;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import javax.swing.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class HumanMessage implements MessageComponent {

    private JTextArea message;

    @Getter private JPanel mainPanel;
    private JLabel promptTypeLabel;

    public void setPromptTypeLabel(Llm.@NotNull PromptType promptType) {
        promptTypeLabel.setText(promptType.name().toLowerCase());
        promptTypeLabel.setVisible(true);
    }

    @Override
    public void setProject(Project project) {}

    @Override
    public MessageComponent setMessage(String message) {
        this.message.setText(message);
        return this;
    }

    private void createUIComponents() {
        message = new RoundedTextArea(35, 35);
        message.setBackground(JBColor.namedColor("Label.foreground"));
        message.setForeground(JBColor.namedColor("Label.background"));

        promptTypeLabel = new RoundedLabel(20, 20, 15, 2);
        promptTypeLabel.setBackground(JBColor.namedColor("Label.background"));
        promptTypeLabel.setForeground(JBColor.namedColor("Label.foreground"));
        promptTypeLabel.setVisible(false);
    }
}

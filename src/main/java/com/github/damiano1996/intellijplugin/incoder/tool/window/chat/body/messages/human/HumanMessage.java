package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.human;

import com.github.damiano1996.intellijplugin.incoder.language.model.PromptType;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.MessageComponent;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.RoundedLabel;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.RoundedTextArea;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import javax.swing.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class HumanMessage implements MessageComponent {

    private JTextArea message;

    @Getter private JPanel mainPanel;
    private JLabel promptTypeLabel;

    public void setPromptTypeLabel(@NotNull PromptType promptType) {
        promptTypeLabel.setText(promptType.getDisplayName());
        promptTypeLabel.setVisible(true);
    }

    @Override
    public void setProject(Project project) {}

    @Override
    public void write(String token) {
        this.message.setText(token);
    }

    @Override
    public String getFullText() {
        return message.getText();
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

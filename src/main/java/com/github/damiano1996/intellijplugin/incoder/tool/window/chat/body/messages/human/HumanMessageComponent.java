package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.human;

import com.github.damiano1996.intellijplugin.incoder.language.model.PromptType;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.MessageComponent;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.RoundedLabel;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.RoundedTextArea;
import com.intellij.ui.JBColor;
import javax.swing.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class HumanMessageComponent implements MessageComponent {

    private final String userPrompt;

    private JTextArea promptTextArea;

    @Getter private JPanel mainPanel;
    private JLabel promptTypeLabel;

    public HumanMessageComponent(String userPrompt) {
        this.userPrompt = userPrompt;
    }

    public void setPromptTypeLabel(@NotNull PromptType promptType) {
        promptTypeLabel.setText(promptType.getDisplayName());
        promptTypeLabel.setVisible(true);
    }

    @Override
    public void write(String token) {
        this.promptTextArea.setText(token);
    }

    @Override
    public String getFullText() {
        return promptTextArea.getText();
    }

    private void createUIComponents() {
        promptTextArea = new RoundedTextArea(35, 35);
        promptTextArea.setBackground(JBColor.namedColor("Label.foreground"));
        promptTextArea.setForeground(JBColor.namedColor("Label.background"));
        promptTextArea.setText(userPrompt);

        promptTypeLabel = new RoundedLabel(20, 20, 15, 2);
        promptTypeLabel.setBackground(JBColor.namedColor("Label.background"));
        promptTypeLabel.setForeground(JBColor.namedColor("Label.foreground"));
        promptTypeLabel.setVisible(false);
    }
}

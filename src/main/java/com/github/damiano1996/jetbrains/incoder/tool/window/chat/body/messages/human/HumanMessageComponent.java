package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.human;

import com.github.damiano1996.jetbrains.incoder.language.model.client.prompt.PromptType;
import com.github.damiano1996.jetbrains.incoder.tool.window.ToolWindowColors;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.MessageComponent;
import com.github.damiano1996.jetbrains.incoder.ui.components.RoundedLabel;
import com.github.damiano1996.jetbrains.incoder.ui.components.RoundedTextArea;
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
        promptTextArea.setBackground(ToolWindowColors.USER_MESSAGE_BACKGROUND);
        promptTextArea.setForeground(ToolWindowColors.USER_MESSAGE_FOREGROUND);
        promptTextArea.setText(userPrompt);

        promptTypeLabel = new RoundedLabel(20, 20, 15, 2);
        promptTypeLabel.setBackground(ToolWindowColors.BADGE_BACKGROUND);
        promptTypeLabel.setForeground(ToolWindowColors.BADGE_FOREGROUND);
        promptTypeLabel.setVisible(false);
    }
}

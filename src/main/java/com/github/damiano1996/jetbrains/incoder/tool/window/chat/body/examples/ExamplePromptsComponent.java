package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.examples;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.IconUtil;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Component that displays example prompts to help users get started with the chat. This component
 * is shown when the chat is empty and hidden after the first message is sent.
 */
public class ExamplePromptsComponent {

    @Getter private JPanel mainPanel;

    private final List<String> examplePrompts =
            Arrays.asList(
                    "Explain this code and suggest improvements",
                    "Refactor this code to follow SOLID principles",
                    "Generate documentation for this class",
                    "Find potential bugs in this code",
                    "Create a builder pattern for this class",
                    "Optimize this algorithm for better performance");

    private final ActionListener onPromptSelected;

    public ExamplePromptsComponent(ActionListener onPromptSelected) {
        this.onPromptSelected = onPromptSelected;
        createUIComponents();
    }

    private void createUIComponents() {
        JBLabel titleLabel = new JBLabel("Try these example prompts:");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));

        JBLabel subtitleLabel =
                new JBLabel("Click on any prompt to get started, or type your own message below.");
        subtitleLabel.setForeground(JBUI.CurrentTheme.Label.disabledForeground());

        FormBuilder formBuilder =
                FormBuilder.createFormBuilder()
                        .addComponent(titleLabel)
                        .addComponent(subtitleLabel)
                        .addVerticalGap(10)
                        .setFormLeftIndent(20);

        for (String prompt : examplePrompts) {

            JButton button = createButton(prompt);
            formBuilder.addComponent(button);
        }

        mainPanel = formBuilder.getPanel();
        mainPanel.setBorder(JBUI.Borders.empty(20));
        mainPanel.setOpaque(false);
    }

    private @NotNull JButton createButton(String prompt) {
        Icon coloredIcon =
                IconUtil.colorize(AllIcons.Actions.Lightning, JBUI.CurrentTheme.Focus.focusColor());

        JButton button = new JButton(prompt, coloredIcon);
        button.addActionListener(
                e -> {
                    if (onPromptSelected != null) {
                        onPromptSelected.actionPerformed(new ActionEvent(button, 0, prompt));
                    }
                });
        return button;
    }
}

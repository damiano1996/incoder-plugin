package com.github.damiano1996.jetbrains.incoder.tool.window.intelligent.actions;

import static com.github.damiano1996.jetbrains.incoder.InCoderIcons.PLUGIN_ICON;

import com.github.damiano1996.jetbrains.incoder.ui.components.MarkdownEditorPane;
import com.intellij.ui.components.JBLabel;
import java.awt.event.ActionListener;
import javax.swing.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IntelligentActionComponent {

    private MarkdownEditorPane descriptionTextArea;
    private JButton button;
    private JLabel actionIcon;
    private JLabel titleLabel;

    @Getter private JPanel mainPanel;

    private void createUIComponents() {
        mainPanel = new JPanel();
        //        mainPanel.setBackground(Color.RED);
        //        mainPanel.setBorder(new
        // RoundedLineBorder(ToolWindowColors.INTELLIGENT_ACTION_BACKGROUND, 5, 10));

        actionIcon = new JBLabel(PLUGIN_ICON);
        titleLabel = new JBLabel();
        descriptionTextArea = new MarkdownEditorPane();
        button = new JButton();
    }

    public IntelligentActionComponent setIcon(Icon icon) {
        actionIcon.setIcon(icon);
        return this;
    }

    public IntelligentActionComponent setTitle(String title) {
        titleLabel.setText(title);
        return this;
    }

    public IntelligentActionComponent setDescription(String description) {
        descriptionTextArea.setText(description);
        return this;
    }

    public IntelligentActionComponent setActionListener(ActionListener actionListener) {
        button.addActionListener(actionListener);
        return this;
    }
}

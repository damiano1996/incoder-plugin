package com.github.damiano1996.jetbrains.incoder.tool.window.agents.cards;

import static com.github.damiano1996.jetbrains.incoder.InCoderIcons.PLUGIN_ICON;

import com.github.damiano1996.jetbrains.incoder.ui.components.MarkdownEditorPane;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.UIUtil;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
@NoArgsConstructor
public class AgentCard {

    private MarkdownEditorPane descriptionTextArea;
    private JButton chooseButton;
    private JLabel actionIcon;
    private JLabel titleLabel;

    @Getter private JPanel mainPanel;
    private JScrollPane scrollPane;
    private JButton stopButton;

    private JPanel bodyPanel;

    private boolean cancelExecution = false;

    private void createUIComponents() {
        mainPanel = new JPanel();

        actionIcon = new JBLabel(PLUGIN_ICON);
        titleLabel = new JBLabel();
        titleLabel.setFont(JBFont.label().asBold());

        descriptionTextArea = new MarkdownEditorPane();
        chooseButton = new JButton();
        stopButton = new JButton();
        stopButton.setBackground(UIUtil.getErrorForeground());

        updateButtons(false);

        // Create messages panel with proper constraints
        bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(JBColor.namedColor("ToolWindow.background"));
        bodyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        // Create a wrapper panel to hold messagesPanel
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(JBColor.namedColor("ToolWindow.background"));
        wrapperPanel.add(bodyPanel, BorderLayout.CENTER);

        // Create scroll pane
        scrollPane = new JBScrollPane(wrapperPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public AgentCard setIcon(Icon icon) {
        actionIcon.setIcon(icon);
        return this;
    }

    public AgentCard setAgent(@NotNull Agent agent) {
        titleLabel.setText(agent.getDisplayName());
        descriptionTextArea.setText(agent.getDescription());

        chooseButton.addActionListener(
                event -> {
                    updateButtons(true);
                    cancelExecution = false;

                    agent.execute(
                                    component -> {
                                        bodyPanel.removeAll();
                                        bodyPanel.add(component);
                                    },
                                    () -> cancelExecution)
                            .thenAccept(unused -> updateButtons(false))
                            .exceptionally(
                                    throwable -> {
                                        log.error("Something went wrong", throwable);
                                        updateButtons(false);
                                        return null;
                                    });
                });

        stopButton.addActionListener(
                event -> {
                    cancelExecution = true;
                    stopButton.setEnabled(false);
                    stopButton.setText("Stopping...");
                });

        return this;
    }

    private void updateButtons(boolean isExecuting) {
        chooseButton.setEnabled(!isExecuting);

        stopButton.setEnabled(isExecuting);
        stopButton.setText("Stop");
        stopButton.setVisible(isExecuting);
    }
}

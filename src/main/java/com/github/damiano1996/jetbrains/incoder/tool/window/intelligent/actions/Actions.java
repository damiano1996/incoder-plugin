package com.github.damiano1996.jetbrains.incoder.tool.window.intelligent.actions;

import com.github.damiano1996.jetbrains.incoder.ClassInstantiator;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Actions {

    @Getter private JPanel mainPanel;

    private JPanel actionsPanel;
    private JScrollPane scrollPane;

    private void createUIComponents() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(JBColor.namedColor("ToolWindow.background"));

        // Create messages panel with proper constraints
        actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBackground(JBColor.namedColor("ToolWindow.background"));
        actionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionsPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        // Create a wrapper panel to hold messagesPanel
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(JBColor.namedColor("ToolWindow.background"));
        wrapperPanel.add(actionsPanel, BorderLayout.NORTH);

        // Create scroll pane
        scrollPane = new JBScrollPane(wrapperPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        var intelligentActions = ClassInstantiator.findImplementations(IntelligentAction.class);

        intelligentActions.forEach(
                intelligentAction ->
                        actionsPanel.add(
                                new IntelligentActionComponent()
                                        .setTitle(intelligentAction.getName())
                                        .setDescription(intelligentAction.getDescription())
                                        .setActionListener(intelligentAction.getActionListener())
                                        .getMainPanel()));
    }
}

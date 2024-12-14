package com.github.damiano1996.jetbrains.incoder.tool.window.intelligent.actions;

import static com.github.damiano1996.jetbrains.incoder.InCoderIcons.PLUGIN_ICON;

import com.github.damiano1996.jetbrains.incoder.tool.window.ToolWindowColors;
import com.github.damiano1996.jetbrains.incoder.ui.components.MarkdownEditorPane;
import com.intellij.ui.JBColor;
import com.intellij.ui.RoundedLineBorder;
import com.intellij.ui.components.JBLabel;

import javax.swing.*;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBFont;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@Slf4j
@NoArgsConstructor
public class IntelligentActionComponent implements IntelligentActionObserver {

    private MarkdownEditorPane descriptionTextArea;
    private JButton button;
    private JLabel actionIcon;
    private JLabel titleLabel;

    @Getter private JPanel mainPanel;
    private JProgressBar progressBar;
    private JScrollPane scrollPane;

    private JPanel outputsPanel;

    private void createUIComponents() {
        mainPanel = new JPanel();

        actionIcon = new JBLabel(PLUGIN_ICON);
        titleLabel = new JBLabel();
        titleLabel.setFont(JBFont.label().asBold());

        descriptionTextArea = new MarkdownEditorPane();
        button = new JButton();
        progressBar = new JProgressBar();

        updateProgressStatus(false);

        // Create messages panel with proper constraints
        outputsPanel = new JPanel();
        outputsPanel.setLayout(new BoxLayout(outputsPanel, BoxLayout.Y_AXIS));
        outputsPanel.setBackground(JBColor.namedColor("ToolWindow.background"));
        outputsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        outputsPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        // Create a wrapper panel to hold messagesPanel
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(JBColor.namedColor("ToolWindow.background"));
        wrapperPanel.add(outputsPanel, BorderLayout.NORTH);

        // Create scroll pane
        scrollPane = new JBScrollPane(wrapperPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
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

    public IntelligentActionComponent setExecutor(@NotNull IntelligentActionExecutor executor) {
        executor.setObserver(this);

        button.addActionListener(event -> {
            updateProgressStatus(true);

            try {
                executor.execute();
            } catch (Exception e){
                log.error("Something went wrong", e);
                updateProgressStatus(false);
            }

        });
        return this;
    }

    private void updateProgressStatus(boolean isExecuting) {
        progressBar.setIndeterminate(isExecuting);
        progressBar.setVisible(isExecuting);
        button.setEnabled(!isExecuting);
    }

    @Override
    public void onProgressUpdate(String text) {
        log.debug("Progress update: {}", text);
        // outputsPanel.setText(outputsPanel.getText() + "\n" + text);
    }

    @Override
    public void onGeneratedArtifact(JComponent component) {
        outputsPanel.add(component);
    }

    @Override
    public void onActionCompleted() {
        updateProgressStatus(false);
    }
}

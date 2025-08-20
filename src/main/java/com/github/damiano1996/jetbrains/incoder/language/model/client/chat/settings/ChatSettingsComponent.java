// ChatSettingsComponent.java
package com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings;

import com.github.damiano1996.jetbrains.incoder.ui.components.DescriptionLabel;
import com.intellij.icons.AllIcons;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.FormBuilder;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import lombok.Getter;

@Getter
public class ChatSettingsComponent {

    private final JPanel mainPanel;

    private final JSpinner maxMessages;
    private final JBTextArea systemMessageInstructionsField;
    private final JButton reloadDefaultButton;

    // Tools granulari
    private final JBCheckBox enableFileTool;
    private final JBCheckBox enableEditorTool;
    private final JBCheckBox enableCommandLineTool;

    // MCP
    private final JBCheckBox enableMcp;
    private final DefaultListModel<ChatSettings.McpConfig> mcpListModel;
    private final JList<ChatSettings.McpConfig> mcpList;

    public ChatSettingsComponent() {
        SpinnerNumberModel maxMessagesModel = new SpinnerNumberModel(10, 0, 50, 1);
        maxMessages = new JSpinner(maxMessagesModel);

        systemMessageInstructionsField = new JBTextArea(5, 20);
        systemMessageInstructionsField.setLineWrap(true);
        systemMessageInstructionsField.setWrapStyleWord(true);

        reloadDefaultButton = new JButton(AllIcons.Actions.Refresh);
        reloadDefaultButton.setToolTipText("Reload default system message");
        reloadDefaultButton.setBorderPainted(false);
        reloadDefaultButton.setContentAreaFilled(false);
        reloadDefaultButton.setFocusPainted(false);
        reloadDefaultButton.addActionListener(
                e ->
                        systemMessageInstructionsField.setText(
                                ChatSettings.State.loadDefaultSystemPrompt()));

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
        labelPanel.add(new JBLabel("System message instructions:"));
        labelPanel.add(Box.createHorizontalGlue());
        labelPanel.add(reloadDefaultButton);

        // === Tools built-in ==================================================
        enableFileTool = new JBCheckBox("Enable fileTool");
        enableEditorTool = new JBCheckBox("Enable editorTool");
        enableCommandLineTool = new JBCheckBox("Enable commandLineTool");

        // === MCP =============================================================
        enableMcp = new JBCheckBox("Enable MCP");

        mcpListModel = new DefaultListModel<>();
        mcpList = new JBList<>(mcpListModel);
        mcpList.setCellRenderer(new McpListCellRenderer());

        JPanel mcpPanel = new JPanel(new BorderLayout());
        mcpPanel.add(new JBLabel("MCP servers:"), BorderLayout.NORTH);

        ToolbarDecorator decorator =
                ToolbarDecorator.createDecorator(mcpList)
                        .setAddAction(button -> {
                            McpConfigDialog dialog = new McpConfigDialog(null);
                            if (dialog.showAndGet()) {
                                mcpListModel.addElement(dialog.getValue());
                            }
                        })
                        .setEditAction(button -> {
                            ChatSettings.McpConfig selected = mcpList.getSelectedValue();
                            if (selected == null) return;
                            McpConfigDialog dialog = new McpConfigDialog(selected);
                            if (dialog.showAndGet()) {
                                int idx = mcpList.getSelectedIndex();
                                mcpListModel.set(idx, dialog.getValue());
                            }
                        })
                        .setRemoveAction(button -> {
                            int idx = mcpList.getSelectedIndex();
                            if (idx >= 0) mcpListModel.remove(idx);
                        })
                        .disableUpDownActions();

        mcpPanel.add(decorator.createPanel(), BorderLayout.CENTER);

        mainPanel =
                FormBuilder.createFormBuilder()
                        .setFormLeftIndent(20)
                        .addLabeledComponent(new JBLabel("Max messages:"), maxMessages, 0, false)
                        .addComponent(new DescriptionLabel("Number of messages to keep in memory."))
                        .addVerticalGap(20)
                        .setFormLeftIndent(20)
                        .addComponent(labelPanel)
                        .addComponent(
                                ScrollPaneFactory.createScrollPane(systemMessageInstructionsField))
                        .addComponent(
                                new DescriptionLabel(
                                        "Custom system prompt that defines the AI assistant's"
                                        + " behavior, role, and response style for all chat"
                                        + " interactions."))
                        .addVerticalGap(20)
                        .addComponent(new JBLabel("Built-in tools:"))
                        .addComponent(enableFileTool)
                        .addComponent(enableEditorTool)
                        .addComponent(enableCommandLineTool)
                        .addVerticalGap(20)
                        .addComponent(enableMcp)
                        .addComponent(mcpPanel)
                        .addComponent(
                                new DescriptionLabel(
                                        "Configure one or more MCP servers (Model Context Protocol)"
                                        + " to expose external tools to the LLM."))
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();
    }

    public List<ChatSettings.McpConfig> getMcpConfigs() {
        List<ChatSettings.McpConfig> list = new ArrayList<>();
        for (int i = 0; i < mcpListModel.size(); i++) {
            list.add(mcpListModel.get(i));
        }
        return list;
    }

    public void setMcpConfigs(List<ChatSettings.McpConfig> configs) {
        mcpListModel.clear();
        if (configs != null) configs.forEach(mcpListModel::addElement);
    }

    // === Renderer per entry MCP =============================================
    private static class McpListCellRenderer extends DefaultListCellRenderer {
        @Override public java.awt.Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            ChatSettings.McpConfig cfg = (ChatSettings.McpConfig) value;
            String cmd = String.join(" ", cfg.command);
            c.setText(String.format("[%s] %s%s",
                    cfg.enabled ? "ON" : "OFF",
                    cfg.key != null ? cfg.key : "(no-key)",
                    cmd.isEmpty() ? "" : "  —  " + cmd));
            return c;
        }
    }
}

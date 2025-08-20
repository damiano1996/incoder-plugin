package com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.FormBuilder;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class McpConfigDialog extends DialogWrapper {

    private final JTextField keyField = new JTextField();
    private final JTextField commandField = new JTextField();
    private final JBTextArea envField = new JBTextArea(6, 40);
    private final JBCheckBox enabled = new JBCheckBox("Enabled", true);
    private final JBCheckBox logEvents = new JBCheckBox("Log MCP events", false);

    @Getter
    private ChatSettings.McpConfig value;

    public McpConfigDialog(@Nullable ChatSettings.McpConfig initial) {
        super(true);
        setTitle(initial == null ? "Add MCP Server" : "Edit MCP Server");
        if (initial != null) {
            keyField.setText(initial.key);
            commandField.setText(String.join(" ", initial.command));
            envField.setText(envToMultiline(initial.env));
            enabled.setSelected(initial.enabled);
            logEvents.setSelected(initial.logEvents);
        } else {
            // sensible defaults for the memory server
            keyField.setText("memory");
            commandField.setText("npx -y @modelcontextprotocol/server-memory");
            logEvents.setSelected(true);
        }
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        envField.setLineWrap(true);
        envField.setWrapStyleWord(true);
        JScrollPane envScroll = new JBScrollPane(envField);
        envScroll.setPreferredSize(new Dimension(400, 120));

        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Key (clientName):", keyField)
                .addLabeledComponent("Command (space-separated):", commandField)
                .addLabeledComponent("Env (one per line: KEY=VALUE):", envScroll)
                .addComponent(enabled)
                .addComponent(logEvents)
                .getPanel();
    }

    @Override
    protected void doOKAction() {
        ChatSettings.McpConfig cfg = new ChatSettings.McpConfig();
        cfg.key = keyField.getText().trim();
        cfg.command = splitCommand(commandField.getText().trim());
        cfg.env = parseEnv(envField.getText());
        cfg.enabled = enabled.isSelected();
        cfg.logEvents = logEvents.isSelected();
        this.value = cfg;
        super.doOKAction();
    }

    private static List<String> splitCommand(@NotNull String s) {
        if (s.isEmpty()) return List.of();
        // naive split on spaces; users can quote via UI later if necessario
        return Arrays.asList(s.split("\\s+"));
    }

    private static @NotNull List<ChatSettings.EnvVar> parseEnv(@NotNull String text) {
        List<ChatSettings.EnvVar> list = new ArrayList<>();
        String[] lines = text.split("\\R");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;
            int eq = trimmed.indexOf('=');
            if (eq <= 0) continue;
            String k = trimmed.substring(0, eq).trim();
            String v = trimmed.substring(eq + 1).trim();
            list.add(new ChatSettings.EnvVar(k, v));
        }
        return list;
    }

    private static @NotNull String envToMultiline(List<ChatSettings.EnvVar> env) {
        StringBuilder sb = new StringBuilder();
        if (env != null) {
            for (ChatSettings.EnvVar e : env) {
                sb.append(e.key).append("=").append(e.value == null ? "" : e.value).append("\n");
            }
        }
        return sb.toString();
    }
}

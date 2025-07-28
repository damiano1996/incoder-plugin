package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.tool;

import static com.github.damiano1996.jetbrains.incoder.tool.window.chat.ChatConstants.*;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.MessageComponent;
import com.intellij.ui.RoundedLineBorder;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import dev.langchain4j.service.tool.ToolExecution;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import lombok.Getter;

public class ToolMessageComponent implements MessageComponent {

    private final ToolExecution toolExecution;

    @Getter private JPanel mainPanel;

    public ToolMessageComponent(ToolExecution toolExecution) {
        this.toolExecution = toolExecution;
        createUIComponents();
    }

    private void createUIComponents() {
        JTextField result = new JTextField(toolExecution.result());
        result.setBorder(JBUI.Borders.empty());

        mainPanel =
                FormBuilder.createFormBuilder()
                        .setFormLeftIndent(10)
                        .addLabeledComponent(
                                new JBLabel(toolExecution.request().name()), result, 5, true)
                        .getPanel();

        mainPanel.setBorder(
                new CompoundBorder(
                        new RoundedLineBorder(
                                JBUI.CurrentTheme.Focus.focusColor(), ARC_DIAMETER, THICKNESS),
                        JBUI.Borders.empty(PADDING)));
    }

    @Override
    public void write(String token) {}

    @Override
    public String getText() {
        return toolExecution.toString();
    }

    @Override
    public void streamClosed() {}
}

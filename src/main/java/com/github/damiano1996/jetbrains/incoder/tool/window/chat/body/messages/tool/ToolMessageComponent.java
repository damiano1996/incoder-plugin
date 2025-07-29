package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.tool;

import static com.github.damiano1996.jetbrains.incoder.tool.window.chat.ChatConstants.ARC_DIAMETER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.MessageComponent;
import com.github.damiano1996.jetbrains.incoder.ui.components.RoundedUtils;
import com.intellij.icons.AllIcons;
import com.intellij.ide.HelpTooltip;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.IconUtil;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import dev.langchain4j.service.tool.ToolExecution;
import java.awt.*;
import java.util.stream.Collectors;
import javax.swing.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class ToolMessageComponent implements MessageComponent {

    private final ToolExecution toolExecution;

    @Getter private JPanel mainPanel;

    public ToolMessageComponent(ToolExecution toolExecution) {
        this.toolExecution = toolExecution;
        createUIComponents();
    }

    private void createUIComponents() {

        Icon coloredIcon =
                IconUtil.colorize(AllIcons.General.Note, JBUI.CurrentTheme.Focus.focusColor());
        JBLabel icon = new JBLabel("Tool:", coloredIcon, 10);

        JPanel toolPanel =
                FormBuilder.createFormBuilder()
                        .addLabeledComponent(
                                icon, new JBLabel(toolExecution.request().name()), 0, false)
                        .getPanel();
        toolPanel.setOpaque(true);
        toolPanel.setBackground(JBColor.background().brighter());

        new HelpTooltip()
                .setTitle("Input arguments")
                .setDescription("<html>%s</html>".formatted(getToolInputArgs()))
                .installOn(toolPanel);

        JPanel wrapper =
                new JBPanel<>(new BorderLayout()) {
                    @Override
                    protected Graphics getComponentGraphics(Graphics g) {
                        return RoundedUtils.getRoundedComponentGraphics(this, g, ARC_DIAMETER);
                    }
                };
        wrapper.setBorder(JBUI.Borders.empty(10));

        wrapper.setOpaque(false);
        wrapper.setBackground(JBColor.background().brighter());
        wrapper.add(toolPanel);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(JBUI.Borders.empty(10, 10, 10, 200));
        mainPanel.add(wrapper);
    }

    private @NotNull String getToolInputArgs() {
        try {
            return "<ul>%s</ul>"
                    .formatted(
                            ToolInputArgs.fromStringAsMap(toolExecution.request().arguments())
                                    .args
                                    .entrySet()
                                    .stream()
                                    .map(
                                            e ->
                                                    "<li><b>%s</b>: %s</li>"
                                                            .formatted(e.getKey(), e.getValue()))
                                    .collect(Collectors.joining("")));
        } catch (JsonProcessingException e) {
            return toolExecution.request().arguments();
        }
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

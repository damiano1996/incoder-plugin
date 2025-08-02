package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.error;

import static com.github.damiano1996.jetbrains.incoder.tool.window.chat.ChatConstants.ARC_DIAMETER;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ChatMessage;
import com.github.damiano1996.jetbrains.incoder.ui.components.RoundedUtils;
import com.intellij.icons.AllIcons;
import com.intellij.ide.HelpTooltip;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;

public class ErrorChatMessage implements ChatMessage {

    private final Throwable throwable;

    @Getter private JPanel mainPanel;

    public ErrorChatMessage(Throwable throwable) {
        this.throwable = throwable;
        createUIComponents();
    }

    private void createUIComponents() {

        Icon coloredIcon = AllIcons.General.Error;
        JBLabel icon = new JBLabel("Error:", coloredIcon, 10);

        JPanel toolPanel =
                FormBuilder.createFormBuilder()
                        .addLabeledComponent(icon, new JBLabel(throwable.getMessage()), 0, false)
                        .getPanel();
        toolPanel.setOpaque(true);
        toolPanel.setBackground(JBUI.CurrentTheme.NotificationError.backgroundColor());

        new HelpTooltip()
                .setTitle("Error message")
                .setDescription(throwable.getMessage())
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
        wrapper.setBackground(JBUI.CurrentTheme.NotificationError.backgroundColor());
        wrapper.add(toolPanel);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(JBUI.Borders.empty(10));
        mainPanel.add(wrapper);
    }
}

package com.github.damiano1996.jetbrains.incoder.language.model.client.tools.commandline;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import java.awt.*;
import javax.swing.*;

public class CommandConfirmationDialog extends DialogWrapper {
    private final String command;
    private final String workingDirectory;

    protected CommandConfirmationDialog(Project project, String command, String workingDirectory) {
        super(project);
        this.command = command;
        this.workingDirectory = workingDirectory;
        setTitle("InCoder - Command Execution Request");
        setOKButtonText("Execute Command");
        setCancelButtonText("Cancel");
        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");
        String osInfo = String.format("%s %s (%s)", osName, osVersion, osArch);

        JBTextArea commandArea = new JBTextArea(command);
        commandArea.setEditable(false);
        commandArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        commandArea.setBorder(JBUI.Borders.empty(8));
        commandArea.setLineWrap(true);
        commandArea.setWrapStyleWord(true);

        JBScrollPane commandScrollPane = new JBScrollPane(commandArea);
        commandScrollPane.setPreferredSize(new Dimension(500, 80));
        commandScrollPane.setBorder(JBUI.Borders.customLine(JBUI.CurrentTheme.Editor.BORDER_COLOR));

        JPanel warningPanel = new JPanel(new BorderLayout());
        warningPanel.setBackground(JBUI.CurrentTheme.NotificationWarning.backgroundColor());
        warningPanel.setBorder(
                JBUI.Borders.compound(
                        JBUI.Borders.customLine(
                                JBUI.CurrentTheme.NotificationWarning.borderColor()),
                        JBUI.Borders.empty(8)));

        JBLabel warningIcon = new JBLabel(AllIcons.General.Warning);
        warningIcon.setBorder(JBUI.Borders.emptyRight(12));

        JBLabel warningText =
                new JBLabel(
                        """
<html>
<b>Security Warning:</b> The AI assistant is requesting to execute a system command.<br/>
Please review the command carefully before proceeding. Only execute commands you trust.
</html>
""");
        warningText.setForeground(JBUI.CurrentTheme.NotificationWarning.foregroundColor());

        warningPanel.add(warningIcon, BorderLayout.WEST);
        warningPanel.add(warningText, BorderLayout.CENTER);

        // Build the form
        return FormBuilder.createFormBuilder()
                .addComponent(warningPanel)
                .addVerticalGap(12)
                .addLabeledComponent("InCoder wants to execute:", commandScrollPane)
                .addVerticalGap(8)
                .addLabeledComponent("Working directory:", new JBLabel(workingDirectory))
                .addVerticalGap(4)
                .addLabeledComponent("Operating system:", new JBLabel(osInfo))
                .addVerticalGap(4)
                .addLabeledComponent("Shell:", new JBLabel(getShellInfo(osName)))
                .addVerticalGap(12)
                .addComponent(createInfoPanel())
                .getPanel();
    }

    private String getShellInfo(String osName) {
        if (osName.toLowerCase().contains("win")) {
            return "Windows Command Prompt (cmd.exe)";
        } else {
            return "Bash Shell (/bin/bash)";
        }
    }

    private JComponent createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(JBUI.CurrentTheme.NotificationInfo.backgroundColor());
        infoPanel.setBorder(
                JBUI.Borders.compound(
                        JBUI.Borders.customLine(JBUI.CurrentTheme.NotificationInfo.borderColor()),
                        JBUI.Borders.empty(8)));

        JBLabel infoIcon = new JBLabel(AllIcons.General.Information);
        infoIcon.setBorder(JBUI.Borders.emptyRight(12));

        JBLabel infoText =
                new JBLabel(
                        """
                        <html>
                        <b>What happens next:</b><br/>
                        • The command will be executed in a new IntelliJ terminal tab<br/>
                        • You can monitor progress and interact with the command in real-time<br/>
                        • The terminal will remain open for further use</html>
                        """);
        infoText.setForeground(JBUI.CurrentTheme.NotificationInfo.foregroundColor());

        infoPanel.add(infoIcon, BorderLayout.WEST);
        infoPanel.add(infoText, BorderLayout.CENTER);

        return infoPanel;
    }
}

package com.github.damiano1996.jetbrains.incoder.language.model.client.tools;

import com.github.damiano1996.jetbrains.incoder.InCoderBundle;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.terminal.LocalTerminalDirectRunner;
import org.jetbrains.plugins.terminal.TerminalTabState;
import org.jetbrains.plugins.terminal.TerminalToolWindowManager;

@Slf4j
@AllArgsConstructor
public class CommandLineTool {

    private static final String TERMINAL_TOOL_WINDOW_ID = "Terminal";
    private final Project project;

    @Tool(
            name = "COMMAND_LINE_TOOL",
            value =
                    """
Executes a shell command in the IntelliJ integrated terminal after obtaining user approval through a confirmation dialog.
The command will be executed in a new terminal tab, allowing real-time monitoring of the execution.
This tool provides a safe way to run system commands with user oversight and visual feedback.
""")
    public String executeCommand(
            @P(
                            """
The shell command to execute. Should be a valid command for the target operating system.
Examples: ["ls", "-la", "/home/user"], ["echo", "\\"Hello", "World\\""].
Avoid commands that require interactive input or run indefinitely.
""")
                    @Nullable
                    List<String> command,
            @P(
                            """
Optional working directory where the command should be executed.
If not provided, the command will be executed in the project root directory.
Must be an absolute path to an existing directory.
""")
                    String workingDirectory) {

        log.info(
                "Tool called to execute command: '{}' in directory: '{}'",
                command,
                workingDirectory);

        File workDir = prepareWorkingDirectory(workingDirectory);

        if (!showConfirmationDialog(command, workDir.getAbsolutePath())) {
            return "Command execution cancelled by user.";
        }

        return executeCommandInTerminal(command, workDir);
    }

    private @NotNull File prepareWorkingDirectory(String workingDirectory) {
        File workDir;

        if (workingDirectory == null || workingDirectory.trim().isEmpty()) {
            // Use project base directory as default
            String projectPath = project.getBasePath();
            if (projectPath == null) {
                throw new ToolException("Unable to determine project base directory");
            }
            workDir = new File(projectPath);
        } else {
            workDir = new File(workingDirectory.trim());
        }

        if (!workDir.exists()) {
            throw new IllegalArgumentException(
                    "Working directory does not exist: " + workDir.getAbsolutePath());
        }

        if (!workDir.isDirectory()) {
            throw new IllegalArgumentException(
                    "Working directory path is not a directory: " + workDir.getAbsolutePath());
        }

        return workDir;
    }

    private boolean showConfirmationDialog(List<String> command, String workingDirectory) {
        CompletableFuture<Boolean> dialogResult = new CompletableFuture<>();

        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            try {
                                CommandConfirmationDialog dialog =
                                        new CommandConfirmationDialog(
                                                project,
                                                String.join(" ", command),
                                                workingDirectory);
                                dialogResult.complete(dialog.showAndGet());
                            } catch (Exception e) {
                                log.error("Error showing confirmation dialog", e);
                                dialogResult.complete(false);
                            }
                        });

        try {
            return dialogResult.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error waiting for user confirmation", e);
            return false;
        }
    }

    private String executeCommandInTerminal(@Nullable List<String> command, File workingDirectory) {
        log.debug(
                "Executing command in terminal: '{}' in directory: '{}'",
                command,
                workingDirectory.getAbsolutePath());

        CompletableFuture<String> executionResult = new CompletableFuture<>();

        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            ToolWindowManager toolWindowManager =
                                    ToolWindowManager.getInstance(project);
                            ToolWindow terminalToolWindow =
                                    toolWindowManager.getToolWindow(TERMINAL_TOOL_WINDOW_ID);

                            if (terminalToolWindow != null) {
                                terminalToolWindow.show();
                            }

                            TerminalToolWindowManager terminalManager =
                                    TerminalToolWindowManager.getInstance(project);

                            TerminalTabState tabState = new TerminalTabState();
                            tabState.myWorkingDirectory = workingDirectory.getAbsolutePath();
                            tabState.myShellCommand = command;
                            tabState.myTabName = InCoderBundle.message("name");

                            terminalManager.createNewSession(
                                    new LocalTerminalDirectRunner(project), tabState);

                            String result = "Command executed in terminal: %s".formatted(command);

                            log.info(result);
                            executionResult.complete(result);
                        });

        try {
            return executionResult.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error waiting for terminal command execution", e);
            return "Error: Failed to execute command in terminal - " + e.getMessage();
        }
    }

    private static class CommandConfirmationDialog extends DialogWrapper {
        private final String command;
        private final String workingDirectory;

        protected CommandConfirmationDialog(
                Project project, String command, String workingDirectory) {
            super(project);
            this.command = command;
            this.workingDirectory = workingDirectory;
            setTitle("AI Assistant - Command Execution Request");
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
            commandScrollPane.setBorder(
                    JBUI.Borders.customLine(JBUI.CurrentTheme.Editor.BORDER_COLOR));

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
                    .addLabeledComponent("AI Assistant wants to execute:", commandScrollPane)
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
                            JBUI.Borders.customLine(
                                    JBUI.CurrentTheme.NotificationInfo.borderColor()),
                            JBUI.Borders.empty(8)));

            JBLabel infoIcon = new JBLabel(AllIcons.General.Information);
            infoIcon.setBorder(JBUI.Borders.emptyRight(12));

            JBLabel infoText =
                    new JBLabel(
                            "<html><b>What happens next:</b><br/>• The command will be executed in"
                                + " a new IntelliJ terminal tab<br/>• You can monitor progress and"
                                + " interact with the command in real-time<br/>• The terminal will"
                                + " remain open for further use</html>");
            infoText.setForeground(JBUI.CurrentTheme.NotificationInfo.foregroundColor());

            infoPanel.add(infoIcon, BorderLayout.WEST);
            infoPanel.add(infoText, BorderLayout.CENTER);

            return infoPanel;
        }
    }
}

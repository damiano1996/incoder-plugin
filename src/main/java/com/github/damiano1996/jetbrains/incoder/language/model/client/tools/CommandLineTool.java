package com.github.damiano1996.jetbrains.incoder.language.model.client.tools;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
@AllArgsConstructor
public class CommandLineTool {

    private final Project project;

    private static final int COMMAND_TIMEOUT_SECONDS = 30;

    @Tool(
            name = "COMMAND_LINE_TOOL",
            value =
                    """
Executes a shell command on the user's system after obtaining user approval through a confirmation dialog.
The command will be adapted to the user's operating system (Windows uses cmd.exe, Unix-like systems use bash).
This tool provides a safe way to run system commands with user oversight and includes timeout protection.
""")
    public String executeCommand(
            @P(
                            """
The shell command to execute. Should be a valid command for the target operating system.
Examples: 'ls -la', 'git status', 'npm install', 'mvn clean compile'.
Avoid commands that require interactive input or run indefinitely.
""")
                    String command,
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

        String[] osCommand = prepareOSCommand(command);

        if (!showConfirmationDialog(command, workDir.getAbsolutePath())) {
            return "Command execution cancelled by user.";
        }

        return executeCommandInternal(osCommand, workDir);
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

    private String[] prepareOSCommand(String command) {
        String osName = System.getProperty("os.name").toLowerCase();
        log.debug("Detected OS: {}", osName);

        if (osName.contains("win")) {
            // Windows
            return new String[] {"cmd.exe", "/c", command};
        } else {
            // Unix-like systems (Linux, macOS, etc.)
            return new String[] {"/bin/bash", "-c", command};
        }
    }

    private boolean showConfirmationDialog(String command, String workingDirectory) {
        CompletableFuture<Boolean> dialogResult = new CompletableFuture<>();

        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            try {
                                CommandConfirmationDialog dialog =
                                        new CommandConfirmationDialog(
                                                project, command, workingDirectory);
                                dialogResult.complete(dialog.showAndGet());
                            } catch (Exception e) {
                                log.error("Error showing confirmation dialog", e);
                                dialogResult.complete(false);
                            }
                        });

        try {
            return dialogResult.get(30, TimeUnit.SECONDS); // 30 second timeout for dialog
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Error waiting for user confirmation", e);
            return false;
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
                            "<html><b>Security Warning:</b> The AI assistant is requesting to"
                                    + " execute a system command.<br/>Please review the command"
                                    + " carefully before proceeding. Only execute commands you"
                                    + " trust.</html>");
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
                            "<html><b>What happens next:</b><br/>• The command will be executed"
                                + " with a 30-second timeout<br/>• Both output and errors will be"
                                + " captured and shown to the AI Assistant</html>");
            infoText.setForeground(JBUI.CurrentTheme.NotificationInfo.foregroundColor());

            infoPanel.add(infoIcon, BorderLayout.WEST);
            infoPanel.add(infoText, BorderLayout.CENTER);

            return infoPanel;
        }
    }

    private String executeCommandInternal(String[] command, File workingDirectory) {
        log.debug(
                "Executing command: {} in directory: {}",
                String.join(" ", command),
                workingDirectory.getAbsolutePath());

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(workingDirectory);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            List<String> outputLines = new ArrayList<>();
            try (BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    outputLines.add(line);
                    log.debug("Command output: {}", line);
                }
            }

            boolean finished = process.waitFor(COMMAND_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                throw new ToolException(
                        "Command execution timed out after "
                                + COMMAND_TIMEOUT_SECONDS
                                + " seconds");
            }

            int exitCode = process.exitValue();
            String output = String.join("\n", outputLines);

            String result =
                    String.format(
                            "Command executed successfully.\n"
                                    + "Exit code: %d\n"
                                    + "Working directory: %s\n"
                                    + "Output:\n%s",
                            exitCode,
                            workingDirectory.getAbsolutePath(),
                            output.isEmpty() ? "(no output)" : output);

            log.info("Command execution completed with exit code: {}", exitCode);
            return result;

        } catch (IOException e) {
            throw new ToolException(
                    "Failed to execute command: "
                            + String.join(" ", command)
                            + ". Error: "
                            + e.getMessage(),
                    e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ToolException(
                    "Command execution was interrupted: " + String.join(" ", command), e);
        }
    }
}

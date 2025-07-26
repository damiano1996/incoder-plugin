package com.github.damiano1996.jetbrains.incoder.language.model.client.tools.commandline;

import com.github.damiano1996.jetbrains.incoder.InCoderBundle;
import com.github.damiano1996.jetbrains.incoder.language.model.client.tools.ToolException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfoRt;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.jediterm.terminal.TtyConnector;
import com.pty4j.PtyProcess;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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

    private static final int COMMAND_TIMEOUT_SECONDS = 30;
    private static final String TERMINAL_TOOL_WINDOW_ID = "Terminal";

    private static final int MAX_OUTPUT_LINES = 100;
    private static final int HEAD_LINES = 50;
    private static final int TAIL_LINES = 50;

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

        CompletableFuture<Boolean> terminalSessionCreationResult = new CompletableFuture<>();
        CompletableFuture<StringBuilder> commandExecutionOutputResult = new CompletableFuture<>();

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
                            tabState.myShellCommand = buildShellCommand(command);
                            tabState.myTabName = InCoderBundle.message("name");
                            tabState.myIsUserDefinedTabTitle = false;

                            try {

                                var runner =
                                        new LocalTerminalDirectRunner(project) {
                                            @Override
                                            public @NotNull TtyConnector createTtyConnector(
                                                    @NotNull PtyProcess process) {
                                                TtyConnector originalConnector =
                                                        super.createTtyConnector(process);
                                                return new OutputCapturingTtyConnector(
                                                        originalConnector,
                                                        commandExecutionOutputResult);
                                            }
                                        };

                                terminalManager.createNewSession(runner, tabState);
                                log.info("Terminal session created");
                                terminalSessionCreationResult.complete(true);

                            } catch (Exception e) {
                                log.warn("Unable to run new terminal session.", e);
                                terminalSessionCreationResult.complete(false);
                            }
                        });

        try {

            boolean isTerminalSessionCreated = terminalSessionCreationResult.get();

            if (!isTerminalSessionCreated)
                throw new ToolException("Unable to run the command: %s".formatted(command));

            StringBuilder commandExecutionOutputStringBuilder =
                    commandExecutionOutputResult.get(COMMAND_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            String result =
                    """
                    Command executed successfully.
                    Output:
                    %s
                    """
                            .formatted(commandExecutionOutputStringBuilder.toString());

            result = truncateOutput(result);
            log.info(result);

            return result;

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new ToolException(
                    "Unable to get the result of the command execution. Error: " + e.getMessage(),
                    e);
        }
    }

    /**
     * Wraps the user command so the terminal stays open after execution. Returns null if command is
     * empty -> means "just open a shell".
     */
    private static @Nullable List<String> buildShellCommand(@Nullable List<String> cmd) {
        if (cmd == null || cmd.isEmpty()) return null;

        String joined = String.join(" ", cmd);

        if (SystemInfoRt.isWindows) {
            return List.of("cmd", "/k", joined);
        } else {
            String shell = System.getenv().getOrDefault("SHELL", "/bin/bash");
            return List.of(shell, "-lc", joined + "; exec " + shell + " -l");
        }
    }

    private String truncateOutput(String output) {
        String[] lines = output.split("\n");

        if (lines.length <= MAX_OUTPUT_LINES) {
            return output;
        }

        StringBuilder result = new StringBuilder();

        // Add first N lines
        for (int i = 0; i < HEAD_LINES; i++) {
            result.append(lines[i]).append("\n");
        }

        // Add truncation indicator
        result.append("\n... [OUTPUT TRUNCATED - ")
                .append(lines.length - HEAD_LINES - TAIL_LINES)
                .append(" lines omitted] ...\n\n");

        // Add last N lines
        for (int i = lines.length - TAIL_LINES; i < lines.length; i++) {
            result.append(lines[i]).append("\n");
        }

        return result.toString();
    }
}

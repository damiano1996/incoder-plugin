package com.github.damiano1996.jetbrains.incoder.language.model.client.tools.commandline;

import com.github.damiano1996.jetbrains.incoder.InCoderBundle;
import com.github.damiano1996.jetbrains.incoder.language.model.client.tools.ToolException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.jediterm.terminal.TtyConnector;
import com.pty4j.PtyProcess;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
            return "Command canceled by the user. "
                    + "Ask why to understand their motivation before proceeding.";
        }

        return executeCommandInTerminal(command, workDir);
    }

    private @NotNull File prepareWorkingDirectory(String workingDirectory) {
        File workDir;

        if (workingDirectory == null || workingDirectory.trim().isEmpty()) {
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
                            tabState.myShellCommand = command;
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

            StringBuilder commandExecutionOutputStringBuilder = commandExecutionOutputResult.get();

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

        } catch (InterruptedException | ExecutionException e) {
            throw new ToolException(
                    "Unable to get the result of the command execution. Error: " + e.getMessage(),
                    e);
        }
    }

    public static String truncateOutput(String output) {
        return truncateOutput(output, MAX_OUTPUT_LINES, HEAD_LINES, TAIL_LINES);
    }

    public static String truncateOutput(
            String output, int maxOutputLines, int headLines, int tailLines) {

        String[] lines = output.split("\n");

        if (lines.length <= maxOutputLines) {
            return output;
        }

        StringBuilder result = new StringBuilder();

        // Add first N lines
        for (int i = 0; i < headLines; i++) {
            result.append(lines[i]).append("\n");
        }

        // Add truncation indicator
        result.append("\n... [OUTPUT TRUNCATED - ")
                .append(lines.length - headLines - tailLines)
                .append(" lines omitted] ...\n\n");

        // Add last N lines
        for (int i = lines.length - tailLines; i < lines.length; i++) {
            result.append(lines[i]).append("\n");
        }

        return result.toString();
    }
}

package com.github.damiano1996.jetbrains.incoder.language.model.client.tools.commandline;

import com.github.damiano1996.jetbrains.incoder.InCoderBundle;
import com.github.damiano1996.jetbrains.incoder.formatting.OutputFormatter;
import com.github.damiano1996.jetbrains.incoder.language.model.client.tools.ToolException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.terminal.TerminalToolWindowManager;

/**
 * Implementation of CommandExecutor that executes commands using ProcessBuilder and optionally
 * displays them in the IntelliJ terminal for user visibility.
 */
@Slf4j
public class TerminalCommandExecutor implements CommandExecutor {

    private static final String TERMINAL_TOOL_WINDOW_ID = "Terminal";

    @Override
    @NotNull
    public String execute(
            @NotNull Project project,
            @NotNull List<String> command,
            @NotNull File workingDirectory) {

        log.debug(
                "Executing command: '{}' in directory: '{}'",
                command,
                workingDirectory.getAbsolutePath());

        // Show terminal window for user visibility
        showInTerminal(project, command, workingDirectory);

        // Execute command using ProcessBuilder to capture output
        return executeAndCaptureOutput(command, workingDirectory);
    }

    private void showInTerminal(
            @NotNull Project project,
            @NotNull List<String> command,
            @NotNull File workingDirectory) {

        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            try {
                                ToolWindowManager toolWindowManager =
                                        ToolWindowManager.getInstance(project);
                                ToolWindow terminalToolWindow =
                                        toolWindowManager.getToolWindow(TERMINAL_TOOL_WINDOW_ID);

                                if (terminalToolWindow != null) {
                                    terminalToolWindow.show();
                                }

                                TerminalToolWindowManager terminalManager =
                                        TerminalToolWindowManager.getInstance(project);

                                // Create a new terminal session to show command execution
                                var shellWidget =
                                        terminalManager.createShellWidget(
                                                workingDirectory.getAbsolutePath(),
                                                InCoderBundle.message("name"),
                                                true,
                                                true);

                                // Send command to terminal for visibility
                                String commandString = String.join(" ", command);
                                shellWidget.sendCommandToExecute(commandString);

                                log.info("Command sent to terminal: {}", commandString);

                            } catch (Exception e) {
                                log.warn("Unable to display command in terminal.", e);
                            }
                        });
    }

    @NotNull
    private String executeAndCaptureOutput(
            @NotNull List<String> command, @NotNull File workingDirectory) {

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(workingDirectory);
            processBuilder.redirectErrorStream(true); // Merge stderr with stdout

            Process process = processBuilder.start();

            // Capture output
            StringBuilder outputBuilder = new StringBuilder();
            try (BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    outputBuilder.append(line).append("\n");
                }
            }

            // Wait for command to complete
            int exitCode = process.waitFor();

            String output = outputBuilder.toString().trim();
            String truncatedOutput = OutputFormatter.truncateOutput(output);

            String result =
                    """
                    Command executed successfully.
                    Working Directory: %s
                    Command: %s
                    Exit Code: %d

                    Output:
                    %s
                    """
                            .formatted(
                                    workingDirectory.getAbsolutePath(),
                                    String.join(" ", command),
                                    exitCode,
                                    truncatedOutput.isEmpty() ? "(no output)" : truncatedOutput);

            log.info("Command execution completed with exit code: {}", exitCode);

            return result;

        } catch (Exception e) {
            String errorMsg = "Failed to execute command: " + e.getMessage();
            log.error(errorMsg, e);
            throw new ToolException(errorMsg, e);
        }
    }
}

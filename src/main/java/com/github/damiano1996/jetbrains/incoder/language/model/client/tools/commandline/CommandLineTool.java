package com.github.damiano1996.jetbrains.incoder.language.model.client.tools.commandline;

import com.github.damiano1996.jetbrains.incoder.language.model.client.tools.ToolException;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Agent tool for executing shell commands with user confirmation. Delegates actual command
 * execution to CommandExecutor implementation.
 */
@Slf4j
public class CommandLineTool {

    private final CommandExecutor commandExecutor;

    public CommandLineTool() {
        this.commandExecutor = new TerminalCommandExecutor();
    }

    public CommandLineTool(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    @Tool(
            name = "local-command_line-execute_command",
            value =
                    """
Executes a shell command in the IntelliJ integrated terminal after obtaining user approval through a confirmation dialog.
The command will be executed in a new terminal tab, allowing real-time monitoring of the execution.
This tool provides a safe way to run system commands with user oversight and visual feedback.
Returns the command output and exit code.
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

        if (command == null || command.isEmpty()) {
            return "No command provided";
        }

        Project project = ProjectUtil.getActiveProject();
        File workDir = prepareWorkingDirectory(project, workingDirectory);

        if (!showConfirmationDialog(project, command, workDir.getAbsolutePath())) {
            return "Command canceled by the user. "
                    + "Ask why to understand their motivation before proceeding.";
        }

        return commandExecutor.execute(project, command, workDir);
    }

    private @NotNull File prepareWorkingDirectory(Project project, String workingDirectory) {
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

    private boolean showConfirmationDialog(
            Project project, List<String> command, String workingDirectory) {
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
}

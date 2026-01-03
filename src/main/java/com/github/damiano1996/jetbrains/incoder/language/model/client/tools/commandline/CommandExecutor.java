package com.github.damiano1996.jetbrains.incoder.language.model.client.tools.commandline;

import com.intellij.openapi.project.Project;
import java.io.File;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/** Interface for executing shell commands with output capture. */
public interface CommandExecutor {

    /**
     * Executes a command and returns the output.
     *
     * @param project the IntelliJ project context
     * @param command the command to execute as a list of arguments
     * @param workingDirectory the directory where the command should be executed
     * @return the execution result including output and exit code
     */
    @NotNull
    String execute(
            @NotNull Project project,
            @NotNull List<String> command,
            @NotNull File workingDirectory);
}

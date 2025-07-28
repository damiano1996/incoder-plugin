package com.github.damiano1996.jetbrains.incoder.tool.window.chat;

import com.intellij.openapi.project.Project;
import dev.langchain4j.service.tool.ToolExecution;
import java.util.function.Consumer;

/**
 * Service interface for handling chat operations. Provides methods for processing user prompts and
 * managing chat streams.
 */
public interface ChatService {

    /**
     * Processes a user prompt and initiates a chat stream.
     *
     * @param project the current project
     * @param chatId the chat identifier
     * @param prompt the user's prompt text
     * @param onStart callback executed when the stream starts
     * @param onNewToken callback executed when a new token is received during streaming
     * @param onToolExecuted callback executed when a tool execution is completed
     * @param onComplete callback executed when the stream completes successfully
     * @param onError callback executed when an error occurs
     */
    void processPrompt(
            Project project,
            int chatId,
            String prompt,
            Runnable onStart,
            Consumer<String> onNewToken,
            Consumer<ToolExecution> onToolExecuted,
            Runnable onComplete,
            Runnable onError);

    /**
     * Checks if the language model service is ready for processing.
     *
     * @param project the current project
     * @return true if the service is ready, false otherwise
     */
    boolean isLanguageModelReady(Project project);
}

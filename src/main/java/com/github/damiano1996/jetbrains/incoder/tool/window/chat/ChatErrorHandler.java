package com.github.damiano1996.jetbrains.incoder.tool.window.chat;

import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.github.damiano1996.jetbrains.incoder.notification.NotificationService;
import com.intellij.openapi.project.Project;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ChatErrorHandler {

    public void handleError(@NotNull Project project, Throwable throwable) {
        if (throwable instanceof StopStreamException) {
            log.info("Stream stop requested");
            return;
        }

        String errorMessage = buildErrorMessage(throwable);
        NotificationService.getInstance(project).notifyError(errorMessage);
        log.error("Chat error occurred", throwable);
    }

    private String buildErrorMessage(Throwable throwable) {
        if (isJsonEOFException(throwable)) {
            return ChatConstants.JSON_EOF_ERROR_TEMPLATE.formatted(throwable.getMessage());
        }

        if (isMismatchedInputException(throwable)) {
            return ChatConstants.TOOL_INVOCATION_ERROR_TEMPLATE.formatted(throwable.getMessage());
        }

        return ChatConstants.UNEXPECTED_ERROR_TEMPLATE.formatted(throwable.getMessage());
    }

    private boolean isJsonEOFException(Throwable throwable) {
        return throwable instanceof JsonEOFException
                || throwable.getCause() instanceof JsonEOFException;
    }

    private boolean isMismatchedInputException(Throwable throwable) {
        return throwable instanceof MismatchedInputException
                || throwable.getCause() instanceof MismatchedInputException;
    }
}

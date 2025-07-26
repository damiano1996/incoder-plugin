package com.github.damiano1996.jetbrains.incoder.tool.window.chat;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelServiceImpl;
import com.intellij.openapi.project.Project;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatState chatState;
    private final ChatErrorHandler errorHandler;

    public ChatServiceImpl(ChatState chatState) {
        this.chatState = chatState;
        this.errorHandler = new ChatErrorHandler();
    }

    @Override
    public void processPrompt(
            Project project,
            int chatId,
            String prompt,
            Runnable onStart,
            Consumer<String> onNewToken,
            Runnable onToolExecuted,
            Runnable onComplete,
            Runnable onError) {

        chatState.setState(ChatStateEnum.GENERATING);
        onStart.run();

        try {
            log.debug("Starting chat stream for chatId: {}", chatId);

            LanguageModelServiceImpl.getInstance(project)
                    .getClient()
                    .chat(chatId, prompt)
                    .onPartialResponse(
                            token -> {
                                onNewToken.accept(token);
                                chatState.checkStopRequest();
                            })
                    .onToolExecuted(
                            toolExecution -> {
                                onToolExecuted.run();
                                chatState.checkStopRequest();
                            })
                    .onCompleteResponse(
                            chatResponse -> {
                                log.debug("Chat stream completed successfully");
                                chatState.reset();
                                onComplete.run();
                            })
                    .onError(
                            throwable -> {
                                log.error("Error in chat stream", throwable);
                                chatState.handleError();
                                errorHandler.handleError(project, throwable);
                                onError.run();
                            })
                    .start();

        } catch (Exception e) {
            log.error("Error starting chat stream", e);
            chatState.handleError();
            errorHandler.handleError(project, e);
            onError.run();
        }
    }

    @Override
    public boolean isLanguageModelReady(Project project) {
        return LanguageModelServiceImpl.getInstance(project).isReady();
    }
}

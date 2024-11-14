package com.github.damiano1996.intellijplugin.incoder.tool.window.chat;

import com.github.damiano1996.intellijplugin.incoder.generation.CodeGenerationService;
import com.github.damiano1996.intellijplugin.incoder.llm.LlmService;
import com.github.damiano1996.intellijplugin.incoder.tool.window.ChatMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.ChatBody;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class Chat {
    private JTextField prompt;
    private ChatBody chatBody;

    @Getter private JPanel mainPanel;
    private JProgressBar generatingAnswer;

    public Chat setActionListeners(Project project) {
        prompt.addActionListener(e -> handleAction(project));
        return this;
    }

    private void handleAction(Project project) {
        String prompt = this.prompt.getText();
        handlePrompt(project, prompt);
    }

    private void handlePrompt(Project project, @NotNull String prompt) {
        if (prompt.isEmpty()) {
            log.debug("Prompt is empty.");
        } else {
            log.debug("Prompt: {}", prompt);
            this.prompt.setText("");

            chatBody.addMessage(new ChatMessage(ChatMessage.Author.USER, prompt));
            updateGenerationProgressBar(true);

            LlmService.getInstance(project)
                    .classify(prompt)
                    .thenApply(
                            promptType -> {
                                switch (promptType) {
                                    case EDIT -> {
                                        return LlmService.getInstance(project)
                                                .edit(
                                                        Objects.requireNonNull(
                                                                FileEditorManager.getInstance(
                                                                                project)
                                                                        .getSelectedTextEditor()),
                                                        prompt)
                                                .thenAccept(
                                                        answer -> {
                                                            chatBody.addMessage(
                                                                    new ChatMessage(
                                                                            ChatMessage.Author.AI,
                                                                            answer.comments()));
                                                            updateGenerationProgressBar(false);

                                                            ApplicationManager.getApplication()
                                                                    .invokeLater(
                                                                            () ->
                                                                                    CodeGenerationService
                                                                                            .showDiff(
                                                                                                    project,
                                                                                                    answer
                                                                                                            .code(),
                                                                                                    Objects
                                                                                                            .requireNonNull(
                                                                                                                    FileEditorManager
                                                                                                                            .getInstance(
                                                                                                                                    project)
                                                                                                                            .getSelectedTextEditor())));
                                                        });
                                    }
                                    case CODE_QUESTION -> {
                                        var message =
                                                chatBody.addMessage(
                                                        new ChatMessage(ChatMessage.Author.AI, ""));

                                        LlmService.getInstance(project)
                                                .answer(
                                                        Objects.requireNonNull(
                                                                FileEditorManager.getInstance(
                                                                                project)
                                                                        .getSelectedTextEditor()),
                                                        prompt)
                                                .onNext(
                                                        new Consumer<>() {
                                                            private String answer = "";

                                                            @Override
                                                            public void accept(String token) {
                                                                log.debug("New token: {}", token);

                                                                answer += token;
                                                                message.setMessage(answer);
                                                            }
                                                        })
                                                .onComplete(
                                                        aiMessageResponse -> {
                                                            log.debug("Stream completed.");
                                                            updateGenerationProgressBar(false);
                                                        })
                                                .onError(
                                                        throwable -> {
                                                            log.warn(
                                                                    "Error during stream",
                                                                    throwable);
                                                            updateGenerationProgressBar(false);
                                                        })
                                                .start();

                                        //
                                        // .thenAccept(codeRagResponse -> {
                                        //
                                        // chatBody.addMessage(new
                                        // ChatMessage(ChatMessage.Author.AI,
                                        // codeRagResponse.response()));
                                        //
                                        // updateGenerationProgressBar(false);
                                        //                                        });
                                        return null;
                                    }
                                    default -> {
                                        return LlmService.getInstance(project)
                                                .chat(prompt)
                                                .thenAccept(
                                                        answer -> {
                                                            chatBody.addMessage(
                                                                    new ChatMessage(
                                                                            ChatMessage.Author.AI,
                                                                            answer));
                                                            updateGenerationProgressBar(false);
                                                        });
                                    }
                                }
                            });
        }
    }

    private void updateGenerationProgressBar(boolean generating) {
        generatingAnswer.setIndeterminate(generating);
        generatingAnswer.setVisible(generating);
    }

    private void createUIComponents() {
        prompt = new PlaceholderTextField("Enter a prompt...");
        generatingAnswer = new JProgressBar();
        generatingAnswer.setVisible(false);
    }
}

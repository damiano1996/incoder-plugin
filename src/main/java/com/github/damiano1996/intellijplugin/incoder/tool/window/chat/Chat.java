package com.github.damiano1996.intellijplugin.incoder.tool.window.chat;

import com.github.damiano1996.intellijplugin.incoder.generation.CodeGenerationService;
import com.github.damiano1996.intellijplugin.incoder.llm.LlmService;
import com.github.damiano1996.intellijplugin.incoder.tool.window.ChatMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.ChatBody;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.HumanMessage;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.output.Response;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class Chat {

    @Getter private JPanel mainPanel;
    private JTextField prompt;
    private JProgressBar generating;
    private ChatBody chatBody;

    public Chat setActionListeners(Project project) {
        prompt.addActionListener(e -> handleAction(project));
        return this;
    }

    private void handleAction(Project project) {
        //        chatBody.addMessage(new ChatMessage(ChatMessage.Author.USER, "Hi!"));
        //        chatBody.addMessage(new ChatMessage(ChatMessage.Author.AI, "Hi!"));
        //        chatBody.addMessage(new ChatMessage(ChatMessage.Author.USER, "Hi! Hi! Hi! Hi! Hi!
        // Hi! Hi! Hi! Hi! Hi! Hi! Hi! Hi! Hi! Hi! Hi! Hi! Hi! Hi!"));
        //        chatBody.addMessage(new ChatMessage(ChatMessage.Author.AI, "Hello World! Hello
        // World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World!
        // Hello World! Hello World! Hello World! Hello World!"));

        String prompt = this.prompt.getText();
        handlePrompt(project, prompt);
    }

    private void handlePrompt(Project project, @NotNull String prompt) {
        if (prompt.isEmpty()) {
            log.debug("Prompt is empty.");
        } else {
            log.debug("Prompt: {}", prompt);
            this.prompt.setText("");

            HumanMessage userMessageComponent =
                    (HumanMessage)
                            chatBody.addMessage(new ChatMessage(ChatMessage.Author.USER, prompt));
            isGenerating(true);

            LlmService.getInstance(project)
                    .classify(prompt)
                    .thenApply(
                            promptType -> {
                                userMessageComponent.setPromptTypeLabel(promptType);
                                return promptType;
                            })
                    .thenAccept(
                            promptType -> {
                                switch (promptType) {
                                    case EDIT -> {
                                        LlmService.getInstance(project)
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
                                                            isGenerating(false);

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
                                        var tokenConsumer =
                                                new TokenConsumer(ChatMessage.Author.AI, chatBody);

                                        LlmService.getInstance(project)
                                                .answer(
                                                        Objects.requireNonNull(
                                                                FileEditorManager.getInstance(
                                                                                project)
                                                                        .getSelectedTextEditor()),
                                                        prompt)
                                                .onNext(tokenConsumer)
                                                .onComplete(onTokenStreamComplete())
                                                .onError(onTokenStreamError())
                                                .start();
                                    }
                                    default -> {
                                        var tokenConsumer =
                                                new TokenConsumer(ChatMessage.Author.AI, chatBody);

                                        LlmService.getInstance(project)
                                                .chat(prompt)
                                                .onNext(tokenConsumer)
                                                .onComplete(onTokenStreamComplete())
                                                .onError(onTokenStreamError())
                                                .start();
                                    }
                                }
                            });
        }
    }

    private @NotNull Consumer<Throwable> onTokenStreamError() {
        return throwable -> {
            log.warn("Error during stream", throwable);
            isGenerating(false);
        };
    }

    private @NotNull Consumer<Response<AiMessage>> onTokenStreamComplete() {
        return aiMessageResponse -> {
            log.debug("Stream completed.");
            isGenerating(false);
        };
    }

    private void isGenerating(boolean generating) {
        this.prompt.setEnabled(!generating);
        this.generating.setIndeterminate(generating);
        this.generating.setVisible(generating);
    }

    private void createUIComponents() {
        mainPanel = new JPanel();
        mainPanel.setBackground(JBColor.namedColor("ToolWindow.background"));

        prompt = new PlaceholderTextField("Enter the prompt...");
        generating = new JProgressBar();
        isGenerating(false);
    }
}

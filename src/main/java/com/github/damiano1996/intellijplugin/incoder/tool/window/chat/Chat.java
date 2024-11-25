package com.github.damiano1996.intellijplugin.incoder.tool.window.chat;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelService;
import com.github.damiano1996.intellijplugin.incoder.tool.window.ChatMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.ChatBody;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.human.HumanMessage;
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

            LanguageModelService.getInstance(project)
                    .classify(prompt)
                    .thenApply(
                            promptType -> {
                                log.debug("Prompt classified as: {}", promptType);
                                userMessageComponent.setPromptTypeLabel(promptType);
                                return promptType;
                            })
                    .thenAccept(
                            promptType -> {
                                switch (promptType) {
                                    case EDIT -> {
                                        var tokenConsumer =
                                                new TokenConsumer(
                                                        project, ChatMessage.Author.AI, chatBody);

                                        LanguageModelService.getInstance(project)
                                                .edit(
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
                                    case CODE_QUESTION -> {
                                        var tokenConsumer =
                                                new TokenConsumer(
                                                        project, ChatMessage.Author.AI, chatBody);

                                        LanguageModelService.getInstance(project)
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
                                                new TokenConsumer(
                                                        project, ChatMessage.Author.AI, chatBody);

                                        LanguageModelService.getInstance(project)
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
        log.debug("Is generating...");
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

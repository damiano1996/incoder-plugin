package com.github.damiano1996.intellijplugin.incoder.tool.window.chat;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelService;
import com.github.damiano1996.intellijplugin.incoder.notification.NotificationService;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.ChatBody;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.AiMessageComponent;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.human.HumanMessageComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.TokenStream;
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

            HumanMessageComponent humanMessageComponent = new HumanMessageComponent(prompt);
            chatBody.addMessage(humanMessageComponent);

            isGenerating(true);

            log.debug("Classifying prompt");
            LanguageModelService.getInstance(project)
                    .classify(prompt)
                    .thenApply(
                            promptType -> {
                                log.debug("Prompt classified as: {}", promptType);
                                humanMessageComponent.setPromptTypeLabel(promptType);
                                return promptType;
                            })
                    .thenAccept(
                            promptType -> {
                                var aiMessage = new AiMessageComponent(project);
                                chatBody.addMessage(aiMessage);
                                Editor editor =
                                        FileEditorManager.getInstance(project)
                                                .getSelectedTextEditor();

                                getChatTokenStreamer(project, prompt, editor)
                                        .onNext(
                                                token -> {
                                                    aiMessage.write(token);
                                                    chatBody.updateUI();
                                                })
                                        .onComplete(onTokenStreamComplete())
                                        .onError(onTokenStreamError())
                                        .start();
                            })
                    .exceptionally(
                            throwable -> {
                                log.debug("Error while classifying the prompt.", throwable);
                                isGenerating(false);
                                NotificationService.getInstance(project)
                                        .notifyError("Error: %s".formatted(throwable.getMessage()));
                                return null;
                            });
        }
    }

    private static TokenStream getChatTokenStreamer(
            Project project, @NotNull String prompt, Editor editor) {
        return editor == null
                ? LanguageModelService.getInstance(project).chat(prompt)
                : LanguageModelService.getInstance(project).chat(editor, prompt);
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

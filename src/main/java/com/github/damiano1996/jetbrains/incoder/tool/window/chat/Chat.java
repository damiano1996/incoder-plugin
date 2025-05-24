package com.github.damiano1996.jetbrains.incoder.tool.window.chat;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelService;
import com.github.damiano1996.jetbrains.incoder.notification.NotificationService;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.ChatBody;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.AiMessageComponent;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.human.HumanMessageComponent;
import com.github.damiano1996.jetbrains.incoder.ui.components.PlaceholderTextField;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import dev.langchain4j.service.TokenStream;
import java.util.function.Consumer;
import javax.swing.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class Chat {

    @Setter @Getter private int chatId;

    @Getter private JPanel mainPanel;
    private JTextField prompt;
    private JProgressBar generating;

    private ChatBody chatBody;

    public Chat setPromptActionListener(Project project) {
        prompt.addActionListener(e -> handleAction(project));
        return this;
    }

    private void handleAction(Project project) {
        String prompt = this.prompt.getText();
        handlePrompt(project, prompt);
    }

    private void handlePrompt(Project project, @NotNull String prompt) {
        if (!LanguageModelService.getInstance(project).isReady()) {
            NotificationService.getInstance(project)
                    .notifyWithSettingsActionButton(
                            "The Language Model Service is not ready. "
                                    + "Please, configure the Chat and the Server from Settings.",
                            NotificationType.WARNING);
            return;
        }

        if (prompt.isEmpty()) {
            log.debug("Prompt is empty.");
            return;
        }

        log.debug("Prompt: {}", prompt);
        this.prompt.setText("");

        HumanMessageComponent humanMessageComponent = new HumanMessageComponent(prompt);
        chatBody.addMessage(humanMessageComponent);

        updateProgressStatus(true);

        log.debug("Classifying prompt");
        LanguageModelService.getInstance(project)
                .classify(prompt)
                .thenAccept(
                        promptType -> {
                            log.debug("Prompt classified as: {}", promptType);
                            humanMessageComponent.setPromptTypeLabel(promptType);

                            var aiMessage = new AiMessageComponent(project);
                            aiMessage.setModelName(
                                    LanguageModelService.getInstance(project)
                                            .getSelectedModelName()
                                            .toLowerCase());
                            chatBody.addMessage(aiMessage);

                            Editor editor =
                                    FileEditorManager.getInstance(project).getSelectedTextEditor();

                            getChatTokenStreamer(project, prompt, editor)
                                    .onPartialResponse(
                                            token -> {
                                                aiMessage.write(token);
                                                chatBody.updateUI();
                                            })
                                    .onCompleteResponse(
                                            chatResponse -> onTokenStreamComplete(aiMessage))
                                    .onError(onTokenStreamError())
                                    .start();
                        })
                .exceptionally(
                        throwable -> {
                            log.debug("Error while classifying the prompt.", throwable);
                            updateProgressStatus(false);
                            NotificationService.getInstance(project)
                                    .notifyError("Error: %s".formatted(throwable.getMessage()));
                            return null;
                        });
    }

    private TokenStream getChatTokenStreamer(
            Project project, @NotNull String prompt, Editor editor) {
        return editor == null
                ? LanguageModelService.getInstance(project).chat(chatId, prompt)
                : LanguageModelService.getInstance(project).chat(chatId, editor, prompt);
    }

    private @NotNull Consumer<Throwable> onTokenStreamError() {
        return throwable -> {
            log.warn("Error during stream", throwable);
            updateProgressStatus(false);
        };
    }

    private void onTokenStreamComplete(@NotNull AiMessageComponent aiMessage) {
        log.debug("Stream completed.");
        aiMessage.streamClosed();
        updateProgressStatus(false);
    }

    private void updateProgressStatus(boolean isGenerating) {
        log.debug("Is generating...");
        this.prompt.setEnabled(!isGenerating);
        if (!isGenerating) this.prompt.requestFocusInWindow();
        this.generating.setIndeterminate(isGenerating);
        this.generating.setVisible(isGenerating);
    }

    private void createUIComponents() {
        mainPanel = new JPanel();
        mainPanel.setBackground(JBColor.namedColor("ToolWindow.background"));

        prompt = new PlaceholderTextField("Enter a prompt...", 12, 8);
        generating = new JProgressBar();
        updateProgressStatus(false);
    }
}

package com.github.damiano1996.jetbrains.incoder.tool.window.chat;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelServiceImpl;
import com.github.damiano1996.jetbrains.incoder.notification.NotificationService;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.ChatBody;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.AiMessageComponent;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.human.HumanMessageComponent;
import com.github.damiano1996.jetbrains.incoder.ui.components.PlaceholderTextField;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import java.util.function.Consumer;
import javax.swing.*;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.tool.ToolExecution;
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

    public void setPromptActionListener(Project project) {
        prompt.addActionListener(e -> handleAction(project));
    }

    private void handleAction(Project project) {
        String prompt = this.prompt.getText();
        handlePrompt(project, prompt);
    }

    private void handlePrompt(Project project, @NotNull String prompt) {
        if (!LanguageModelServiceImpl.getInstance(project).isReady()) {
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

        var aiMessage = new AiMessageComponent(project);
        aiMessage.setModelName(
                LanguageModelServiceImpl.getInstance(project)
                        .getSelectedModelName()
                        .toLowerCase()
        );
        chatBody.addMessage(aiMessage);

        try {
            LanguageModelServiceImpl.getInstance(project)
                    .getClient()
                    .chat(chatId, prompt)
                    .onPartialResponse(
                            token -> {
                                aiMessage.write(token);
                                chatBody.updateUI();
                            })
                    .onToolExecuted(toolExecution -> {
                        aiMessage.write("\n\n");
                        chatBody.updateUI();
                    })
                    .onCompleteResponse(chatResponse -> onTokenStreamComplete(aiMessage, chatResponse))
                    .onError(onTokenStreamError(project))
                    .start();
        } catch (Exception e) {
            log.warn("Error while starting token stream", e);
            NotificationService.getInstance(project).notifyError(e.getMessage());
            updateProgressStatus(false);
        }
    }

    private @NotNull Consumer<Throwable> onTokenStreamError(Project project) {
        return throwable -> {
            log.warn("Error during stream.", throwable);
            NotificationService.getInstance(project).notifyError(throwable.getMessage());
            updateProgressStatus(false);
        };
    }

    private void onTokenStreamComplete(@NotNull AiMessageComponent aiMessage, @NotNull ChatResponse chatResponse) {
        log.debug("Stream completed.");
        log.debug("Full response message:\n{}", chatResponse.aiMessage().text());
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

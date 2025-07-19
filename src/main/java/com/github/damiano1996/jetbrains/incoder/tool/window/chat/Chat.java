package com.github.damiano1996.jetbrains.incoder.tool.window.chat;

import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelServiceImpl;
import com.github.damiano1996.jetbrains.incoder.notification.NotificationService;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.ChatBody;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.AiMessageComponent;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.human.HumanMessageComponent;
import com.github.damiano1996.jetbrains.incoder.ui.components.expandabletextarea.ExpandableTextArea;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import dev.langchain4j.model.chat.response.ChatResponse;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class Chat {

    @Setter @Getter private int chatId;

    @Getter private JPanel mainPanel;
    private ExpandableTextArea prompt;
    private JProgressBar generating;
    private JScrollPane promptScrollPane;

    private ChatBody chatBody;

    public void setPromptActionListener(Project project) {
        prompt.addActionListener(e -> handleAction(project));
    }

    private void handleAction(Project project) {
        String prompt = this.prompt.getText().trim();
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
                LanguageModelServiceImpl.getInstance(project).getSelectedModelName().toLowerCase());
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
                    .onToolExecuted(
                            toolExecution -> {
                                aiMessage.write("\n\n");
                                chatBody.updateUI();
                            })
                    .onCompleteResponse(
                            chatResponse -> onTokenStreamComplete(aiMessage, chatResponse))
                    .onError(
                            throwable -> {
                                log.error("Error during chat streaming.", throwable);
                                handleError(project, throwable);
                            })
                    .start();
        } catch (Exception e) {
            log.error("Error during chat stream start.", e);
            handleError(project, e);
        }
    }

    private void handleError(Project project, Throwable throwable) {
        String errorMessage;
        if (throwable instanceof JsonEOFException
                || throwable.getCause() instanceof JsonEOFException) {
            errorMessage =
                    """
                    <html>Response parsing failed. This could be due to:<br>
                    &nbsp;&nbsp;- Insufficient max tokens<br>
                    &nbsp;&nbsp;- Incomplete model response<br>
                    Additional details: %s<br>
                    Please adjust model settings and start a new chat.</html>
                    """
                            .formatted(throwable.getMessage());
        } else if (throwable instanceof MismatchedInputException
                || throwable.getCause() instanceof MismatchedInputException) {
            errorMessage =
                    """
                    <html>Tool invocation error occurred.<br>
                    &nbsp;&nbsp;- Model failed to call tool correctly<br>
                    &nbsp;&nbsp;- Incorrect tool function or parameter format<br>
                    Additional details: %s<br>
                    Please try again or start a new chat.</html>
                    """
                            .formatted(throwable.getMessage());
        } else {
            errorMessage =
                    """
                    <html>An unexpected error occurred during response generation.<br>
                    Additional details: %s<br>
                    Please check your network connection and model settings.</html>
                    """
                            .formatted(throwable.getMessage());
        }

        NotificationService.getInstance(project).notifyError(errorMessage);
        updateProgressStatus(false);
    }

    private void onTokenStreamComplete(
            @NotNull AiMessageComponent aiMessage, @NotNull ChatResponse chatResponse) {
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

        prompt = new ExpandableTextArea("Enter a prompt...", 12, 12, 1);
        generating = new JProgressBar();

        promptScrollPane =
                new JScrollPane() {
                    @Override
                    public boolean isWheelScrollingEnabled() {
                        return false;
                    }

                    @Override
                    public Dimension getMinimumSize() {
                        return getPreferredSize();
                    }

                    @Override
                    public Dimension getPreferredSize() {
                        Dimension size = super.getPreferredSize();
                        if (getViewport() != null && getViewport().getView() != null) {
                            size.height = getViewport().getView().getPreferredSize().height;
                        }
                        return size;
                    }
                };
        promptScrollPane.setBorder(BorderFactory.createEmptyBorder());
        promptScrollPane.setOpaque(false);
        promptScrollPane.getViewport().setOpaque(false);
        promptScrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        promptScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        updateProgressStatus(false);
    }
}

package com.github.damiano1996.jetbrains.incoder.tool.window.chat;

import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelServiceImpl;
import com.github.damiano1996.jetbrains.incoder.notification.NotificationService;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.ChatBody;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.AiMessageComponent;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.human.HumanMessageComponent;
import com.github.damiano1996.jetbrains.incoder.ui.components.FocusAwarePanel;
import com.github.damiano1996.jetbrains.incoder.ui.components.expandabletextarea.ExpandableTextArea;
import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
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
    private JScrollPane promptScrollPane;
    private JButton submitButton;

    private boolean isGenerating;
    private boolean stopRequested;

    private ChatBody chatBody;
    private JPanel inputPanel;

    public void setPromptActionListener(Project project) {
        prompt.addActionListener(e -> handleAction(project));
        submitButton.addActionListener(e -> handleButtonAction(project));
    }

    private void handleButtonAction(Project project) {
        if (isGenerating) {
            stopRequested = true;
        } else {
            handleAction(project);
        }
    }

    private void handleAction(Project project) {
        if (!LanguageModelServiceImpl.getInstance(project).isReady()) {
            NotificationService.getInstance(project)
                    .notifyWithSettingsActionButton(
                            "The Language Model Service is not ready. "
                                    + "Please, configure the Chat and the Server from Settings.",
                            NotificationType.WARNING);
            return;
        }

        String prompt = this.prompt.getText().trim();

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

        startChatStream(project, prompt, aiMessage);
    }

    private void startChatStream(
            Project project, @NotNull String prompt, AiMessageComponent aiMessage) {
        try {
            log.debug("Starting chat stream...");

            LanguageModelServiceImpl.getInstance(project)
                    .getClient()
                    .chat(chatId, prompt)
                    .onPartialResponse(
                            token -> {
                                aiMessage.write(token);
                                chatBody.updateUI();

                                checkStreamStopRequest();
                            })
                    .onToolExecuted(
                            toolExecution -> {
                                aiMessage.write("\n\n");
                                chatBody.updateUI();

                                checkStreamStopRequest();
                            })
                    .onCompleteResponse(chatResponse -> onTokenStreamComplete(aiMessage))
                    .onError(
                            throwable -> {
                                if (throwable instanceof StopStreamException) {
                                    log.info("Unable to stop stream...");
                                } else {
                                    handleError(project, throwable);
                                }
                            })
                    .start();

        } catch (Exception e) {
            log.error("Error during chat stream execution.", e);
            handleError(project, e);
        }
    }

    private void checkStreamStopRequest() {
        if (stopRequested) {
            throw new StopStreamException();
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
                    <br>
                    Additional details:<br>
                    %s
                    </html>
                    """
                            .formatted(throwable.getMessage());
        } else if (throwable instanceof MismatchedInputException
                || throwable.getCause() instanceof MismatchedInputException) {
            errorMessage =
                    """
                    <html>Tool invocation error occurred.<br>
                    &nbsp;&nbsp;- Model failed to call tool correctly<br>
                    &nbsp;&nbsp;- Incorrect tool function or parameter format<br>
                    <br>
                    Additional details:<br>
                    %s
                    </html>
                    """
                            .formatted(throwable.getMessage());
        } else {
            errorMessage =
                    """
                    <html>An unexpected error occurred during response generation.<br>
                    <br>
                    Additional details:<br>
                    %s
                    </html>
                    """
                            .formatted(throwable.getMessage());
        }

        NotificationService.getInstance(project).notifyError(errorMessage);
        updateProgressStatus(false);
    }

    private void onTokenStreamComplete(@NotNull AiMessageComponent aiMessage) {
        log.debug("Stream completed.");
        stopRequested = false;
        aiMessage.streamClosed();
        updateProgressStatus(false);
    }

    private synchronized void updateProgressStatus(boolean isGenerating) {
        log.debug("Is generating...");
        this.prompt.setEnabled(!isGenerating);
        if (!isGenerating) this.prompt.requestFocusInWindow();

        log.debug("Updating progress bar status");
        this.isGenerating = isGenerating;
        if (inputPanel != null) log.debug("Updating submit button status");

        if (isGenerating) {
            this.submitButton.setIcon(AllIcons.Actions.Suspend);
            this.submitButton.setToolTipText("Stop feature coming soon");
            this.submitButton.setEnabled(false);
        } else {
            this.submitButton.setIcon(AllIcons.Actions.Execute);
            this.submitButton.setToolTipText("Send message");
            this.submitButton.setEnabled(true);
        }
    }

    private void createUIComponents() {
        mainPanel = new JPanel();
        mainPanel.setBackground(JBColor.namedColor("ToolWindow.background"));

        prompt = new ExpandableTextArea("Enter a prompt...", 12, 12, 1);

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
        promptScrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        submitButton = new JButton();
        submitButton.setBorderPainted(false);
        submitButton.setContentAreaFilled(false);
        submitButton.setFocusPainted(false);
        submitButton.setOpaque(false);

        inputPanel = new FocusAwarePanel();
        ((FocusAwarePanel) inputPanel).addFocusTrackingFor(prompt);

        updateProgressStatus(false);
    }
}

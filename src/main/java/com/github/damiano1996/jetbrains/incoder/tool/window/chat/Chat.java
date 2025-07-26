package com.github.damiano1996.jetbrains.incoder.tool.window.chat;

import static com.github.damiano1996.jetbrains.incoder.tool.window.chat.ChatConstants.*;

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

@Slf4j
public class Chat {

    @Setter @Getter private int chatId;

    @Getter private JPanel mainPanel;
    private ExpandableTextArea prompt;
    private JScrollPane promptScrollPane;
    private JButton submitButton;
    private ChatBody chatBody;
    private JPanel inputPanel;

    private ChatState chatState;
    private ChatService chatService;

    public void setPromptActionListener(Project project) {
        prompt.addActionListener(e -> handleAction(project));
        submitButton.addActionListener(e -> handleButtonAction(project));
    }

    private void handleButtonAction(Project project) {
        if (chatState.isGenerating()) {
            chatState.requestStop();
            updateProgressStatus();
        } else {
            handleAction(project);
        }
    }

    private void handleAction(Project project) {
        if (!chatService.isLanguageModelReady(project)) {
            NotificationService.getInstance(project)
                    .notifyWithSettingsActionButton(
                            LANGUAGE_MODEL_NOT_READY, NotificationType.WARNING);
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

        var aiMessage = new AiMessageComponent(project);
        aiMessage.setModelName(
                LanguageModelServiceImpl.getInstance(project).getSelectedModelName().toLowerCase());
        chatBody.addMessage(aiMessage);

        chatService.processPrompt(
                project,
                chatId,
                prompt,
                this::updateProgressStatus,
                token -> {
                    aiMessage.write(token);
                    chatBody.updateUI();
                },
                () -> {
                    aiMessage.write("\n\n");
                    chatBody.updateUI();
                },
                () -> {
                    aiMessage.streamClosed();
                    updateProgressStatus();
                },
                this::updateProgressStatus);
    }

    private synchronized void updateProgressStatus() {
        log.debug("Is generating...");
        this.prompt.setEnabled(!chatState.isGenerating());
        if (!chatState.isGenerating()) this.prompt.requestFocusInWindow();

        if (chatState.isGenerating()) {
            this.submitButton.setIcon(AllIcons.Actions.Suspend);
            this.submitButton.setToolTipText(STOP_TOOLTIP);
            this.submitButton.setEnabled(false);
        } else {
            this.submitButton.setIcon(AllIcons.Actions.Execute);
            this.submitButton.setToolTipText(SEND_MESSAGE_TOOLTIP);
            this.submitButton.setEnabled(true);
        }
    }

    private void createUIComponents() {
        mainPanel = new JPanel();
        mainPanel.setBackground(JBColor.namedColor("ToolWindow.background"));

        prompt = new ExpandableTextArea(PROMPT_PLACEHOLDER, 12, 12, 1);

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

        chatState = new ChatState();
        chatService = new ChatServiceImpl(chatState);

        updateProgressStatus();
    }
}

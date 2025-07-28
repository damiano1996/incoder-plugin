package com.github.damiano1996.jetbrains.incoder.tool.window.chat;

import static com.github.damiano1996.jetbrains.incoder.tool.window.chat.ChatConstants.*;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelServiceImpl;
import com.github.damiano1996.jetbrains.incoder.notification.NotificationService;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.ChatBody;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.AiMessageComponent;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.human.HumanMessageComponent;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.tool.ToolMessageComponent;
import com.github.damiano1996.jetbrains.incoder.ui.components.FocusAwarePanel;
import com.github.damiano1996.jetbrains.incoder.ui.components.Layout;
import com.github.damiano1996.jetbrains.incoder.ui.components.expandabletextarea.ExpandableTextArea;
import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.RoundedLineBorder;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class Chat {

    private final Project project;

    @Setter @Getter private int chatId;

    @Getter private JPanel mainPanel;
    private ExpandableTextArea promptTextArea;
    private JButton submitButton;
    private ChatBody chatBody;

    private ChatState chatState;
    private ChatService chatService;

    private AiMessageComponent currentAiMessage;

    public Chat(Project project) {
        this.project = project;

        createUIComponents();
        initActionListeners();
    }

    private void initActionListeners() {
        promptTextArea.addActionListener(e -> handleAction());
        submitButton.addActionListener(e -> handleButtonAction());
    }

    private void handleButtonAction() {
        if (chatState.isGenerating()) {
            chatState.requestStop();
            updateProgressStatus();
        } else {
            handleAction();
        }
    }

    private void handleAction() {
        if (!chatService.isLanguageModelReady(project)) {
            NotificationService.getInstance(project)
                    .notifyWithSettingsActionButton(
                            LANGUAGE_MODEL_NOT_READY, NotificationType.WARNING);
            return;
        }

        String prompt = this.promptTextArea.getText().trim();

        if (prompt.isEmpty()) {
            log.debug("Prompt is empty.");
            return;
        }

        log.debug("Prompt: {}", prompt);
        this.promptTextArea.setText("");

        HumanMessageComponent humanMessageComponent = new HumanMessageComponent(prompt);
        chatBody.addMessage(humanMessageComponent);

        currentAiMessage =
                new AiMessageComponent(
                        project,
                        LanguageModelServiceImpl.getInstance(project)
                                .getSelectedModelName()
                                .toLowerCase());
        chatBody.addMessage(currentAiMessage);

        chatService.processPrompt(
                project,
                chatId,
                prompt,
                this::updateProgressStatus,
                token -> {
                    currentAiMessage.write(token);
                    chatBody.updateUI();
                },
                toolExecution -> {
                    currentAiMessage.streamClosed();

                    chatBody.addMessage(new ToolMessageComponent(toolExecution));

                    currentAiMessage = new AiMessageComponent(project);
                    chatBody.addMessage(currentAiMessage);

                    chatBody.updateUI();
                },
                () -> {
                    currentAiMessage.streamClosed();
                    updateProgressStatus();
                },
                this::updateProgressStatus);
    }

    private synchronized void updateProgressStatus() {
        log.debug("Is generating...");
        this.promptTextArea.setEnabled(!chatState.isGenerating());
        if (!chatState.isGenerating()) this.promptTextArea.requestFocusInWindow();

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
        chatState = new ChatState();
        chatService = new ChatServiceImpl(chatState);

        promptTextArea = new ExpandableTextArea(PROMPT_PLACEHOLDER, 12, 12, 1);
        promptTextArea.setMargin(JBUI.insets(2, 9, 2, 6));

        JScrollPane promptScrollPane = getPromptScrollPane();

        submitButton = new JButton();
        submitButton.setBorderPainted(false);
        submitButton.setContentAreaFilled(false);
        submitButton.setFocusPainted(false);
        submitButton.setOpaque(false);
        submitButton.setHorizontalAlignment(SwingConstants.RIGHT);
        submitButton.setToolTipText(SEND_MESSAGE_TOOLTIP);

        Dimension buttonSize = new Dimension(40, 40);
        submitButton.setMinimumSize(buttonSize);
        submitButton.setPreferredSize(buttonSize);
        submitButton.setMaximumSize(buttonSize);

        chatBody = new ChatBody();

        Border focusBorder =
                new RoundedLineBorder(
                        JBUI.CurrentTheme.Focus.focusColor(), ARC_DIAMETER, THICKNESS);
        Border unfocusBorder =
                new RoundedLineBorder(
                        JBUI.CurrentTheme.Label.disabledForeground(), ARC_DIAMETER, THICKNESS);
        Border padding = JBUI.Borders.empty(PADDING);

        FocusAwarePanel inputPanel =
                new FocusAwarePanel(
                        new CompoundBorder(focusBorder, padding),
                        new CompoundBorder(unfocusBorder, padding));
        inputPanel.addFocusTrackingFor(promptTextArea);

        JPanel inputContent =
                FormBuilder.createFormBuilder()
                        .addComponent(promptScrollPane)
                        .addVerticalGap(4)
                        .addComponent(Layout.componentToRight(submitButton))
                        .getPanel();

        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(inputContent, BorderLayout.CENTER);

        mainPanel =
                FormBuilder.createFormBuilder()
                        .addComponentFillVertically(chatBody.getMainPanel(), 0)
                        .addVerticalGap(4)
                        .addComponent(inputPanel)
                        .getPanel();

        mainPanel.setBorder(JBUI.Borders.empty(20));
        mainPanel.setMinimumSize(new Dimension(150, 400));
        mainPanel.setPreferredSize(new Dimension(150, 400));

        updateProgressStatus();
    }

    private @NotNull JScrollPane getPromptScrollPane() {
        JScrollPane promptScrollPane =
                new JBScrollPane() {
                    private final int MIN_HEIGHT = 50;
                    private final int MAX_HEIGHT = 200;
                    private final int PREFERRED_WIDTH = 100;

                    @Override
                    public Dimension getPreferredSize() {
                        Component view = getViewport().getView();
                        int viewHeight = view != null ? view.getPreferredSize().height : MIN_HEIGHT;
                        int height = Math.max(MIN_HEIGHT, Math.min(viewHeight, MAX_HEIGHT));
                        return new Dimension(PREFERRED_WIDTH, height);
                    }

                    @Override
                    public Dimension getMinimumSize() {
                        return new Dimension(PREFERRED_WIDTH, MIN_HEIGHT);
                    }

                    @Override
                    public Dimension getMaximumSize() {
                        Dimension viewPreferredSize = getViewport().getView().getPreferredSize();
                        int maxHeight = Math.min(viewPreferredSize.height, MAX_HEIGHT);
                        return new Dimension(PREFERRED_WIDTH, maxHeight);
                    }
                };

        promptScrollPane.setBorder(BorderFactory.createEmptyBorder());
        promptScrollPane.setOpaque(false);
        promptScrollPane.getViewport().setOpaque(false);
        promptScrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        promptScrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        promptScrollPane.setViewportView(promptTextArea);

        return promptScrollPane;
    }
}

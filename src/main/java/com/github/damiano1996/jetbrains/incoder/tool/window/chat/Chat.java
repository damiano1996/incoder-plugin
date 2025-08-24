package com.github.damiano1996.jetbrains.incoder.tool.window.chat;

import static com.github.damiano1996.jetbrains.incoder.tool.window.chat.ChatConstants.*;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelProjectService;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.ChatLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings.ChatSettings;
import com.github.damiano1996.jetbrains.incoder.language.model.client.tokenstream.StoppableTokenStream;
import com.github.damiano1996.jetbrains.incoder.language.model.client.tokenstream.StoppableTokenStreamImpl;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import com.github.damiano1996.jetbrains.incoder.language.model.server.settings.LanguageModelParametersUtils;
import com.github.damiano1996.jetbrains.incoder.language.model.server.settings.ServerSettings;
import com.github.damiano1996.jetbrains.incoder.notification.NotificationService;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.ChatBody;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.AiChatMessage;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.error.ErrorChatMessage;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.human.HumanChatMessage;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.MarkdownChatMessage;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.tool.ToolChatMessage;
import com.github.damiano1996.jetbrains.incoder.ui.components.FocusAwarePanel;
import com.github.damiano1996.jetbrains.incoder.ui.components.expandabletextarea.ExpandableTextArea;
import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.RoundedLineBorder;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.IconUtil;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import dev.langchain4j.service.tool.ToolExecution;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class Chat {

    private final Project project;
    @Nullable private ChatLanguageModelClient client;

    @Setter @Getter private int chatId;

    @Getter private JPanel mainPanel;
    private ExpandableTextArea promptTextArea;
    private JButton submitButton;
    private ChatBody chatBody;

    private boolean isStreaming = false;

    @Nullable private StoppableTokenStream stoppableTokenStream;

    public Chat(Project project) {
        this.project = project;
        createUIComponents();
    }

    private void handleButtonAction() {
        if (stoppableTokenStream != null && isStreaming) {
            stoppableTokenStream.stop();
            uiStopping();
        } else {
            handleAction();
        }
    }

    private void handleAction() {
        String prompt = this.promptTextArea.getText().trim();

        if (prompt.isEmpty()) {
            log.debug("Prompt is empty.");
            return;
        }

        if (client == null) {
            NotificationService.getInstance(project)
                    .notifyWithSettingsActionButton(
                            "No language model selected. Please, add one from Settings and select"
                                    + " it to start chatting.",
                            NotificationType.WARNING);
            return;
        }

        log.debug("Prompt: {}", prompt);
        this.promptTextArea.setText("");

        HumanChatMessage humanChatMessage = new HumanChatMessage(prompt);
        chatBody.addChatMessage(humanChatMessage);

        chatBody.addChatMessage(new AiChatMessage(client.getParameters().modelName.toLowerCase()));

        chatBody.addChatMessage(new MarkdownChatMessage(chatBody));

        stoppableTokenStream =
                (StoppableTokenStream)
                        new StoppableTokenStreamImpl(client.chat(chatId, prompt))
                                .onStart(this::onStart)
                                .onStop(this::onStop)
                                .onPartialResponse(this::onNewToken)
                                .onCompleteResponse(chatResponse -> onComplete())
                                .onToolExecuted(this::onToolExecuted)
                                .onError(this::onError);

        stoppableTokenStream.start();
    }

    private void onStop() {
        isStreaming = false;
        uiIdle();
    }

    private void onStart() {
        isStreaming = true;
        uiGenerating();
    }

    private void onNewToken(String token) {
        chatBody.write(token);
    }

    private void onToolExecuted(ToolExecution toolExecution) {
        log.debug("Tool executed");
        chatBody.closeStream();
        log.debug("Adding new tool message component");
        chatBody.addChatMessage(new ToolChatMessage(toolExecution));
        log.debug("Resuming with markdown");
        chatBody.addChatMessage(new MarkdownChatMessage(chatBody));
    }

    private void onComplete() {
        isStreaming = false;
        chatBody.closeStream();
        uiIdle();
    }

    private void onError(Throwable throwable) {
        chatBody.closeStream();
        chatBody.addChatMessage(new ErrorChatMessage(throwable));
        uiIdle();
    }

    private void handleExamplePromptSelected(@NotNull ActionEvent e) {
        String selectedPrompt = e.getActionCommand();
        log.debug("Example prompt selected: {}", selectedPrompt);
        promptTextArea.setText(selectedPrompt);
        promptTextArea.requestFocusInWindow();
        promptTextArea.setCaretPosition(selectedPrompt.length());
    }

    private void uiGenerating() {
        this.promptTextArea.setEnabled(false);

        updateSubmitButton(getColoredIcon(AllIcons.Actions.Suspend), STOP_TOOLTIP, true);
    }

    private void uiStopping() {
        this.promptTextArea.setEnabled(false);

        updateSubmitButton(getColoredIcon(AllIcons.Actions.Suspend), STOP_TOOLTIP, false);
    }

    private void uiIdle() {
        this.promptTextArea.setEnabled(true);
        this.promptTextArea.requestFocusInWindow();

        updateSubmitButton(getColoredIcon(AllIcons.Actions.Execute), SEND_MESSAGE_TOOLTIP, true);
    }

    private static @NotNull Icon getColoredIcon(@NotNull Icon icon) {
        return IconUtil.colorize(icon, JBUI.CurrentTheme.Focus.focusColor());
    }

    private void updateSubmitButton(@NotNull Icon execute, String sendMessageTooltip, boolean b) {
        this.submitButton.setIcon(execute);
        this.submitButton.setToolTipText(sendMessageTooltip);
        this.submitButton.setEnabled(b);
    }

    private void createUIComponents() {
        JPanel inputPanel = getInputPanel();

        chatBody = new ChatBody(this::handleExamplePromptSelected);

        mainPanel =
                FormBuilder.createFormBuilder()
                        .addComponentFillVertically(chatBody.getMainPanel(), 0)
                        .addVerticalGap(4)
                        .addComponent(inputPanel)
                        .getPanel();

        mainPanel.setBorder(JBUI.Borders.empty(20));
        mainPanel.setMinimumSize(new Dimension(400, 400));
        mainPanel.setPreferredSize(new Dimension(400, 400));

        uiIdle();
    }

    private @NotNull JPanel getInputPanel() {
        promptTextArea = new ExpandableTextArea(PROMPT_PLACEHOLDER, 12, 12, 2);
        promptTextArea.setMargin(JBUI.insets(2, 9, 2, 6));
        promptTextArea.setLineWrap(true);
        promptTextArea.addActionListener(e -> handleAction());

        JScrollPane promptScrollPane = getPromptScrollPane();

        submitButton = createToolBarButton(SEND_MESSAGE_TOOLTIP);
        submitButton.addActionListener(e -> handleButtonAction());

        ComboBox<LanguageModelParameters> languageModelParametersComboBox =
                getLanguageModelParametersComboBox();

        if (ServerSettings.getInstance().getState().configuredLanguageModels.isEmpty()) {
            NotificationService.getInstance(project)
                    .notifyWithSettingsActionButton(
                            "No language model selected. Please, add one from Settings and select"
                                    + " it to start chatting.",
                            NotificationType.INFORMATION);
        } else {
            LanguageModelParametersUtils.refreshComboBoxModels(
                    languageModelParametersComboBox,
                    ChatSettings.getInstance().getState().getDefaultLanguageModelParameters());
        }

        JButton refreshModelsButton = createToolBarButton("Refresh models");
        refreshModelsButton.setIcon(getColoredIcon(AllIcons.Actions.Refresh));
        refreshModelsButton.addActionListener(
                e ->
                        LanguageModelParametersUtils.refreshComboBoxModels(
                                languageModelParametersComboBox,
                                ChatSettings.getInstance()
                                        .getState()
                                        .getDefaultLanguageModelParameters()));

        JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
        toolbar.setFloatable(false);
        toolbar.setBorderPainted(false);
        toolbar.setOpaque(false);
        toolbar.add(languageModelParametersComboBox);
        toolbar.add(refreshModelsButton);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(submitButton);

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
                        .addComponent(toolbar)
                        .getPanel();

        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(inputContent, BorderLayout.CENTER);
        return inputPanel;
    }

    private @NotNull ComboBox<LanguageModelParameters> getLanguageModelParametersComboBox() {
        ComboBox<LanguageModelParameters> serverNamesComboBox =
                LanguageModelParametersUtils.getLanguageModelParametersComboBox();

        serverNamesComboBox.addItemListener(
                e -> {
                    if (e.getStateChange() == ItemEvent.DESELECTED) return;
                    try {
                        LanguageModelParameters selectedParameters =
                                (LanguageModelParameters) e.getItem();
                        client =
                                LanguageModelProjectService.getInstance(project)
                                        .createChatClientWithDefaultSettings(selectedParameters)
                                        .compute();
                        ChatSettings.getInstance()
                                .getState()
                                .setDefaultLanguageModelParameters(selectedParameters);
                    } catch (LanguageModelException ex) {
                        notifySettingsError(e);
                    }
                });
        return serverNamesComboBox;
    }

    private @NotNull JButton createToolBarButton(String tooltipMessage) {
        JButton button = new JButton();
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setToolTipText(tooltipMessage);

        Dimension buttonSize = new Dimension(40, 40);
        button.setMinimumSize(buttonSize);
        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize);

        return button;
    }

    private void notifySettingsError(@NotNull ItemEvent e) {
        NotificationService.getInstance(project)
                .notifyWithSettingsActionButton(
                        """
                        <html>
                        Unable to start the service with <b>%s</b>.<br>
                        Please, review server configurations from Settings.
                        </html>
                        """
                                .formatted(e.getItem().toString()),
                        NotificationType.ERROR);
    }

    private @NotNull JScrollPane getPromptScrollPane() {
        JScrollPane promptScrollPane =
                new JBScrollPane() {
                    private final int MIN_HEIGHT = 100;
                    private final int MAX_HEIGHT = 200;
                    private final int PREFERRED_WIDTH = 100;

                    @Override
                    public @NotNull Dimension getPreferredSize() {
                        Component view = getViewport().getView();
                        int viewHeight = view != null ? view.getPreferredSize().height : MIN_HEIGHT;
                        int height = Math.max(MIN_HEIGHT, Math.min(viewHeight, MAX_HEIGHT));
                        return new Dimension(PREFERRED_WIDTH, height);
                    }

                    @Contract(value = " -> new", pure = true)
                    @Override
                    public @NotNull Dimension getMinimumSize() {
                        return new Dimension(PREFERRED_WIDTH, MIN_HEIGHT);
                    }

                    @Override
                    public @NotNull Dimension getMaximumSize() {
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

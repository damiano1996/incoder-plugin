package com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings;

import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactoryUtils;
import com.github.damiano1996.jetbrains.incoder.ui.components.DescriptionLabel;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ChatSettingsComponent {

    private final JPanel mainPanel;
    private final ComboBox<String> serverNamesComboBox;
    private final JSpinner maxMessages;
    private final JBTextArea systemMessageInstructionsField;
    private final JButton reloadDefaultButton;
    private final JBCheckBox enableTools;

    public ChatSettingsComponent() {

        List<String> serverNames = new ArrayList<>(ServerFactoryUtils.getServerNames());
        serverNames.add("");
        serverNamesComboBox =
                new ComboBox<>(serverNames.toArray(new String[0]));

        SpinnerNumberModel maxMessagesModel = new SpinnerNumberModel(10, 0, 50, 1);
        maxMessages = new JSpinner(maxMessagesModel);

        systemMessageInstructionsField = new JBTextArea(5, 20);
        systemMessageInstructionsField.setLineWrap(true);
        systemMessageInstructionsField.setWrapStyleWord(true);

        reloadDefaultButton = new JButton(AllIcons.Actions.Refresh);
        reloadDefaultButton.setToolTipText("Reload default system message");
        reloadDefaultButton.setBorderPainted(false);
        reloadDefaultButton.setContentAreaFilled(false);
        reloadDefaultButton.setFocusPainted(false);
        reloadDefaultButton.addActionListener(
                e ->
                        systemMessageInstructionsField.setText(
                                ChatSettings.State.loadDefaultSystemPrompt()));

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
        labelPanel.add(new JBLabel("System message instructions:"));
        labelPanel.add(Box.createHorizontalGlue());
        labelPanel.add(reloadDefaultButton);

        enableTools = new JBCheckBox("LLM with tools");

        mainPanel =
                FormBuilder.createFormBuilder()
                        .setFormLeftIndent(20)
                        .addLabeledComponent(
                                new JBLabel("Server:"), serverNamesComboBox, 0, false)
                        .addComponent(new DescriptionLabel("Server used in the chat."))
                        .addVerticalGap(20)
                        .addLabeledComponent(new JBLabel("Max messages:"), maxMessages, 0, false)
                        .addComponent(new DescriptionLabel("Number of messages to keep in memory."))
                        .addVerticalGap(20)
                        .setFormLeftIndent(0)
                        .addComponent(labelPanel)
                        .addComponent(
                                ScrollPaneFactory.createScrollPane(systemMessageInstructionsField))
                        .addComponent(
                                new DescriptionLabel(
                                        "Custom system prompt that defines the AI assistant's"
                                                + " behavior, role, and response style for all chat"
                                                + " interactions."))
                        .addVerticalGap(20)
                        .addComponent(enableTools)
                        .addComponent(
                                new DescriptionLabel(
                                        "When enabled, allows the AI to use predefined tools and"
                                                + " functions to enhance code generation and"
                                                + " problem-solving capabilities."))
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();
    }
}

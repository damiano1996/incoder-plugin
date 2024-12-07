package com.github.damiano1996.intellijplugin.incoder.language.model.client.chat.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.ServerFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.ServerFactoryUtils;
import com.github.damiano1996.intellijplugin.incoder.ui.components.DescriptionLabel;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;
import lombok.Getter;

@Getter
public class ChatSettingsComponent {

    private final JPanel mainPanel;
    private final ComboBox<String> serverTypeComboBox;
    private final JSpinner maxMessages;
    private final JBTextArea systemMessageInstructionsWithCodeField;
    private final JBTextArea systemMessageInstructionsField;

    public ChatSettingsComponent() {

        serverTypeComboBox =
                new ComboBox<>(
                        ServerFactoryUtils.getServerFactories().stream()
                                .map(ServerFactory::getName)
                                .toList()
                                .toArray(new String[0]));

        SpinnerNumberModel maxMessagesModel = new SpinnerNumberModel(10, 0, 50, 1);
        maxMessages = new JSpinner(maxMessagesModel);

        systemMessageInstructionsWithCodeField = new JBTextArea(5, 20);
        systemMessageInstructionsWithCodeField.setLineWrap(true);
        systemMessageInstructionsWithCodeField.setWrapStyleWord(true);

        systemMessageInstructionsField = new JBTextArea(5, 20);
        systemMessageInstructionsField.setLineWrap(true);
        systemMessageInstructionsField.setWrapStyleWord(true);

        mainPanel =
                FormBuilder.createFormBuilder()
                        .setFormLeftIndent(20)
                        .addLabeledComponent(
                                new JBLabel("Server type:"), serverTypeComboBox, 1, false)
                        .addComponent(
                                new DescriptionLabel(
                                        "Select the server to be used for interaction with language"
                                                + " models."))
                        .addVerticalGap(20)
                        .addLabeledComponent(new JBLabel("Max messages:"), maxMessages, 1, false)
                        .addComponent(new DescriptionLabel("Number of messages to keep in memory."))
                        .addVerticalGap(20)
                        .addLabeledComponent(
                                new JBLabel("System message instructions with code:"),
                                ScrollPaneFactory.createScrollPane(
                                        systemMessageInstructionsWithCodeField),
                                1,
                                true)
                        .addComponent(
                                new DescriptionLabel(
                                        "System message template instructions when code context is"
                                            + " included. These instructions will be added to the"
                                            + " @SystemMessage."))
                        .addVerticalGap(20)
                        .addLabeledComponent(
                                new JBLabel("System message instructions:"),
                                ScrollPaneFactory.createScrollPane(systemMessageInstructionsField),
                                1,
                                true)
                        .addComponent(
                                new DescriptionLabel(
                                        "System message template instructions for general"
                                            + " questions. These instructions will be added to the"
                                            + " @SystemMessage."))
                        .setFormLeftIndent(0)
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();
    }
}

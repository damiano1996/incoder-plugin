package com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelService;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import java.util.Objects;
import javax.swing.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ChatSettingsConfigurable implements Configurable {

    private ChatSettingsComponent chatSettingsComponent;

    public ChatSettingsConfigurable() {
        chatSettingsComponent = new ChatSettingsComponent();
    }

    private static ChatSettings.@NotNull State getState() {
        return ChatSettings.getInstance().getState();
    }

    @Contract(pure = true)
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getDisplayName() {
        return "Chat";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return chatSettingsComponent.getMainPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return chatSettingsComponent.getMainPanel();
    }

    @Override
    public boolean isModified() {
        var state = getState();

        return !chatSettingsComponent.getServerTypeComboBox().getItem().equals(state.serverName)
                || !chatSettingsComponent.getMaxMessages().getValue().equals(state.maxMessages)
                || !chatSettingsComponent
                        .getSystemMessageInstructionsWithCodeField()
                        .getText()
                        .equals(state.systemMessageInstructionsWithCode)
                || !chatSettingsComponent
                        .getSystemMessageInstructionsField()
                        .getText()
                        .equals(state.systemMessageInstructions);
    }

    @Override
    public void apply() throws ConfigurationException {
        var state = getState();

        state.serverName = chatSettingsComponent.getServerTypeComboBox().getItem();
        state.maxMessages = (int) chatSettingsComponent.getMaxMessages().getValue();
        state.systemMessageInstructionsWithCode =
                chatSettingsComponent.getSystemMessageInstructionsWithCodeField().getText();
        state.systemMessageInstructions =
                chatSettingsComponent.getSystemMessageInstructionsField().getText();

        try {
            LanguageModelService.getInstance(Objects.requireNonNull(ProjectUtil.getActiveProject()))
                    .init();
        } catch (LanguageModelException e) {
            throw new ConfigurationException(
                    e.getMessage(), "Unable to Initialize the Language Model Service");
        }
    }

    @Override
    public void reset() {
        var state = getState();

        chatSettingsComponent.getServerTypeComboBox().setItem(state.serverName);
        chatSettingsComponent.getMaxMessages().setValue(state.maxMessages);
        chatSettingsComponent
                .getSystemMessageInstructionsWithCodeField()
                .setText(state.systemMessageInstructionsWithCode);
        chatSettingsComponent
                .getSystemMessageInstructionsField()
                .setText(state.systemMessageInstructions);
    }

    @Override
    public void disposeUIResources() {
        chatSettingsComponent = null;
    }
}

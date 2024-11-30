package com.github.damiano1996.intellijplugin.incoder.language.model.client.chat.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

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

        return !chatSettingsComponent.getMaxMessages().getValue().equals(state.maxMessages) ||
                !chatSettingsComponent.getSystemMessageInstructionsWithCodeField().getText().equals(state.systemMessageInstructionsWithCode)
                || !chatSettingsComponent.getSystemMessageInstructionsField().getText().equals(state.systemMessageInstructions);
    }

    @Override
    public void apply() {
        var state = getState();

        state.maxMessages = (int) chatSettingsComponent.getMaxMessages().getValue();
        state.systemMessageInstructionsWithCode = chatSettingsComponent.getSystemMessageInstructionsWithCodeField().getText();
        state.systemMessageInstructions = chatSettingsComponent.getSystemMessageInstructionsField().getText();

    }

    @Override
    public void reset() {
        var state = getState();

        chatSettingsComponent.getMaxMessages().setValue(state.maxMessages);
        chatSettingsComponent.getSystemMessageInstructionsWithCodeField().setText(state.systemMessageInstructionsWithCode);
        chatSettingsComponent.getSystemMessageInstructionsField().setText(state.systemMessageInstructions);
    }

    @Override
    public void disposeUIResources() {
        chatSettingsComponent = null;
    }
}

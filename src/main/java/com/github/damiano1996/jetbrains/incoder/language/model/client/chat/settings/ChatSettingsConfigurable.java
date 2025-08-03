package com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
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

        return !chatSettingsComponent.getMaxMessages().getValue().equals(state.maxMessages)
                || !chatSettingsComponent
                        .getSystemMessageInstructionsField()
                        .getText()
                        .equals(state.systemMessageInstructions)
                || chatSettingsComponent.getEnableTools().isSelected() != state.enableTools;
    }

    @Override
    public void apply() throws ConfigurationException {
        var state = getState();

        state.maxMessages = (int) chatSettingsComponent.getMaxMessages().getValue();
        state.systemMessageInstructions =
                chatSettingsComponent.getSystemMessageInstructionsField().getText();
        state.enableTools = chatSettingsComponent.getEnableTools().isSelected();
    }

    @Override
    public void reset() {
        var state = getState();

        chatSettingsComponent.getMaxMessages().setValue(state.maxMessages);
        chatSettingsComponent
                .getSystemMessageInstructionsField()
                .setText(state.systemMessageInstructions);
        chatSettingsComponent.getEnableTools().setSelected(state.enableTools);
    }

    @Override
    public void disposeUIResources() {
        chatSettingsComponent = null;
    }
}

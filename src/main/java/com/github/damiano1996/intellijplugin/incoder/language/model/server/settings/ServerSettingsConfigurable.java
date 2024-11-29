package com.github.damiano1996.intellijplugin.incoder.language.model.server.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.LanguageModelServerType;
import com.intellij.openapi.options.Configurable;

import javax.swing.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ServerSettingsConfigurable implements Configurable {

    private ServerSettingsComponent serverSettingsComponent;

    public ServerSettingsConfigurable() {
        serverSettingsComponent = new ServerSettingsComponent();
    }

    private static ServerSettings.@NotNull State getState() {
        return ServerSettings.getInstance().getState();
    }

    @Contract(pure = true)
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getDisplayName() {
        return "Server Settings";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return serverSettingsComponent.getMainPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return serverSettingsComponent.getMainPanel();
    }

    @Override
    public boolean isModified() {
        ServerSettings.State state = getState();

        return !serverSettingsComponent.getModelTypeComboBox().getItem().equals(state.modelType);
    }

    @Override
    public void apply() {
        ServerSettings.State state = getState();

        state.modelType =
                (LanguageModelServerType) serverSettingsComponent.getModelTypeComboBox().getSelectedItem();

    }

    @Override
    public void reset() {
        ServerSettings.State state = getState();

        serverSettingsComponent.getModelTypeComboBox().setSelectedItem(state.modelType);
    }

    @Override
    public void disposeUIResources() {
        serverSettingsComponent = null;
    }
}

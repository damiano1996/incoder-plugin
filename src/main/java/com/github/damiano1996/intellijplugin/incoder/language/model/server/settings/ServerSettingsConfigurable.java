package com.github.damiano1996.intellijplugin.incoder.language.model.server.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.BaseServerConfigurable;
import javax.swing.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ServerSettingsConfigurable extends BaseServerConfigurable {

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
        return "Server";
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

        return !serverSettingsComponent.getServerTypeComboBox().getItem().equals(state.serverName);
    }

    @Override
    public void updateState() {
        ServerSettings.State state = getState();

        state.serverName = serverSettingsComponent.getServerTypeComboBox().getItem();
    }

    @Override
    public void reset() {
        ServerSettings.State state = getState();

        serverSettingsComponent.getServerTypeComboBox().setItem(state.serverName);
    }

    @Override
    public void disposeUIResources() {
        serverSettingsComponent = null;
    }
}

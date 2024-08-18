package com.github.damiano1996.intellijplugin.incoder.llm.server.settings;

import com.intellij.openapi.options.Configurable;
import java.util.Objects;
import javax.swing.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ServerSettingsConfigurable implements Configurable {

    private ServerSettingsComponent serverSettingsComponent;

    public ServerSettingsConfigurable(ServerSettingsComponent serverSettingsComponent) {
        this.serverSettingsComponent = serverSettingsComponent;
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
        @NotNull
        ServerSettings.State state =
                Objects.requireNonNull(ServerSettings.getInstance().getState());
        return !Objects.equals(
                serverSettingsComponent.getServerTypeComboBox().getItem(), state.serverType);
    }

    @Override
    public void apply() {
        @NotNull
        ServerSettings.State state =
                Objects.requireNonNull(ServerSettings.getInstance().getState());
        state.serverType = serverSettingsComponent.getServerTypeComboBox().getItem();
    }

    @Override
    public void reset() {
        @NotNull
        ServerSettings.State state =
                Objects.requireNonNull(ServerSettings.getInstance().getState());
        serverSettingsComponent.getServerTypeComboBox().setSelectedItem(state.serverType);
    }

    @Override
    public void disposeUIResources() {
        serverSettingsComponent = null;
    }
}

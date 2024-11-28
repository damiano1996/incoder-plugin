package com.github.damiano1996.intellijplugin.incoder.language.model.server.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.ServerType;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.UnnamedConfigurable;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ServerSettingsConfigurable implements Configurable {

    private final List<ServerConfigurable> configurableList;

    private ServerSettingsComponent serverSettingsComponent;

    public ServerSettingsConfigurable() {
        configurableList =
                Arrays.stream(ServerType.values())
                        .map(
                                serverType ->
                                        serverType.getServerAbstractFactory().createConfigurable())
                        .toList();
        serverSettingsComponent = new ServerSettingsComponent(configurableList);
    }

    private static ServerSettings.State getState() {
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

        return !serverSettingsComponent.getModelTypeComboBox().getItem().equals(state.modelType)
                || configurableList.stream().anyMatch(UnnamedConfigurable::isModified);
    }

    @Override
    public void apply() throws ConfigurationException {
        ServerSettings.State state = getState();

        state.modelType =
                (ServerType) serverSettingsComponent.getModelTypeComboBox().getSelectedItem();

        for (ServerConfigurable serverSettingsConfigurable : configurableList) {
            try {
                serverSettingsConfigurable.apply();
            } catch (ConfigurationException e) {
                throw new ConfigurationException(
                        "Unable to apply changes. " + e.getMessage(), "Server Settings Error");
            }
        }
    }

    @Override
    public void reset() {
        ServerSettings.State state = getState();

        serverSettingsComponent.getModelTypeComboBox().setSelectedItem(state.modelType);
        configurableList.forEach(UnnamedConfigurable::reset);
    }

    @Override
    public void disposeUIResources() {
        serverSettingsComponent = null;
        configurableList.forEach(UnnamedConfigurable::disposeUIResources);
    }
}

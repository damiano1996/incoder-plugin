package com.github.damiano1996.intellijplugin.incoder.language.model.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.servers.ServerType;
import com.github.damiano1996.intellijplugin.incoder.language.model.servers.ollama.settings.OllamaSettingsConfigurable;
import com.github.damiano1996.intellijplugin.incoder.language.model.servers.openai.settings.OpenAiSettingsConfigurable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.UnnamedConfigurable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public final class ServerSettingsConfigurable implements Configurable {

    private final List<IServerSettingsConfigurable> configurableList;

    private ServerSettingsComponent serverSettingsComponent;

    public ServerSettingsConfigurable() {
        configurableList = List.of(new OllamaSettingsConfigurable(), new OpenAiSettingsConfigurable());
        serverSettingsComponent = new ServerSettingsComponent(configurableList);
    }

    private static @NotNull ServerSettings.State getState() {
        @NotNull
        ServerSettings.State state =
                Objects.requireNonNull(ServerSettings.getInstance().getState());
        return state;
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

        ServerSettings.@NotNull State state = getState();

        return !serverSettingsComponent.getModelTypeComboBox().getItem().equals(state.modelType) ||
                configurableList.stream().anyMatch(UnnamedConfigurable::isModified);
    }

    @Override
    public void apply() {
        @NotNull ServerSettings.State state = getState();

        state.modelType =
                (ServerType)
                        serverSettingsComponent.getModelTypeComboBox().getSelectedItem();

        configurableList.forEach(serverSettingsConfigurable -> {
            try {
                serverSettingsConfigurable.apply();
            } catch (ConfigurationException e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Override
    public void reset() {
        ServerSettings.@NotNull State state = getState();

        serverSettingsComponent.getModelTypeComboBox().setSelectedItem(state.modelType);
        configurableList.forEach(UnnamedConfigurable::reset);

    }

    @Override
    public void disposeUIResources() {
        serverSettingsComponent = null;
        configurableList.forEach(UnnamedConfigurable::disposeUIResources);
    }
}

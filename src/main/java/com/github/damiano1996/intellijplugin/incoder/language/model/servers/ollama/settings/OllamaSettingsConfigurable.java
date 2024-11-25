package com.github.damiano1996.intellijplugin.incoder.language.model.servers.ollama.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.settings.IServerSettingsComponent;
import com.github.damiano1996.intellijplugin.incoder.language.model.settings.IServerSettingsConfigurable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public final class OllamaSettingsConfigurable implements IServerSettingsConfigurable {

    private OllamaSettingsComponent settingsComponent = new OllamaSettingsComponent();

    private static OllamaSettings.@NotNull State getState() {
        @NotNull
        var state =
                Objects.requireNonNull(OllamaSettings.getInstance().getState());
        return state;
    }

    @Contract(pure = true)
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getDisplayName() {
        return "Ollama Settings";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return settingsComponent.getMainPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return settingsComponent.getMainPanel();
    }

    @Override
    public boolean isModified() {
        var state = getState();

        return !settingsComponent.getBaseUrlField().getText().equals(state.baseUrl)
                || !settingsComponent.getModelNameField().getText().equals(state.modelName)
                || !Objects.equals(
                settingsComponent.getTemperatureField().getText(),
                state.temperature != null
                        ? state.temperature
                        : "");
    }

    @Override
    public void apply() {
        var state = getState();

        state.baseUrl = settingsComponent.getBaseUrlField().getText();
        state.modelName = settingsComponent.getModelNameField().getText();

        String temperatureText = settingsComponent.getTemperatureField().getText();
        state.temperature = temperatureText.isEmpty() ? null : Double.parseDouble(temperatureText);
    }

    @Override
    public void reset() {
        var state = getState();

        settingsComponent.getBaseUrlField().setText(state.baseUrl);
        settingsComponent.getModelNameField().setText(state.modelName);
        settingsComponent.getTemperatureField().setText(
                state.temperature != null ? state.temperature.toString() : "");
    }

    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }

    @Override
    public IServerSettingsComponent getComponent() {
        return settingsComponent;
    }
}

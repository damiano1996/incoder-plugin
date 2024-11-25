package com.github.damiano1996.intellijplugin.incoder.language.model.servers.openai.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.settings.IServerSettingsComponent;
import com.github.damiano1996.intellijplugin.incoder.language.model.settings.IServerSettingsConfigurable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public final class OpenAiSettingsConfigurable implements IServerSettingsConfigurable {

    private OpenAiSettingsComponent settingsComponent = new OpenAiSettingsComponent();


    private static OpenAiSettings.@NotNull State getState() {
        @NotNull
        var state =
                Objects.requireNonNull(OpenAiSettings.getInstance().getState());
        return state;
    }

    @Contract(pure = true)
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getDisplayName() {
        return "Open AI Settings";
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

        return !settingsComponent.getApiKeyField().getText().equals(state.apiKey)
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

        state.apiKey = settingsComponent.getApiKeyField().getText();
        state.modelName = settingsComponent.getModelNameField().getText();

        String temperatureText = settingsComponent.getTemperatureField().getText();
        state.temperature = temperatureText.isEmpty() ? null : Double.parseDouble(temperatureText);
    }

    @Override
    public void reset() {
        var state = getState();

        settingsComponent.getApiKeyField().setText(state.apiKey);
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

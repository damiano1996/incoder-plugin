package com.github.damiano1996.intellijplugin.incoder.language.model.server.openai.settings;

import javax.swing.*;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OpenAiConfigurable implements Configurable {

    private OpenAiComponent settingsComponent = new OpenAiComponent();

    private static OpenAiSettings.State getState() {
        return OpenAiSettings.getInstance().getState();
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
                || !settingsComponent.getModelNameField().getItem().equals(state.modelName)
                || !settingsComponent.getTemperatureField().getValue().equals(state.temperature);
    }

    @Override
    public void apply() {
        var state = getState();

        state.apiKey = settingsComponent.getApiKeyField().getText();
        state.modelName = settingsComponent.getModelNameField().getItem();
        state.temperature = (Double) settingsComponent.getTemperatureField().getValue();
    }

    @Override
    public void reset() {
        var state = getState();

        settingsComponent.getApiKeyField().setText(state.apiKey);
        settingsComponent.getModelNameField().setItem(state.modelName);
        settingsComponent.getTemperatureField().setValue(state.temperature);
    }

    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }

}

package com.github.damiano1996.jetbrains.incoder.language.model.server.openai.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;
import javax.swing.*;
import org.jetbrains.annotations.Nullable;

public final class OpenAiSettingsConfigurable implements Configurable {

    private OpenAiSettingsComponent settingsComponent = new OpenAiSettingsComponent();

    private static OpenAiSettings.State getState() {
        return OpenAiSettings.getInstance().getState();
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
                || !new String(settingsComponent.getApiKeyField().getPassword())
                        .equals(state.apiKey)
                || !settingsComponent
                        .getModelNameField()
                        .getEditor()
                        .getItem()
                        .equals(state.modelName)
                || !settingsComponent.getTemperatureField().getValue().equals(state.temperature)
                || !settingsComponent.getMaxTokensField().getValue().equals(state.maxTokens);
    }

    @Override
    public void apply() {
        var state = getState();

        state.baseUrl = settingsComponent.getBaseUrlField().getText();
        state.apiKey = new String(settingsComponent.getApiKeyField().getPassword());
        state.modelName = settingsComponent.getModelNameField().getItem();
        state.temperature = (Double) settingsComponent.getTemperatureField().getValue();
        state.maxTokens = (Integer) settingsComponent.getMaxTokensField().getValue();
    }

    @Override
    public void reset() {
        var state = getState();

        settingsComponent.getBaseUrlField().setText(state.baseUrl);
        settingsComponent.getApiKeyField().setText(state.apiKey);
        settingsComponent.getModelNameField().setItem(state.modelName);
        settingsComponent.getTemperatureField().setValue(state.temperature);
        settingsComponent.getMaxTokensField().setValue(state.maxTokens);
    }

    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Open AI";
    }
}

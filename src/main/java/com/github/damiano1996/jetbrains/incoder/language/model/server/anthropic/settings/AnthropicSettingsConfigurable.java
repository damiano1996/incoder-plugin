package com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;
import javax.swing.*;
import org.jetbrains.annotations.Nullable;

public final class AnthropicSettingsConfigurable implements Configurable {

    private AnthropicSettingsComponent settingsComponent = new AnthropicSettingsComponent();

    private static AnthropicSettings.State getState() {
        return AnthropicSettings.getInstance().getState();
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

        return !new String(settingsComponent.getApiKeyField().getPassword()).equals(state.apiKey)
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

        state.apiKey = new String(settingsComponent.getApiKeyField().getPassword());
        state.modelName = settingsComponent.getModelNameField().getItem();
        state.temperature = (Double) settingsComponent.getTemperatureField().getValue();
        state.maxTokens = (Integer) settingsComponent.getMaxTokensField().getValue();
    }

    @Override
    public void reset() {
        var state = getState();

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
        return "Anthropic";
    }
}

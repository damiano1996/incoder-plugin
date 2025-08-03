package com.github.damiano1996.jetbrains.incoder.language.model.server.ollama.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OllamaSettingsConfigurable implements Configurable {

    private OllamaSettingsComponent settingsComponent = new OllamaSettingsComponent();

    private static OllamaSettings.@NotNull State getState() {
        return OllamaSettings.getInstance().getState();
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
                || !settingsComponent
                        .getModelNameField()
                        .getEditor()
                        .getItem()
                        .equals(state.modelName)
                || !settingsComponent.getTemperatureField().getValue().equals(state.temperature);
    }

    @Override
    public void apply() {
        var state = getState();

        state.baseUrl = settingsComponent.getBaseUrlField().getText();
        state.modelName = settingsComponent.getModelNameField().getItem();
        state.temperature = (Double) settingsComponent.getTemperatureField().getValue();
    }

    @Override
    public void reset() {
        var state = getState();

        settingsComponent.getBaseUrlField().setText(state.baseUrl);
        settingsComponent.getModelNameField().setItem(state.modelName);
        settingsComponent.getTemperatureField().setValue(state.temperature);
    }

    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Ollama";
    }
}

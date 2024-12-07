package com.github.damiano1996.intellijplugin.incoder.language.model.server.ollama.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.BaseServerConfigurable;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.ollama.OllamaLanguageModelServer;
import com.intellij.openapi.options.ConfigurationException;
import javax.swing.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OllamaConfigurable extends BaseServerConfigurable {

    private OllamaComponent settingsComponent = new OllamaComponent();

    private static OllamaSettings.@NotNull State getState() {
        return OllamaSettings.getInstance().getState();
    }

    @Contract(pure = true)
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getDisplayName() {
        return "Ollama";
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
                || !getModelName().equals(state.modelName)
                || !settingsComponent.getTemperatureField().getValue().equals(state.temperature);
    }

    private @NotNull String getModelName() {
        var selectedModelName = settingsComponent.getModelNameField().getItem();
        return (selectedModelName == null) ? "" : selectedModelName;
    }

    @Override
    public void updateState() {
        var state = getState();

        state.baseUrl = settingsComponent.getBaseUrlField().getText();
        state.modelName = getModelName();
        state.temperature = (Double) settingsComponent.getTemperatureField().getValue();
    }

    @Override
    protected void verifySettings() throws ConfigurationException {
        try {
            new OllamaLanguageModelServer().createClient();
        } catch (LanguageModelException e) {
            throw new ConfigurationException(e.getMessage());
        }
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
}

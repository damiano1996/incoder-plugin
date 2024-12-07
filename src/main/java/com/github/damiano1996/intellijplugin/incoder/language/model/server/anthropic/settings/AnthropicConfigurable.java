package com.github.damiano1996.intellijplugin.incoder.language.model.server.anthropic.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.BaseServerConfigurable;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.anthropic.AnthropicLanguageModelServer;
import com.intellij.openapi.options.ConfigurationException;
import javax.swing.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AnthropicConfigurable extends BaseServerConfigurable {

    private AnthropicComponent settingsComponent = new AnthropicComponent();

    private static AnthropicSettings.State getState() {
        return AnthropicSettings.getInstance().getState();
    }

    @Contract(pure = true)
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getDisplayName() {
        return "Anthropic";
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
    public void updateState() {
        var state = getState();

        state.apiKey = settingsComponent.getApiKeyField().getText();
        state.modelName = settingsComponent.getModelNameField().getItem();
        state.temperature = (Double) settingsComponent.getTemperatureField().getValue();
    }

    @Override
    protected void verifySettings() throws ConfigurationException {
        try {
            new AnthropicLanguageModelServer().createClient();
        } catch (LanguageModelException e) {
            throw new ConfigurationException(e.getMessage());
        }
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

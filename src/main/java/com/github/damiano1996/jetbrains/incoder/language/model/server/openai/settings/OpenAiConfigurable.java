package com.github.damiano1996.jetbrains.incoder.language.model.server.openai.settings;

import com.github.damiano1996.jetbrains.incoder.language.model.server.BaseServerConfigurable;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactory;
import com.github.damiano1996.jetbrains.incoder.language.model.server.openai.OpenAiFactory;
import javax.swing.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OpenAiConfigurable extends BaseServerConfigurable {

    private OpenAiComponent settingsComponent = new OpenAiComponent();

    private static OpenAiSettings.State getState() {
        return OpenAiSettings.getInstance().getState();
    }

    @Contract(value = " -> new", pure = true)
    @Override
    protected @NotNull ServerFactory getServerFactory() {
        return new OpenAiFactory();
    }

    @Contract(pure = true)
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getDisplayName() {
        return "Open AI";
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
    public void updateState() {
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
}

package com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic.settings;

import com.github.damiano1996.jetbrains.incoder.language.model.server.BaseServerConfigurable;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactory;
import com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic.AnthropicFactory;
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

    @Contract(value = " -> new", pure = true)
    @Override
    protected @NotNull ServerFactory getServerFactory() {
        return new AnthropicFactory();
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

        return !new String(settingsComponent.getApiKeyField().getPassword()).equals(state.apiKey)
                || !settingsComponent
                        .getModelNameField()
                        .getEditor()
                        .getItem()
                        .equals(state.modelName)
                || !settingsComponent.getTemperatureField().getValue().equals(state.temperature);
    }

    @Override
    public void updateState() {
        var state = getState();

        state.apiKey = new String(settingsComponent.getApiKeyField().getPassword());
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

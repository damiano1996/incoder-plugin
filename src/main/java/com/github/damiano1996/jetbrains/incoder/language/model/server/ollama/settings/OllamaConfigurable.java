package com.github.damiano1996.jetbrains.incoder.language.model.server.ollama.settings;

import com.github.damiano1996.jetbrains.incoder.language.model.server.BaseServerConfigurable;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactory;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ollama.OllamaFactory;
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

    @Contract(value = " -> new", pure = true)
    @Override
    protected @NotNull ServerFactory getServerFactory() {
        return new OllamaFactory();
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
                || !settingsComponent.getModelNameField().getItem().equals(state.modelName)
                || !settingsComponent.getTemperatureField().getValue().equals(state.temperature);
    }

    @Override
    public void updateState() {
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
}

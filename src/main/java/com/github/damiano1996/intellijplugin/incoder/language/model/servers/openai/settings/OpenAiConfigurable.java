package com.github.damiano1996.intellijplugin.incoder.language.model.servers.openai.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.servers.ServerType;
import com.github.damiano1996.intellijplugin.incoder.language.model.settings.ServerComponent;
import com.github.damiano1996.intellijplugin.incoder.language.model.settings.ServerConfigurable;
import java.util.Objects;
import javax.swing.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OpenAiConfigurable implements ServerConfigurable {

    private OpenAiComponent settingsComponent = new OpenAiComponent();

    private static OpenAiSettings.@NotNull State getState() {
        return Objects.requireNonNull(OpenAiSettings.getInstance().getState());
    }

    @Contract(pure = true)
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getDisplayName() {
        return getServerType().getDisplayName();
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
                || !settingsComponent.getTemperatureField().getValue().equals(state.temperature);
    }

    @Override
    public void apply() {
        var state = getState();

        state.apiKey = settingsComponent.getApiKeyField().getText();
        state.modelName = settingsComponent.getModelNameField().getText();
        state.temperature = (Double) settingsComponent.getTemperatureField().getValue();
    }

    @Override
    public void reset() {
        var state = getState();

        settingsComponent.getApiKeyField().setText(state.apiKey);
        settingsComponent.getModelNameField().setText(state.modelName);
        settingsComponent.getTemperatureField().setValue(state.temperature);
    }

    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }

    @Override
    public ServerType getServerType() {
        return ServerType.OPENAI;
    }

    @Override
    public ServerComponent getComponent() {
        return settingsComponent;
    }
}

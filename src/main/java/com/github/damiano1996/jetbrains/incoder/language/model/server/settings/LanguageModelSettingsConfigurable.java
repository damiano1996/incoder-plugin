package com.github.damiano1996.jetbrains.incoder.language.model.server.settings;

import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerSettings;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;
import java.util.ArrayList;
import javax.swing.*;
import org.jetbrains.annotations.Nullable;

public final class LanguageModelSettingsConfigurable implements Configurable {

    private LanguageModelSettingsComponent settingsComponent = new LanguageModelSettingsComponent();

    private static ServerSettings.State getState() {
        return ServerSettings.getInstance().getState();
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
        return !settingsComponent.getConfigurations().equals(state.configuredLanguageModels);
    }

    @Override
    public void apply() {
        var state = getState();

        state.configuredLanguageModels = new ArrayList<>(settingsComponent.getConfigurations());
    }

    @Override
    public void reset() {
        var state = getState();

        settingsComponent.getConfigurations().clear();
        settingsComponent.getConfigurations().addAll(state.configuredLanguageModels);
    }

    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Language Models";
    }
}

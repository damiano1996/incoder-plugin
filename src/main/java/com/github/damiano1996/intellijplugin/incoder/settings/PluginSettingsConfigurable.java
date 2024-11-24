package com.github.damiano1996.intellijplugin.incoder.settings;

import com.github.damiano1996.intellijplugin.incoder.InCoderActivity;
import com.github.damiano1996.intellijplugin.incoder.language.model.langchain.settings.LangChainSettingsComponent;
import com.github.damiano1996.intellijplugin.incoder.language.model.langchain.settings.LangChainSettingsConfigurable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PluginSettingsConfigurable implements Configurable {

    private final List<Configurable> configurables = new ArrayList<>();

    public PluginSettingsConfigurable() {
        LangChainSettingsComponent langChainSettingsComponent = new LangChainSettingsComponent();
        configurables.add(new LangChainSettingsConfigurable(langChainSettingsComponent));
    }

    @Contract(pure = true)
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getDisplayName() {
        return "InCoder Settings";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return configurables.get(0).getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        var formBuilder = FormBuilder.createFormBuilder();
        configurables.forEach(
                configurable ->
                        formBuilder.addComponent(
                                Objects.requireNonNull(configurable.createComponent())));

        return formBuilder.addComponentFillVertically(new JPanel(), 0).getPanel();
    }

    @Override
    public boolean isModified() {
        return configurables.stream().anyMatch(UnnamedConfigurable::isModified);
    }

    @Override
    public void apply() {
        configurables.forEach(
                configurable -> {
                    try {
                        configurable.apply();
                    } catch (ConfigurationException e) {
                        throw new RuntimeException(e);
                    }
                });

        Objects.requireNonNull(PluginSettings.getInstance().getState()).isPluginConfigured = true;
        InCoderActivity.initServices(ProjectManager.getInstance().getDefaultProject());
    }

    @Override
    public void reset() {
        configurables.forEach(UnnamedConfigurable::reset);
    }

    @Override
    public void disposeUIResources() {
        configurables.forEach(UnnamedConfigurable::disposeUIResources);
    }
}

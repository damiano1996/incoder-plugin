package com.github.damiano1996.intellijplugin.incoder.settings;

import com.github.damiano1996.intellijplugin.incoder.InCoderActivity;
import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.settings.ServerSettingsConfigurable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.ui.FormBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PluginSettingsConfigurable implements Configurable {

    private final List<Configurable> configurableList = new ArrayList<>();

    public PluginSettingsConfigurable() {
        configurableList.add(new ServerSettingsConfigurable());
    }

    @Contract(pure = true)
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getDisplayName() {
        return "InCoder Settings";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return configurableList.get(0).getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        var formBuilder = FormBuilder.createFormBuilder();
        configurableList.forEach(
                configurable ->
                        formBuilder.addComponent(
                                Objects.requireNonNull(configurable.createComponent())));

        return formBuilder.addComponentFillVertically(new JPanel(), 0).getPanel();
    }

    @Override
    public boolean isModified() {
        return configurableList.stream().anyMatch(UnnamedConfigurable::isModified);
    }

    @Override
    public void apply() throws ConfigurationException {
        for (Configurable configurable : configurableList) {
            configurable.apply();
        }

        try {
            InCoderActivity.initServices(ProjectManager.getInstance().getDefaultProject());
            PluginSettings.getInstance().getState().isPluginConfigured = true;
        } catch (LanguageModelException e) {
            throw new ConfigurationException(e.getMessage(), "Server Settings Error");
        }
    }

    @Override
    public void reset() {
        configurableList.forEach(UnnamedConfigurable::reset);
    }

    @Override
    public void disposeUIResources() {
        configurableList.forEach(UnnamedConfigurable::disposeUIResources);
    }
}

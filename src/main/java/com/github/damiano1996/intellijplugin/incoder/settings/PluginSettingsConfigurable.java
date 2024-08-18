package com.github.damiano1996.intellijplugin.incoder.settings;

import com.github.damiano1996.intellijplugin.incoder.llm.server.container.settings.ContainerSettingsConfigurable;
import com.github.damiano1996.intellijplugin.incoder.llm.server.settings.ServerSettingsComponent;
import com.github.damiano1996.intellijplugin.incoder.llm.server.settings.ServerSettingsConfigurable;
import com.intellij.openapi.options.Configurable;
import com.intellij.util.ui.FormBuilder;
import java.util.Objects;
import javax.swing.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PluginSettingsConfigurable implements Configurable {

    private final ServerSettingsConfigurable serverSettingsConfigurable;
    private final ContainerSettingsConfigurable containerSettingsConfigurable;

    public PluginSettingsConfigurable() {
        ServerSettingsComponent serverSettingsComponent = new ServerSettingsComponent();
        serverSettingsConfigurable = new ServerSettingsConfigurable(serverSettingsComponent);
        containerSettingsConfigurable =
                new ContainerSettingsConfigurable(
                        serverSettingsComponent.getContainerSettingsComponent());
    }

    @Contract(pure = true)
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getDisplayName() {
        return "InCoder Settings";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return serverSettingsConfigurable.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        var serverSettingsConfigurableComponent =
                Objects.requireNonNull(serverSettingsConfigurable.createComponent());
        var containerSettingsConfigurableComponent =
                Objects.requireNonNull(containerSettingsConfigurable.createComponent());

        return FormBuilder.createFormBuilder()
                .addComponent(serverSettingsConfigurableComponent)
                .addComponent(containerSettingsConfigurableComponent)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    @Override
    public boolean isModified() {
        return serverSettingsConfigurable.isModified()
                || containerSettingsConfigurable.isModified();
    }

    @Override
    public void apply() {
        serverSettingsConfigurable.apply();
        containerSettingsConfigurable.apply();
    }

    @Override
    public void reset() {
        serverSettingsConfigurable.reset();
        containerSettingsConfigurable.reset();
    }

    @Override
    public void disposeUIResources() {
        serverSettingsConfigurable.disposeUIResources();
        containerSettingsConfigurable.disposeUIResources();
    }
}

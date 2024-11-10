package com.github.damiano1996.intellijplugin.incoder.llm.container.server.settings;

import com.intellij.openapi.options.Configurable;
import java.util.Objects;
import javax.swing.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ContainerSettingsConfigurable implements Configurable {

    private ContainerSettingsComponent containerSettingsComponent;

    public ContainerSettingsConfigurable(ContainerSettingsComponent containerSettingsComponent) {
        this.containerSettingsComponent = containerSettingsComponent;
    }

    @Contract(pure = true)
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getDisplayName() {
        return "Container Settings";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return containerSettingsComponent.getMainPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return containerSettingsComponent.getMainPanel();
    }

    @Override
    public boolean isModified() {
        @NotNull
        ContainerSettings.State state =
                Objects.requireNonNull(ContainerSettings.getInstance().getState());

        return !containerSettingsComponent
                        .getHuggingFaceHomeField()
                        .getText()
                        .equals(state.huggingFace.home)
                || !containerSettingsComponent
                        .getHuggingFaceHubCacheField()
                        .getText()
                        .equals(state.huggingFace.hubCache)
                || containerSettingsComponent.getHuggingFaceModelComboBox().getItem()
                        != state.huggingFace.model
                || !containerSettingsComponent
                        .getLocalHostPortNumberField()
                        .getText()
                        .equals(String.valueOf(state.localHostPortNumber))
                || containerSettingsComponent.getUseCudaCheckBox().isSelected() != state.cuda;
    }

    @Override
    public void apply() {
        @NotNull
        ContainerSettings.State state =
                Objects.requireNonNull(ContainerSettings.getInstance().getState());

        state.huggingFace.home = containerSettingsComponent.getHuggingFaceHomeField().getText();
        state.huggingFace.hubCache =
                containerSettingsComponent.getHuggingFaceHubCacheField().getText();
        state.huggingFace.model =
                containerSettingsComponent.getHuggingFaceModelComboBox().getItem();

        state.localHostPortNumber =
                Integer.parseInt(
                        containerSettingsComponent.getLocalHostPortNumberField().getText());
        state.cuda = containerSettingsComponent.getUseCudaCheckBox().isSelected();
    }

    @Override
    public void reset() {
        @NotNull
        ContainerSettings.State state =
                Objects.requireNonNull(ContainerSettings.getInstance().getState());

        containerSettingsComponent
                .getOrchestratorTypeComboBox()
                .setSelectedItem(state.orchestratorType);

        containerSettingsComponent
                .getLocalHostPortNumberField()
                .setText(String.valueOf(state.localHostPortNumber));

        containerSettingsComponent.getHuggingFaceHomeField().setText(state.huggingFace.home);
        containerSettingsComponent
                .getHuggingFaceHubCacheField()
                .setText(state.huggingFace.hubCache);
        containerSettingsComponent.getHuggingFaceModelComboBox().setItem(state.huggingFace.model);

        containerSettingsComponent.getUseCudaCheckBox().setSelected(state.cuda);
    }

    @Override
    public void disposeUIResources() {
        containerSettingsComponent = null;
    }
}

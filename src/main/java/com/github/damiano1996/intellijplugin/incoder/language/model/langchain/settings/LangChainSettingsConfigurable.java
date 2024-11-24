package com.github.damiano1996.intellijplugin.incoder.language.model.langchain.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.langchain.LangChainModelType;
import com.intellij.openapi.options.Configurable;
import java.util.Objects;
import javax.swing.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LangChainSettingsConfigurable implements Configurable {

    private LangChainSettingsComponent langChainSettingsComponent;

    public LangChainSettingsConfigurable(LangChainSettingsComponent langChainSettingsComponent) {
        this.langChainSettingsComponent = langChainSettingsComponent;
    }

    @Contract(pure = true)
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getDisplayName() {
        return "LangChain Settings";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return langChainSettingsComponent.getMainPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return langChainSettingsComponent.getMainPanel();
    }

    @Override
    public boolean isModified() {
        @NotNull
        LangChainSettings.State state =
                Objects.requireNonNull(LangChainSettings.getInstance().getState());

        boolean modified =
                !langChainSettingsComponent
                        .getModelTypeComboBox()
                        .getItem()
                        .equals(state.modelType);

        if (state.modelType.equals(LangChainModelType.OLLAMA)) {
            modified |=
                    !langChainSettingsComponent
                                    .getOllamaBaseUrlField()
                                    .getText()
                                    .equals(state.ollamaState.baseUrl)
                            || !langChainSettingsComponent
                                    .getOllamaModelNameField()
                                    .getText()
                                    .equals(state.ollamaState.modelName)
                            || !Objects.equals(
                                    langChainSettingsComponent
                                            .getOllamaTemperatureField()
                                            .getText(),
                                    state.ollamaState.temperature != null
                                            ? state.ollamaState.temperature.toString()
                                            : "");
        }

        return modified;
    }

    @Override
    public void apply() {
        @NotNull
        LangChainSettings.State state =
                Objects.requireNonNull(LangChainSettings.getInstance().getState());

        state.modelType =
                (LangChainModelType)
                        langChainSettingsComponent.getModelTypeComboBox().getSelectedItem();

        if (state.modelType == LangChainModelType.OLLAMA) {
            state.ollamaState.baseUrl =
                    langChainSettingsComponent.getOllamaBaseUrlField().getText();
            state.ollamaState.modelName =
                    langChainSettingsComponent.getOllamaModelNameField().getText();

            String temperatureText =
                    langChainSettingsComponent.getOllamaTemperatureField().getText();
            state.ollamaState.temperature =
                    temperatureText.isEmpty() ? null : Double.parseDouble(temperatureText);
        }
    }

    @Override
    public void reset() {
        @NotNull
        LangChainSettings.State state =
                Objects.requireNonNull(LangChainSettings.getInstance().getState());

        langChainSettingsComponent.getModelTypeComboBox().setSelectedItem(state.modelType);

        if (state.modelType == LangChainModelType.OLLAMA) {
            langChainSettingsComponent.getOllamaBaseUrlField().setText(state.ollamaState.baseUrl);
            langChainSettingsComponent
                    .getOllamaModelNameField()
                    .setText(state.ollamaState.modelName);
            langChainSettingsComponent
                    .getOllamaTemperatureField()
                    .setText(
                            state.ollamaState.temperature != null
                                    ? state.ollamaState.temperature.toString()
                                    : "");
        }
    }

    @Override
    public void disposeUIResources() {
        langChainSettingsComponent = null;
    }
}

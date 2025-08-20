package com.github.damiano1996.jetbrains.incoder.language.model.client.inline.settings;

import com.github.damiano1996.jetbrains.incoder.completion.CodeCompletionProjectService;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelProjectService;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import java.util.Objects;
import javax.swing.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class InlineSettingsConfigurable implements Configurable {

    private InlineSettingsComponent inlineSettingsComponent = new InlineSettingsComponent();

    private static InlineSettings.State getState() {
        return InlineSettings.getInstance().getState();
    }

    @Contract(pure = true)
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getDisplayName() {
        return "Inline";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return inlineSettingsComponent.getMainPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return inlineSettingsComponent.getMainPanel();
    }

    @Override
    public boolean isModified() {
        var state = getState();

        LanguageModelParameters languageModelParameters =
                inlineSettingsComponent.getLanguageModelParametersComboBox().getItem();

        return inlineSettingsComponent.getEnableCheckbox().isSelected() != state.enable
                || inlineSettingsComponent.getEndLineCheckBox().isSelected() != state.triggerEndLine
                || !inlineSettingsComponent
                        .getSystemMessageInstructionsField()
                        .getText()
                        .equals(state.systemMessageInstructions)
                || (languageModelParameters != null
                        && !languageModelParameters.equals(
                                state.getSelectedLanguageModelParameters()));
    }

    @Override
    public void apply() throws ConfigurationException {
        var state = getState();

        state.enable = inlineSettingsComponent.getEnableCheckbox().isSelected();
        state.triggerEndLine = inlineSettingsComponent.getEndLineCheckBox().isSelected();
        state.systemMessageInstructions =
                inlineSettingsComponent.getSystemMessageInstructionsField().getText();
        state.setSelectedLanguageModelParameters(
                inlineSettingsComponent.getLanguageModelParametersComboBox().getItem());

        try {
            Project project = Objects.requireNonNull(ProjectUtil.getActiveProject());
            CodeCompletionProjectService.getInstance(project)
                    .setInlineLanguageModelClient(
                            LanguageModelProjectService.getInstance(project)
                                    .createInlineClient(
                                            state, state.getSelectedLanguageModelParameters())
                                    .compute());
        } catch (NullPointerException | LanguageModelException e) {
            throw new ConfigurationException(
                    "Unable to create inline client. Error: " + e.getMessage());
        }
    }

    @Override
    public void reset() {
        var state = getState();

        inlineSettingsComponent.getEnableCheckbox().setSelected(state.enable);
        inlineSettingsComponent.getEndLineCheckBox().setSelected(state.triggerEndLine);
        inlineSettingsComponent
                .getSystemMessageInstructionsField()
                .setText(state.systemMessageInstructions);
        inlineSettingsComponent
                .getLanguageModelParametersComboBox()
                .setSelectedItem(state.getSelectedLanguageModelParameters());
    }

    @Override
    public void disposeUIResources() {
        inlineSettingsComponent = null;
    }
}

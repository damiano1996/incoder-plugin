package com.github.damiano1996.jetbrains.incoder.language.model.client.inline.settings;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelProjectService;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
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

        return inlineSettingsComponent.getEnableCheckbox().isSelected() != state.enable
                || inlineSettingsComponent.getEndLineCheckBox().isSelected() != state.triggerEndLine
                || !inlineSettingsComponent
                        .getSystemMessageInstructionsField()
                        .getText()
                        .equals(state.systemMessageInstructions);
        //                || !inlineSettingsComponent
        //                        .getServerNamesComboBox()
        //                        .getItem()
        //                        .equals(state.selectedLanguageModelParameters.serverName());
    }

    @Override
    public void apply() throws ConfigurationException {
        var state = getState();

        state.enable = inlineSettingsComponent.getEnableCheckbox().isSelected();
        state.triggerEndLine = inlineSettingsComponent.getEndLineCheckBox().isSelected();
        state.systemMessageInstructions =
                inlineSettingsComponent.getSystemMessageInstructionsField().getText();
        //        state.serverName = inlineSettingsComponent.getServerNamesComboBox().getItem();

        try {
            LanguageModelProjectService.getInstance(
                            Objects.requireNonNull(ProjectUtil.getActiveProject()))
                    .with(state);
        } catch (NullPointerException e) {
            throw new ConfigurationException("Unable to verify settings.");
        } catch (LanguageModelException e) {
            throw new ConfigurationException(e.getMessage());
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
        //
        // inlineSettingsComponent.getServerNamesComboBox().setSelectedItem(state.serverName);
    }

    @Override
    public void disposeUIResources() {
        inlineSettingsComponent = null;
    }
}

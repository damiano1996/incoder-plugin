package com.github.damiano1996.jetbrains.incoder.language.model.client.inline.settings;

import com.intellij.openapi.options.Configurable;
import javax.swing.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class InlineConfigurable implements Configurable {

    private InlineComponent inlineComponent = new InlineComponent();

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
        return inlineComponent.getMainPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return inlineComponent.getMainPanel();
    }

    @Override
    public boolean isModified() {
        var state = getState();

        return inlineComponent.getEnableCheckbox().isSelected() != state.enable
                || inlineComponent.getEndLineCheckBox().isSelected() != state.triggerEndLine
                || !inlineComponent
                        .getSystemMessageInstructionsField()
                        .getText()
                        .equals(state.systemMessageInstructions);
    }

    @Override
    public void apply() {
        var state = getState();

        state.enable = inlineComponent.getEnableCheckbox().isSelected();
        state.triggerEndLine = inlineComponent.getEndLineCheckBox().isSelected();
        state.systemMessageInstructions =
                inlineComponent.getSystemMessageInstructionsField().getText();
    }

    @Override
    public void reset() {
        var state = getState();

        inlineComponent.getEnableCheckbox().setSelected(state.enable);
        inlineComponent.getEndLineCheckBox().setSelected(state.triggerEndLine);
        inlineComponent
                .getSystemMessageInstructionsField()
                .setText(state.systemMessageInstructions);
    }

    @Override
    public void disposeUIResources() {
        inlineComponent = null;
    }
}

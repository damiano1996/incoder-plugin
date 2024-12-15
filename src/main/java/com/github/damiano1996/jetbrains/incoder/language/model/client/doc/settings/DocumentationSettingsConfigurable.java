package com.github.damiano1996.jetbrains.incoder.language.model.client.doc.settings;

import com.intellij.openapi.options.Configurable;
import javax.swing.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DocumentationSettingsConfigurable implements Configurable {

    private DocumentationSettingsComponent documentationSettingsComponent;

    public DocumentationSettingsConfigurable() {
        documentationSettingsComponent = new DocumentationSettingsComponent();
    }

    private static DocumentationSettings.@NotNull State getState() {
        return DocumentationSettings.getInstance().getState();
    }

    @Contract(pure = true)
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getDisplayName() {
        return "Documentation";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return documentationSettingsComponent.getMainPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return documentationSettingsComponent.getMainPanel();
    }

    @Override
    public boolean isModified() {
        var state = getState();

        return !documentationSettingsComponent
                .getDocumentationInstructionsTextArea()
                .getText()
                .equals(state.documentationInstructions);
    }

    @Override
    public void apply() {
        var state = getState();

        state.documentationInstructions =
                documentationSettingsComponent.getDocumentationInstructionsTextArea().getText();
    }

    @Override
    public void reset() {
        var state = getState();

        documentationSettingsComponent
                .getDocumentationInstructionsTextArea()
                .setText(state.documentationInstructions);
    }

    @Override
    public void disposeUIResources() {
        documentationSettingsComponent = null;
    }
}

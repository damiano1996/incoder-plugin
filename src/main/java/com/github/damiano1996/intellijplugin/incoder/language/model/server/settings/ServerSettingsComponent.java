package com.github.damiano1996.intellijplugin.incoder.language.model.server.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.LanguageModelServerType;
import com.github.damiano1996.intellijplugin.incoder.settings.description.label.DescriptionLabel;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import lombok.Getter;

import javax.swing.*;

import static com.github.damiano1996.intellijplugin.incoder.language.model.server.LanguageModelServerType.OLLAMA;

@Getter
public class ServerSettingsComponent {

    private final JPanel mainPanel;
    private final ComboBox<LanguageModelServerType> modelTypeComboBox = new ComboBox<>(LanguageModelServerType.values());

    public ServerSettingsComponent() {
        mainPanel =
                FormBuilder.createFormBuilder()
                        .setFormLeftIndent(20)
                        .addLabeledComponent(
                                new JBLabel("Server type:"), modelTypeComboBox, 1, false)
                        .addComponent(new DescriptionLabel("Choose the server type InCoder will use to interact with language models."))
                        .setFormLeftIndent(0)
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();

        modelTypeComboBox.setSelectedItem(OLLAMA);
    }
}

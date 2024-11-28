package com.github.damiano1996.intellijplugin.incoder.language.model.server.anthropic.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.anthropic.AnthropicLanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.settings.ServerComponent;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;
import lombok.Getter;

@Getter
public class AnthropicComponent implements ServerComponent {

    private final JPanel mainPanel;
    private final JBTextField apiKeyField = new JBTextField();
    private final ComboBox<String> modelNameField;
    private final JSpinner temperatureField;

    public AnthropicComponent() {
        SpinnerNumberModel temperatureModel = new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1);
        temperatureField = new JSpinner(temperatureModel);

        try {
            modelNameField = new ComboBox<>(new AnthropicLanguageModelServer().getAvailableModels().toArray(new String[0]));
        } catch (LanguageModelException e) {
            throw new RuntimeException(e);
        }

        mainPanel =
                FormBuilder.createFormBuilder()
                        .addComponent(new TitledSeparator("Anthropic"))
                        .setFormLeftIndent(40)
                        .addLabeledComponent(new JBLabel("Api key:"), apiKeyField, 1, false)
                        .addLabeledComponent(new JBLabel("Model name:"), modelNameField, 1, false)
                        .addLabeledComponent(
                                new JBLabel("Temperature:"), temperatureField, 1, false)
                        .setFormLeftIndent(20)
                        .addVerticalGap(5)
                        .getPanel();
    }
}

package com.github.damiano1996.intellijplugin.incoder.language.model.server.openai.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.openai.OpenAiLanguageModelServer;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;
import lombok.Getter;

@Getter
public class OpenAiComponent {

    private final JPanel mainPanel;
    private final JBTextField apiKeyField = new JBTextField();
    private final ComboBox<String> modelNameField;
    private final JSpinner temperatureField;

    public OpenAiComponent() {
        SpinnerNumberModel temperatureModel = new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1);
        temperatureField = new JSpinner(temperatureModel);

        modelNameField =
                new ComboBox<>(
                        new OpenAiLanguageModelServer()
                                .getAvailableModels()
                                .toArray(new String[0]));

        mainPanel =
                FormBuilder.createFormBuilder()
                        .setFormLeftIndent(20)
                        .addLabeledComponent(new JBLabel("Api key:"), apiKeyField, 1, false)
                        .addLabeledComponent(new JBLabel("Model name:"), modelNameField, 1, false)
                        .addLabeledComponent(
                                new JBLabel("Temperature:"), temperatureField, 1, false)
                        .setFormLeftIndent(0)
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();
    }
}

package com.github.damiano1996.jetbrains.incoder.language.model.server.openai.settings;

import com.github.damiano1996.jetbrains.incoder.language.model.server.openai.OpenAiLanguageModelServer;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;

@Getter
public class OpenAiSettingsComponent {

    private final JPanel mainPanel;
    private final JBTextField baseUrlField;
    private final JPasswordField apiKeyField;
    private final ComboBox<String> modelNameField;
    private final JSpinner temperatureField;
    private final JSpinner maxTokensField;

    public OpenAiSettingsComponent() {
        baseUrlField = new JBTextField();
        baseUrlField.setColumns(40);

        apiKeyField = new JPasswordField();
        apiKeyField.setColumns(40);

        SpinnerNumberModel temperatureModel = new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1);
        temperatureField = new JSpinner(temperatureModel);

        SpinnerNumberModel maxTokensModel = new SpinnerNumberModel(2048, 100, 100000, 1);
        maxTokensField = new JSpinner(maxTokensModel);

        modelNameField =
                new ComboBox<>(
                        new OpenAiLanguageModelServer()
                                .getAvailableModels()
                                .toArray(new String[0]));
        modelNameField.setEditable(true);
        modelNameField.setPreferredSize(new Dimension(300, 30));

        mainPanel =
                FormBuilder.createFormBuilder()
                        .setFormLeftIndent(20)
                        .addLabeledComponent(new JBLabel("Base URL:"), baseUrlField, 1, false)
                        .addLabeledComponent(new JBLabel("Api key:"), apiKeyField, 1, false)
                        .addLabeledComponent(new JBLabel("Model name:"), modelNameField, 1, false)
                        .addLabeledComponent(
                                new JBLabel("Temperature:"), temperatureField, 1, false)
                        .addLabeledComponent(new JBLabel("Max tokens:"), maxTokensField, 1, false)
                        .setFormLeftIndent(0)
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();
    }
}

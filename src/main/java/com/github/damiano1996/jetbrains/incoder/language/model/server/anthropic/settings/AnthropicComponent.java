package com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic.settings;

import com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic.AnthropicLanguageModelServer;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;

@Getter
public class AnthropicComponent {

    private final JPanel mainPanel;
    private final JPasswordField apiKeyField;
    private final ComboBox<String> modelNameField;
    private final JSpinner temperatureField;

    public AnthropicComponent() {
        apiKeyField = new JPasswordField();
        apiKeyField.setColumns(60);

        SpinnerNumberModel temperatureModel = new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1);
        temperatureField = new JSpinner(temperatureModel);

        modelNameField =
                new ComboBox<>(
                        new AnthropicLanguageModelServer()
                                .getAvailableModels()
                                .toArray(new String[0]));
        modelNameField.setPreferredSize(new Dimension(300, 30));

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

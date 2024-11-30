package com.github.damiano1996.intellijplugin.incoder.language.model.server.ollama.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.ollama.OllamaLanguageModelServer;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import java.util.List;
import javax.swing.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class OllamaComponent {

    private final JPanel mainPanel;
    private final JBTextField baseUrlField = new JBTextField();
    private final ComboBox<String> modelNameField;
    private final JSpinner temperatureField;

    public OllamaComponent() {
        SpinnerNumberModel temperatureModel = new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1);
        temperatureField = new JSpinner(temperatureModel);

        modelNameField =
                new ComboBox<>(
                        new OllamaLanguageModelServer()
                                .getAvailableModels()
                                .toArray(new String[0]));

        baseUrlField.addActionListener(
                e -> {
                    List<String> availableModels =
                            new OllamaLanguageModelServer()
                                    .getAvailableModels(baseUrlField.getText());
                    modelNameField.removeAllItems();
                    availableModels.forEach(modelNameField::addItem);
                });

        mainPanel =
                FormBuilder.createFormBuilder()
                        .setFormLeftIndent(20)
                        .addLabeledComponent(new JBLabel("Base URL:"), baseUrlField, 1, false)
                        .addLabeledComponent(new JBLabel("Model name:"), modelNameField, 1, false)
                        .addLabeledComponent(
                                new JBLabel("Temperature:"), temperatureField, 1, false)
                        .setFormLeftIndent(0)
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();
    }
}

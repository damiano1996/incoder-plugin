package com.github.damiano1996.intellijplugin.incoder.language.model.server.ollama.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.ollama.OllamaLanguageModelServer;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import java.awt.*;
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
    private final JButton refreshButton;

    public OllamaComponent() {
        SpinnerNumberModel temperatureModel = new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1);
        temperatureField = new JSpinner(temperatureModel);

        modelNameField =
                new ComboBox<>(
                        new OllamaLanguageModelServer()
                                .getAvailableModels()
                                .toArray(new String[0]));
        modelNameField.setPreferredSize(new Dimension(300, 30));

        refreshButton = new JButton(AllIcons.Actions.Refresh);
        refreshButton.setPreferredSize(new Dimension(30, 30));

        refreshButton.addActionListener(
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
                        .addLabeledComponent(
                                new JBLabel("Model name:"),
                                new JPanel() {
                                    {
                                        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
                                        add(modelNameField);
                                        add(refreshButton);
                                    }
                                },
                                1,
                                false)
                        .addLabeledComponent(
                                new JBLabel("Temperature:"), temperatureField, 1, false)
                        .setFormLeftIndent(0)
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();
    }
}

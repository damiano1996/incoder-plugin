package com.github.damiano1996.intellijplugin.incoder.language.model.server.ollama.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.anthropic.AnthropicLanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.ollama.OllamaLanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.settings.ServerComponent;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;
import lombok.Getter;

@Getter
public class OllamaComponent implements ServerComponent {

    private final JPanel mainPanel;
    private final JBTextField baseUrlField = new JBTextField();
    private final ComboBox<String> modelNameField;
    private final JSpinner temperatureField;

    public OllamaComponent() {
        SpinnerNumberModel temperatureModel = new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1);
        temperatureField = new JSpinner(temperatureModel);

        try {
            modelNameField = new ComboBox<>(new OllamaLanguageModelServer().getAvailableModels().toArray(new String[0]));
        } catch (LanguageModelException e) {
            throw new RuntimeException(e);
        }

        mainPanel =
                FormBuilder.createFormBuilder()
                        .addComponent(new TitledSeparator("Ollama"))
                        .setFormLeftIndent(40)
                        .addLabeledComponent(new JBLabel("Base URL:"), baseUrlField, 1, false)
                        .addLabeledComponent(new JBLabel("Model name:"), modelNameField, 1, false)
                        .addLabeledComponent(
                                new JBLabel("Temperature:"), temperatureField, 1, false)
                        .setFormLeftIndent(20)
                        .addVerticalGap(5)
                        .getPanel();
    }
}

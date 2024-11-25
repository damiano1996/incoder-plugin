package com.github.damiano1996.intellijplugin.incoder.language.model.servers.ollama.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.settings.ServerComponent;
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
    private final JBTextField modelNameField = new JBTextField();
    private final JSpinner temperatureField;

    public OllamaComponent() {
        SpinnerNumberModel temperatureModel = new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1);
        temperatureField = new JSpinner(temperatureModel);

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

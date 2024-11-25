package com.github.damiano1996.intellijplugin.incoder.language.model.servers.ollama.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.settings.IServerSettingsComponent;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import lombok.Getter;

import javax.swing.*;

@Getter
public class OllamaSettingsComponent implements IServerSettingsComponent {

    private final JPanel mainPanel;
    private final JBTextField baseUrlField = new JBTextField();
    private final JBTextField modelNameField = new JBTextField();
    private final JBTextField temperatureField = new JBTextField();


    public OllamaSettingsComponent() {
        mainPanel =
                FormBuilder.createFormBuilder()
                        .addComponent(new TitledSeparator("Ollama"))
                        .setFormLeftIndent(40)
                        .addLabeledComponent(new JBLabel("Base URL:"), baseUrlField, 1, false)
                        .addLabeledComponent(
                                new JBLabel("Model name:"), modelNameField, 1, false)
                        .addLabeledComponent(
                                new JBLabel("Temperature:"), temperatureField, 1, false)
                        .setFormLeftIndent(20)
                        .addVerticalGap(5)
                        .getPanel();
    }
}

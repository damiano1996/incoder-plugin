package com.github.damiano1996.intellijplugin.incoder.language.model.servers.openai.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.settings.IServerSettingsComponent;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import lombok.Getter;

import javax.swing.*;

@Getter
public class OpenAiSettingsComponent implements IServerSettingsComponent {

    private final JPanel mainPanel;
    private final JBTextField apiKeyField = new JBTextField();
    private final JBTextField modelNameField = new JBTextField();
    private final JBTextField temperatureField = new JBTextField();

    public OpenAiSettingsComponent() {
        mainPanel =
                FormBuilder.createFormBuilder()
                        .addComponent(new TitledSeparator("Open AI"))
                        .setFormLeftIndent(40)
                        .addLabeledComponent(new JBLabel("Api key:"), apiKeyField, 1, false)
                        .addLabeledComponent(
                                new JBLabel("Model name:"), modelNameField, 1, false)
                        .addLabeledComponent(
                                new JBLabel("Temperature:"), temperatureField, 1, false)
                        .setFormLeftIndent(20)
                        .addVerticalGap(5)
                        .getPanel();
    }
}

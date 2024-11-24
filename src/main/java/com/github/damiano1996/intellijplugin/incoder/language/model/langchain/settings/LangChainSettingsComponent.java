package com.github.damiano1996.intellijplugin.incoder.language.model.langchain.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.langchain.LangChainModelType;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import lombok.Getter;

import javax.swing.*;

import static com.github.damiano1996.intellijplugin.incoder.language.model.langchain.LangChainModelType.OLLAMA;

@Getter
public class LangChainSettingsComponent {

    private final JPanel mainPanel;

    private final ComboBox<LangChainModelType> modelTypeComboBox =
            new ComboBox<>(LangChainModelType.values());

    private final JPanel ollamaPanel;

    private final JBTextField ollamaBaseUrlField = new JBTextField();
    private final JBTextField ollamaModelNameField = new JBTextField();
    private final JBTextField ollamaTemperatureField = new JBTextField();

    public LangChainSettingsComponent() {
        ollamaPanel =
                FormBuilder.createFormBuilder()
                        .addComponent(new TitledSeparator("Ollama"))
                        .setFormLeftIndent(40)
                        .addLabeledComponent(new JBLabel("Base URL:"), ollamaBaseUrlField, 1, false)
                        .addLabeledComponent(
                                new JBLabel("Model name:"), ollamaModelNameField, 1, false)
                        .addLabeledComponent(
                                new JBLabel("Temperature:"), ollamaTemperatureField, 1, false)
                        .setFormLeftIndent(20)
                        .addVerticalGap(5)
                        .getPanel();

        mainPanel =
                FormBuilder.createFormBuilder()
                        .addComponent(new TitledSeparator("LangChain Settings"))
                        .setVerticalGap(5)
                        .setFormLeftIndent(20)
                        .addLabeledComponent(
                                new JBLabel("Model type:"), modelTypeComboBox, 1, false)
                        .addVerticalGap(5)
                        // Ollama
                        .addComponent(ollamaPanel)
                        .setFormLeftIndent(0)
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();

        modelTypeComboBox.addItemListener(
                e -> {
                    ollamaPanel.setVisible(e.getItem().equals(OLLAMA));
                    mainPanel.revalidate();
                    mainPanel.repaint();
                });
    }
}

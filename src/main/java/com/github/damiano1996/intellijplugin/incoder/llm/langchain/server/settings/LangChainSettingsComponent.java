package com.github.damiano1996.intellijplugin.incoder.llm.langchain.server.settings;

import static com.github.damiano1996.intellijplugin.incoder.llm.langchain.server.LangChainModelType.OLLAMA;

import com.github.damiano1996.intellijplugin.incoder.llm.langchain.server.LangChainModelType;
import com.github.damiano1996.intellijplugin.incoder.llm.server.settings.CustomServerSettingsComponent;
import com.github.damiano1996.intellijplugin.incoder.llm.server.settings.ServerSettings;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;
import lombok.Getter;

@Getter
public class LangChainSettingsComponent implements CustomServerSettingsComponent {

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

    @Override
    public ServerSettings.State.ServerType getServerType() {
        return ServerSettings.State.ServerType.LANG_CHAIN_4J;
    }

    @Override
    public Configurable getConfigurable() {
        return new LangChainSettingsConfigurable(this);
    }
}
